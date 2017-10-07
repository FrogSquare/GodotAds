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

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import com.godot.game.BuildConfig;
import com.godot.game.R;

import org.godotengine.godot.Godot;
import org.godotengine.godot.Utils;

import org.json.JSONObject;
import org.json.JSONException;

import org.godotengine.godot.Dictionary;

public class GDUnityAds extends Godot.SingletonBase {

	static public Godot.SingletonBase initialize (Activity p_activity) {
		return new GDUnityAds(p_activity);
	}

	public GDUnityAds(Activity p_activity) {
		activity = p_activity;

		registerClass ("GDUnityAds", new String[] {
			"init", "show"
		});
	}

	public void init (final Dictionary p_dict, final int p_script_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				_config = new JSONObject(p_dict);
				_init();

				Utils.setScriptInstance(p_script_id);
				Utils.d("UnityAds::Initialized");
			}
		});
	}

	private void _init() {
		UnityAds.setListener(listener);

		if (Config.DEBUG || BuildConfig.DEBUG) {
			UnityAds.setDebugMode(true);
		}

		String gameId = _config.optString("GameId", "14851");
		UnityAds.initialize(activity, gameId, listener);
	}

	public void show (final String location) {
		if (UnityAds.isReady(location)) {
			UnityAds.show(activity, location);
		} else {
			Utils.d("UnityAds::AdNotReady:: " + location);
		}
	}

	private IUnityAdsListener listener = new IUnityAdsListener() {
		@Override
		public void onUnityAdsReady(final String zoneId) {
			Utils.d("UnityAds::onUnityAdsReady: " + zoneId);
			Utils.callScriptFunc("UnityAds", "AdLoad", "Success");
		}

		@Override
		public void onUnityAdsStart(String zoneId) {
			Utils.d("UnityAds::onUnityAdsStart: " + zoneId);
		}

		@Override
		public void onUnityAdsFinish(String zoneId, UnityAds.FinishState result) {
			Utils.d("UnityAds::onUnityAdsFinish: " + zoneId + " - " + result);

			if (result.toString().equals("COMPLETED")) {
				Utils.callScriptFunc("UnityAds", "ShouldReward", true);
			} else {
				Utils.callScriptFunc("UnityAds", "ShouldReward", false);
			}
		}

		@Override
		public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
			Utils.d("UnityAds::onUnityAdsError: " + error + " - " + message);
		}
	};

	private static Activity activity = null;
	private JSONObject _config = null;

	private int _script_id = -1;
}
