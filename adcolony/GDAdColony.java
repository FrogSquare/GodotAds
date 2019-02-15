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
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

import com.adcolony.sdk.*;

import com.godot.game.BuildConfig;
import com.godot.game.R;

import org.godotengine.godot.Godot;
import org.godotengine.godot.Utils;

import org.json.JSONObject;
import org.json.JSONException;

import org.godotengine.godot.Dictionary;

public class GDAdColony extends Godot.SingletonBase {

	static public Godot.SingletonBase initialize (Activity p_activity) {
		return new GDAdColony(p_activity);
	}

	public GDAdColony(Activity p_activity) {
		activity = p_activity;

		registerClass ("GDAdColony", new String[] {
		"init", "show"
		});
	}

	public void init (final Dictionary p_dict, final int p_script_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				_config = new JSONObject(p_dict);
				_init();

				Utils.setScriptInstance(p_script_id);
				Utils.d("GodotAds", "AdColony::Initialized");
			}
		});
	}

	private void _init() {
		_ad_caller = new HashMap<String, AdColonyInterstitial>();
		app_id = _config.optString("app_id", "app3772aa9e845c4338bb");

		ArrayList<String> i_zone_ids =
		new ArrayList<String>(Arrays.asList(_config.optString("zone_ids").split(",")));

		ArrayList<String> r_zone_ids =
		new ArrayList<String>(Arrays.asList(_config.optString("reward_ids").split(",")));

		ArrayList<String> list = i_zone_ids;
		list.addAll(r_zone_ids);

		AdColonyAppOptions app_options = new AdColonyAppOptions();
		app_options.setUserID(Utils.getDeviceId(activity));

		AdColony.configure(activity, app_options, app_id, list.toArray(new String[list.size()]));

		ad_options = new AdColonyAdOptions();
		ad_options.enableConfirmationDialog(_config.optBoolean("dialog", true));
		ad_options.enableResultsDialog(_config.optBoolean("dialog", true));

		AdColony.setRewardListener(reward_l);

		for (String zone_id : i_zone_ids) {
			AdColony.requestInterstitial(zone_id, listener);
		}

		for (String zone_id : r_zone_ids) {
			AdColony.requestInterstitial(zone_id, listener, ad_options);
		}
	}

	private AdColonyInterstitialListener listener = new AdColonyInterstitialListener() {
		@Override
		public void onRequestFilled(AdColonyInterstitial p_ad) {
			Utils.d("GodotAds", "AdColony::RequestFilled::" + p_ad.getZoneID());
			_ad_caller.put(p_ad.getZoneID(), p_ad);
			Utils.callScriptFunc("AdColony", "AdFill", "success");
		}
	
		@Override
		public void onRequestNotFilled(AdColonyZone zone) {
			Utils.callScriptFunc("AdColony", "AdFill", "failed");
		}

		@Override
		public void onOpened(AdColonyInterstitial p_ad) {
			Utils.callScriptFunc("AdColony", "AdOpened", p_ad.getZoneID());
		}

		@Override
		public void onExpiring(AdColonyInterstitial p_ad) {
			Utils.callScriptFunc("AdColony", "AdExpiring", p_ad.getZoneID());
		}
	};

	AdColonyRewardListener reward_l = new AdColonyRewardListener() {
		@Override
		public void onReward(AdColonyReward reward) {
			Utils.d("GodotAds", "Give Reward");
			Utils.callScriptFunc("AdColony", "Reward", true);
			/** Query reward object for info here */
		}
	};

	void show(final String p_zone_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Utils.d("GodotAds", "Show Interstitial Ad");

				if (_ad_caller.containsKey(p_zone_id)) {
					_ad_caller.get(p_zone_id).show();
				}
			}
		});
	}

	protected void onMainActivityResult (int requestCode, int resultCode, Intent data) {
	}

	protected void onMainPause () {
	}

	protected void onMainResume () {
	}

	protected void onMainDestroy () {
	}

	private static Activity activity = null;

	private String app_id;

	private static Map<String, AdColonyInterstitial> _ad_caller;
	private static AdColonyAdOptions ad_options;
	private static AdColonyAppOptions app_options;

	private JSONObject _config = null;
}
