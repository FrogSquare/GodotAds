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

import com.vungle.warren.Vungle;
import com.vungle.warren.AdConfig;              // Custom ad configurations
import com.vungle.warren.InitCallback;          // Initialization callback
import com.vungle.warren.LoadAdCallback;        // Load ad callback
import com.vungle.warren.PlayAdCallback;        // Play ad callback
import com.vungle.warren.VungleNativeAd;        // Flex-Feed ad
import com.vungle.warren.Vungle.Consent;        // GDPR consent
import com.vungle.warren.error.VungleException; // onError message

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
			"init", "show", "load"
		});
	}

	public void init (final Dictionary p_dict, final int p_script_id) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				_config = new JSONObject(p_dict);
				_init();

				Utils.setScriptInstance(p_script_id);
			}
		});
	}

	private void _init() {
		final String app_id = _config.optString("app_id");

        Vungle.init(app_id, activity, new InitCallback() {
            @Override
            public void onSuccess() {
                Utils.d("GodotAds", "Vungle::Initialized");
            }

            @Override
            public void onError(Throwable throwable) {
                // throwable.getLocalizedMessage() contains error message
				Utils.d("GodotAds", "Vungle::Initialization:Error");
            }

            @Override
            public void onAutoCacheAdAvailable(String placementId) {
                // Callback to notify when an ad becomes available for the auto-cached placement
                // NOTE: This callback works only for the auto-cached placement. Otherwise, please use
                // LoadAdCallback with loadAd API for loading placements.
            }
        });

		globalAdConfig = new AdConfig();
		globalAdConfig.setMuted(true);
        globalAdConfig.setAutoRotate(true);
	}

    public void load(final String placement) {
    	activity.runOnUiThread(new Runnable() {
		    public void run() {
                if (Vungle.isInitialized()) {
                    if (placement.equals("")) {
                        Vungle.loadAd("PLACEMENT_ID", loadListener);
                    } else {
                        Vungle.loadAd(placement, loadListener);
                    }
                } else {
                    Utils.d("GodotAds", "Vungle:NotInitialized");
                }
            }
        });
    }

	//TODO: expose AdPlay configuration to script
	public void show(final String placement) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (Vungle.canPlayAd(placement)) {
					Utils.d("GodotAds", "Vungle::Show::Ad");
					Vungle.playAd(placement, globalAdConfig, playListener);
				}
			}
		});
	}

	private PlayAdCallback playListener = new PlayAdCallback() {
		@Override
        public void onAdStart(String placementReferenceId) {
			Utils.d("GodotAds", "Vungle::Ad::Start");
		}

		@Override
		public void onAdEnd(String pReferenceId, boolean completed, boolean isCTAClicked) {
			if (completed) {
				Utils.d("GodotAds", "Vungle::ShouldReward");
				Utils.callScriptFunc("Vungle", "Reward", true);
			}

			if (isCTAClicked) {
				Utils.d("GodotAds", "Vungle::CallToAction::Clicked");
				Utils.callScriptFunc("Vungle", "CallToAction", true);
			}

			Utils.d("GodotAds", "Vungle::Ad::End");
		}

        @Override
        public void onError(String placementReferenceId, Throwable throwable) {
            // Placement reference ID for the placement that failed to play an ad
            // Throwable contains error message
        }
	};

    // Implement LoadAdCallback
    private LoadAdCallback loadListener = new LoadAdCallback() {
        @Override
        public void onAdLoad(String placementReferenceId) {
            // Placement reference ID for the placement to load ad assets
            Utils.callScriptFunc("Vungle", "AdLoaded", true);
        }

        @Override
        public void onError(String placementReferenceId, Throwable throwable) {
            // Placement reference ID for the placement that failed to download ad assets
            // Throwable contains error message
            Utils.callScriptFunc("Vungle", "AdLoaded", false);
        }
    };

	protected void onMainPause () {
	}

	protected void onMainResume () {
	}

	protected void onMainDestroy() {
	}

	private static Activity activity;
	private static JSONObject _config;
    private static AdConfig globalAdConfig;
}
