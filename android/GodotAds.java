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

import org.godotengine.godot.Godot;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.FrameLayout;

import android.app.Activity;

public class GodotAds extends Godot.SingletonBase {

	static public Godot.SingletonBase initialize (Activity p_activity) {
		return new GodotAds(p_activity);
	}

	public GodotAds(Activity p_activity) {
        activity = p_activity;

		registerClass ("GodotAds", new String[] {
		"init", "set_debug"
		});
	}

	public void init (int p_script_id) {
        adLayout = new RelativeLayout(activity);
        adLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        ((Godot)activity).layout.addView(adLayout);


		Utils.setScriptInstance(p_script_id);
	}

    public void set_debug(final boolean p_value) {
        Utils.set_debug("GodotAds", p_value);
    }

    public static RelativeLayout adLayout;
    public static Activity activity;
}
