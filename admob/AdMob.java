/**
 * Copyright 2017 FrogSquare. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package org.godotengine.godot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.reward.RewardItem;

import com.godot.game.BuildConfig;
import com.godot.game.R;

import org.godotengine.godot.Godot;
import org.godotengine.godot.Utils;

import org.json.JSONObject;
import org.json.JSONException;

import org.godotengine.godot.Dictionary;

public class AdMob extends Godot.SingletonBase {

	static public Godot.SingletonBase initialize (Activity p_activity) {
		return new AdMob(p_activity);
	}

	public AdMob(Activity p_activity) {
		activity = p_activity;

		registerClass ("AdMob", new String[] {
		"init", "show_banner_ad", "show_interstitial_ad", "show_rewarded_video", "get_banner_size", "is_rewarded_video_loaded"
		});
	}

	public void init (final Dictionary p_dict, final int p_script_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				_config = new JSONObject(p_dict);
				_init();

				Utils.setScriptInstance(p_script_id);
				Utils.d("GodotAds", "AdMob::Initialized");
			}
		});
	}

	private void _init() {
        mAdSize = new Dictionary();
        mAdSize.put("width", 0);
        mAdSize.put("height", 0);

		if (_config.optBoolean("BannerAd", false)) {
			createBanner();
		}

		if (_config.optBoolean("InterstitialAd", false)) {
			createInterstitial();
		}

		if (_config.optBoolean("RewardedVideoAd", false)) {
			createRewardedVideo();
		}
	}

	public void createBanner() {
        RelativeLayout adLayout = new RelativeLayout(activity);
        adLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        ((Godot)activity).layout.addView(adLayout);

		FrameLayout.LayoutParams AdParams = new FrameLayout.LayoutParams(
						 FrameLayout.LayoutParams.MATCH_PARENT,
						 FrameLayout.LayoutParams.WRAP_CONTENT);

		if(mAdView != null) { adLayout.removeView(mAdView); }

		if (_config.optString("BannerGravity", "BOTTOM").equals("BOTTOM")) {
			AdParams.gravity = Gravity.BOTTOM;
		} else { AdParams.gravity = Gravity.TOP; }

		AdRequest.Builder adRequestB = new AdRequest.Builder();
		adRequestB.tagForChildDirectedTreatment(true);

		// Show test ads if the build flaged debug
		if (BuildConfig.DEBUG) {
			adRequestB.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
			adRequestB.addTestDevice(Utils.getDeviceId(activity));
		}

		AdRequest adRequest = adRequestB.build();

		String ad_unit_id = _config.optString("BannerAdId", "");

		if (ad_unit_id.length() <= 0) {
			Utils.d("GodotAds", "AdMob:Banner:UnitId:NotProvided");
			ad_unit_id = activity.getString(R.string.gads_banner_ad_unit_id);
		}

		mAdView = new AdView(activity);
		mAdView.setBackgroundColor(Color.TRANSPARENT);
		mAdView.setAdUnitId(ad_unit_id);
		mAdView.setAdSize(AdSize.SMART_BANNER);

		mAdView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				Utils.d("GodotAds", "AdMob:Banner:OnAdLoaded");
                AdSize adSize = mAdView.getAdSize();

                mAdSize.put("width", adSize.getWidthInPixels(activity));
                mAdSize.put("height", adSize.getHeightInPixels(activity));

				Utils.callScriptFunc("AdMob", "AdMob_Banner", "loaded");
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				Utils.w("GodotAds", "AdMob:Banner:onAdFailedToLoad:" + errorCode);
				Utils.callScriptFunc("AdMob", "AdMob_Banner", "load_failed");
			}
		});

		mAdView.setVisibility(View.INVISIBLE);
		mAdView.loadAd(adRequest);

		adLayout.addView(mAdView, AdParams);
	}

	public void createInterstitial() {
		String ad_unit_id = _config.optString("InterstitialAdId", "");

		if (ad_unit_id.length() <= 0) {
			Utils.d("GodotAds", "AdMob:Interstitial:UnitId:NotProvided");
			ad_unit_id = activity.getString(R.string.gads_interstitial_ad_unit_id);
		}

		mInterstitialAd = new InterstitialAd(activity);
		mInterstitialAd.setAdUnitId(ad_unit_id);
		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				Utils.d("GodotAds", "AdMob:Interstitial:OnAdLoaded");
				Utils.callScriptFunc("AdMob", "AdMob_Interstitial", "loaded");
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				Utils.w("GodotAds", "AdMob:Interstitial:onAdFailedToLoad:" + errorCode);
				Utils.callScriptFunc("AdMob", "AdMob_Interstitial", "load_failed");
			}

			@Override
			public void onAdClosed() {
				Utils.w("GodotAds", "AdMob:Interstitial:onAdClosed");
				requestNewInterstitial();
			}

		});

		requestNewInterstitial();
	}

	private void requestNewInterstitial() {
		AdRequest.Builder adRB = new AdRequest.Builder();

		if (BuildConfig.DEBUG) {
			adRB.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
			adRB.addTestDevice(Utils.getDeviceId(activity));
		}

		AdRequest adRequest = adRB.build();

		mInterstitialAd.loadAd(adRequest);
	}

	public void createRewardedVideo() {
		mrv = MobileAds.getRewardedVideoAdInstance(activity);
		mrv.setRewardedVideoAdListener(new RewardedVideoAdListener() {

			@Override
			public void onRewardedVideoAdLoaded() {
				Utils.d("GodotAds", "AdMob:Video:Loaded");
				//emitRewardedVideoStatus();
				//Utils.call
				adRewardLoaded = true;
			}

			@Override
			public void onRewarded(RewardItem rewardItem) {
				Utils.d("GodotAds", "AdMob:Rewarded");

				JSONObject ret = new JSONObject();
				try {
					ret.put("RewardType", rewardItem.getType());
					ret.put("RewardAmount", rewardItem.getAmount());
				} catch (JSONException e) {
					Utils.d("GodotAds", "AdMob:Reward:Error:" + e.toString());
				}

				Utils.callScriptFunc("AdMob", "AdMobReward", ret.toString());
				adRewardLoaded = false;
			}

			@Override
			public void onRewardedVideoAdFailedToLoad(int errorCode) {
				Utils.d("GodotAds", "AdMob:VideoLoad:Failed");
				Utils.callScriptFunc("AdMob", "AdMob_Video", "load_failed");
				adRewardLoaded = false;
			}

			@Override
			public void onRewardedVideoAdClosed() {
				Utils.d("GodotAds", "AdMob:VideoAd:Closed");
				adRewardLoaded = false;
			}

			@Override
			public void onRewardedVideoAdLeftApplication() {
				Utils.d("GodotAds", "AdMob:VideoAd:LeftApp");
				adRewardLoaded = false;
			}

			@Override
			public void onRewardedVideoAdOpened() {
				Utils.d("GodotAds", "AdMon:VideoAd:Opended");
				adRewardLoaded = false;
			}

			@Override
			public void onRewardedVideoStarted() {
				Utils.d("GodotAds", "Reward:VideoAd:Started");
				adRewardLoaded = false;
			}

			@Override
			public void onRewardedVideoCompleted() {
				Utils.d("GodotAds", "Reward:VideoAd:Completed");
				adRewardLoaded = false;
				
				createRewardedVideo();
			}
		});

		requestNewRewardedVideo();
	}

	private void requestNewRewardedVideo() {
		if (_config == null) { return; }

		AdRequest.Builder adRB = new AdRequest.Builder();

		if (BuildConfig.DEBUG) {
			adRB.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
			adRB.addTestDevice(Utils.getDeviceId(activity));
		}

		String ad_unit_id = _config.optString("RewardedVideoAdId", "");

		if (ad_unit_id.length() <= 0) {
			Utils.d("GodotAds", "AdMob:RewardedVideo:UnitId:NotProvided");
			ad_unit_id = activity.getString(R.string.gads_rewarded_video_ad_unit_id);	
		}

		mrv.loadAd(ad_unit_id, adRB.build());
	}

	public void show_banner_ad(final boolean show) {
		if (mAdView == null) { return; }

		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (show) {
					if (mAdView.isEnabled()) { mAdView.setEnabled(true); }
					if (mAdView.getVisibility() == View.INVISIBLE) {
						Utils.d("GodotAds", "AdMob:Visiblity:On");
						mAdView.setVisibility(View.VISIBLE);
				}
				} else {
					if (mAdView.isEnabled()) { mAdView.setEnabled(false); }
					if (mAdView.getVisibility() != View.INVISIBLE) {
						Utils.d("GodotAds", "AdMob:Visiblity:Off");
						mAdView.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
	}

	public void show_interstitial_ad() {
		if (mInterstitialAd == null) { return; }

		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (mInterstitialAd.isLoaded()) { mInterstitialAd.show(); }
				else { Utils.d("GodotAds", "AdMob:Interstitial:NotLoaded"); }
			}
		});
	}

	public void show_rewarded_video() {
		if (mrv == null) { return; }

		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (mrv.isLoaded()) { mrv.show(); }
				else { Utils.d("GodotAds", "AdMob:RewardedVideo:NotLoaded"); }
			}
		});
	}
	
	public boolean is_rewarded_video_loaded() {
		return adRewardLoaded;
	}

    public Dictionary get_banned_size() {
        if ((int)mAdSize.get("width") == 0 || (int)mAdSize.get("height") == 0) {
            Utils.d("GodotAds", "AdView::Not::Loaded::Yet");
        }
        
        return mAdSize;
    }

	protected void onMainPause () {
		if (mAdView != null) { mAdView.pause(); }
		if (mrv != null) { mrv.pause(activity); }
	}

	protected void onMainResume () {
		if (mAdView != null) { mAdView.resume(); }
		if (mrv != null) { mrv.resume(activity); }
	}

	protected void onMainDestroy () {
		if (mAdView != null) { mAdView.destroy(); }
		if (mrv != null) { mrv.destroy(activity); }
	}

	private static Activity activity = null;
	private static AdMob mInstance = null;

	private AdView mAdView = null;
	private RewardedVideoAd mrv = null;
	private InterstitialAd mInterstitialAd = null;

	private JSONObject _config = null;
    private Dictionary mAdSize = null;

	private int _script_id = -1;
	
	private boolean adRewardLoaded = false;
}

