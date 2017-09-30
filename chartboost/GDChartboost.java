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

import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.ChartboostDelegate;

import com.chartboost.sdk.Libraries.CBLogging.Level;
import com.chartboost.sdk.Model.CBError.CBClickError;
import com.chartboost.sdk.Model.CBError.CBImpressionError;
import com.chartboost.sdk.Tracking.CBAnalytics;
import com.chartboost.sdk.CBImpressionActivity;

import com.godot.game.BuildConfig;
import com.godot.game.R;

import org.godotengine.godot.Godot;
import org.godotengine.godot.Utils;

import org.json.JSONObject;
import org.json.JSONException;

import org.godotengine.godot.Dictionary;

public class GDChartboost extends Godot.SingletonBase {

	static public Godot.SingletonBase initialize (Activity p_activity) {
		return new GDChartboost(p_activity);
	}

	public GDChartboost(Activity p_activity) {
		activity = p_activity;

		registerClass ("GDChartboost", new String[] {
		"init", "show_interstitial_ad"
		});
	}

	public void init (final Dictionary p_dict) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				_config = new JSONObject(p_dict);
				_init();
			}
		});
	}

	private void _init() {
		appId = _config.optString("app_id", "4f7b433509b6025804000002");
		appSignature = _config.optString("app_signature", "dd2d41b69ac01b80f443f5b6cf06096d457f82bd");

		Chartboost.startWithAppId(activity, appId, appSignature);
		Chartboost.setDelegate(delegate);

		Chartboost.setLoggingLevel(Level.NONE);

		Chartboost.onCreate(activity);

		preloadAds();
	}

	private void preloadAds() {
		if (!Chartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT)) {
			Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
		}

		if (!Chartboost.hasInterstitial(CBLocation.LOCATION_GAME_SCREEN)) {
			Chartboost.cacheInterstitial(CBLocation.LOCATION_GAME_SCREEN);
		}

		if (!Chartboost.hasInterstitial(CBLocation.LOCATION_GAMEOVER)) {
			Chartboost.cacheInterstitial(CBLocation.LOCATION_GAMEOVER);
		}

		if (!Chartboost.hasRewardedVideo(CBLocation.LOCATION_DEFAULT)) {
			Chartboost.cacheRewardedVideo(CBLocation.LOCATION_DEFAULT);
		}
	}

	public void show_interstitial_ad() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (Chartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT)) {
					Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);
				}

				Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
			}
		});
	}

	public void show_rewarded_ad() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (Chartboost.hasRewardedVideo(CBLocation.LOCATION_DEFAULT)) {
					Chartboost.showRewardedVideo(CBLocation.LOCATION_DEFAULT);
				}

				Chartboost.cacheRewardedVideo(CBLocation.LOCATION_DEFAULT);
			}
		});
	}

	private ChartboostDelegate delegate = new ChartboostDelegate() {

		@Override
		public boolean shouldRequestInterstitial(String location) {
			return true;
		}

		@Override
		public boolean shouldDisplayInterstitial(String location) {
			return true;
		}

		@Override
		public void didCacheInterstitial(String location) {

		}

		@Override
		public void didFailToLoadInterstitial(String location, CBImpressionError error) {

		}

		@Override
		public void didDismissInterstitial(String location) {

		}

		@Override
		public void didCloseInterstitial(String location) {

		}

		@Override
		public void didClickInterstitial(String location) {

		}

		@Override
		public void didDisplayInterstitial(String location) {

		}

		@Override
		public boolean shouldRequestMoreApps(String location) {
			return true;
		}

		@Override
		public boolean shouldDisplayMoreApps(String location) {
			return true;
		}

		@Override
		public void didFailToLoadMoreApps(String location, CBImpressionError error) {

		}

		@Override
		public void didCacheMoreApps(String location) {

		}

		@Override
		public void didDismissMoreApps(String location) {

		}

		@Override
		public void didCloseMoreApps(String location) {

		}

		@Override
		public void didClickMoreApps(String location) {

		}

		@Override
		public void didDisplayMoreApps(String location) {

		}

		@Override
		public void didFailToRecordClick(String uri, CBClickError error) {

		}

		@Override
		public boolean shouldDisplayRewardedVideo(String location) {
			return true;
		}

		@Override
		public void didCacheRewardedVideo(String location) {

		}

		@Override
		public void didFailToLoadRewardedVideo(String location, CBImpressionError error) {

		}

		@Override
		public void didDismissRewardedVideo(String location) {

		}

		@Override
		public void didCloseRewardedVideo(String location) {

		}

		@Override
		public void didClickRewardedVideo(String location) {

		}

		@Override
		public void didCompleteRewardedVideo(String location, int reward) {

		}

		@Override
		public void didDisplayRewardedVideo(String location) {

		}

		@Override
		public void willDisplayVideo(String location) {

		}
	};

	protected void onMainActivityResult (int requestCode, int resultCode, Intent data) {
	}

	protected void onMainPause () {
		Chartboost.onPause(activity);
	}

	protected void onMainResume () {
		Chartboost.onResume(activity);
	}

	protected void onMainDestroy () {
		Chartboost.onDestroy(activity);
	}

	private static Activity activity = null;

	private String appId = "";
	private String appSignature = "";

	private JSONObject _config = null;
}
