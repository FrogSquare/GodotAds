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

import com.vungle.publisher.VunglePub;

import com.godot.game.BuildConfig;
import com.godot.game.R;

import org.godotengine.godot.Godot;
import org.godotengine.godot.Utils;

import org.json.JSONObject;
import org.json.JSONException;

import org.godotengine.godot.Dictionary;

public class GDVungle extends Godot.SingletonBase {

	static public Godot.SingletonBase initialize (Activity p_activity) {
		return new GDVungle(p_activity);
	}

	public GDVungle(Activity p_activity) {
		activity = p_activity;

		registerClass ("GDVungle", new String[] {
			"init", "show"
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
		final String app_id = _config.optString("app_id");
		vunglePub.init(activity, app_id);

		Utils.d("Vungle::Initialized");
	}

	public void show() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (vunglePub.isAdPlayable()) {
					Utils.d("Vungle::Show::Ad");
					vunglePub.playAd();
				}
			}
		});
	}

	protected void onMainPause () {
		vunglePub.onPause();
	}

	protected void onMainResume () {
		vunglePub.onResume();
	}

	private static Activity activity;
	private static JSONObject _config;

	final VunglePub vunglePub = VunglePub.getInstance();
}
