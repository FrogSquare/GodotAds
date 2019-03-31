**
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
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.godot.game.BuildConfig;
import com.godot.game.R;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.godotengine.godot.Utils;

public class Utils {

    public static final String TAG = "FrogSquare";

	public static final int FIREBASE_INVITE_REQUEST		= 8002;
	public static final int FIREBASE_NOTIFICATION_REQUEST	= 8003;
	public static final int FIREBASE_GOOGLE_SIGN_IN		= 8004;
	public static final int FIREBASE_FACEBOOK_SIGN_IN	= 8005;
	public static final int FIREBASE_TWITTER_SIGN_IN	= 8006;
	// public static final int FIREBASE_ = ;

    public static void initDebug(final String from) {
        if (DebugCfg == null) {
            DebugCfg = new HashMap< String, Boolean>();
        }

        set_debug(from, true);
    }

    public static void set_debug(final String from, final boolean value) {
        if (DebugCfg == null) {
            initDebug(from);
        }

        DebugCfg.put(from, value);
    }

    public static boolean get_debug(final String from) {
        if (DebugCfg == null) {
            initDebug(from);
        }

        if (DebugCfg.containsKey(from)) {
            return DebugCfg.get(from);
        } else {
            set_debug(from, true);
            return DebugCfg.get(from);
        }
    }

    /** GodotSQL **/
    public static boolean get_db_bool(final String p_key) {
        String val = get_db_value(p_key);

        if (val.equals("0") || val.equals("false")) {
            return false;
        } else {
            return true;
        }
    }

    public static String get_db_value(final String p_key) {
        return KeyValueStorage.getValue(p_key);
    }

    public static void set_db_value(final String p_key, final String p_value) {
        KeyValueStorage.setValue(p_key, p_value);
    }
    /** GodotSQL **/

	public static void d(final String from, final String message) {
		if (get_debug(from)) {
			Log.d(TAG, message);
		}
	}

	public static void e(final String from, final String message) {
		if (get_debug(from)) {
			Log.e(TAG, message);
		}
	}

	public static void i(final String from, final String message) {
		if (get_debug(from)) {
			Log.i(TAG, message);
		}
	}

	public static void w(final String from, final String message) {
		if (get_debug(from)) {
			Log.w(TAG, message);
		}
	}

	public static Map<String, Object> jsonToMap (String jsonData) {
		JSONObject jobject = null;

		try { jobject = new JSONObject(jsonData); }
		catch (JSONException e) { Utils.d(TAG, "JSONObject exception: " + e.toString()); }

		Map<String, Object> retMap = new HashMap<String, Object>();
		Iterator<String> keysItr = jobject.keys();

		while(keysItr.hasNext()) {
			try {
				String key = keysItr.next();
				Object value = jobject.get(key);

				retMap.put(key, value);
			} catch (JSONException e) {
				Utils.d(TAG, "JSONObject get key error" + e.toString());
			}
		}

		return retMap;
	}

	public static Bitmap getBitmapFromAsset(Context context, String filePath) {
		if (filePath.startsWith("res://")) { filePath = filePath.replaceFirst("res://", ""); }

		AssetManager assetManager = context.getAssets();

		InputStream istr;
		Bitmap bitmap = null;

		try {
			istr = assetManager.open(filePath);
			bitmap = BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			// handle exception
		}

		return bitmap;
	}

	public static String readFromFile(String fPath, Context context) {
        Utils.d(TAG, "Reading File: " + fPath);
		StringBuilder returnString = new StringBuilder();

		String fileName = fPath;
		if (fPath.startsWith("res://")) { fileName = fileName.replaceFirst("res://", ""); }

		InputStream fIn = null;
		InputStreamReader isr = null;
		BufferedReader input = null;

		try {
			fIn = context.getResources().getAssets()
			.open(fileName, Context.MODE_WORLD_READABLE);

			isr = new InputStreamReader(fIn);
			input = new BufferedReader(isr);

			String line = "";

			while ((line = input.readLine()) != null) {
				returnString.append(line);
			}

		}
		catch (Exception e) {
            Utils.d(TAG, "FileRead Failed: " + e.getMessage());
        }
		finally {
			try {
				if (isr != null) { isr.close(); }
				if (fIn != null) { fIn.close(); }
				if (input != null) { input.close(); }

			} catch (Exception e2) { e2.getMessage(); }
		}

		return returnString.toString();
	}

	public static String askForPath(final String folder) {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + folder;
	}

	// { Device ID - https://stackoverflow.com/questions/4524752/how-can-i-get-device-id-for-admob

	public static String getDeviceId(Activity activity) {
		String android_id =
		Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);

		String deviceId = md5(android_id).toUpperCase();

		return android_id;
	}

	public static final String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());

			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);

				while (h.length() < 2) {
					h = "0" + h;
				}

				hexString.append(h);
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) { Utils.w(TAG, "FB:MD5:Algorithm:" + e.toString()); }

		return "";
	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}

		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
		Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

			return true;
		}

		return false;
	}

	// Check for class
	public static boolean isClass(String className) {
		try  {
			Class.forName(className);
			return true;
		}  catch (ClassNotFoundException e) { return false; }
	}

	public static void setScriptInstance(int instanceID) {
		script_instanceID = instanceID;
	}

	public static void callScriptCallback(
            int script_id, String function, String from, Object key, Object value) {

		GodotLib.calldeferred(script_id, function, new Object[] { TAG, from, key, value });
	}

	public static void callScriptCallback(String function, String from, Object key, Object value) {
		if (script_instanceID == -1) {
			Utils.d(TAG, "Script::Instance::NotSset");
			return;
		}

		GodotLib.calldeferred(script_instanceID, function,
		new Object[] { TAG, from, key, value });
	}

	public static void callScriptFunc(int script_id, String from, Object key, Object value) {
		GodotLib.calldeferred(script_id, "_receive_message",
		new Object[] { TAG, from, key, value });
	}

	public static void callScriptFunc(String from, Object key, Object value) {
		if (script_instanceID == -1) {
			Utils.d(TAG, "Script::Instance::NotSset");
			return;
		}

		GodotLib.calldeferred(script_instanceID, "_receive_message",
		new Object[] { TAG, from, key, value });
	}

	public static boolean checkGooglePlayService(Activity activity) {
		return true;
	}

	public static void putAllInDict(Bundle bundle, Dictionary keyValues) {
		String[] keys = keyValues.get_keys();
		for(int i=0; i < keys.length; i++) {
			String key = keys[i];
			Utils.putGodotValue(bundle, key, keyValues.get(key));
		}
	}

	public static void putGodotValue(Bundle bundle, String key, Object value) {

		if (value instanceof Boolean) {
			bundle.putBoolean(key, (Boolean) value);

		} else if (value instanceof Integer) {
			bundle.putInt(key, (Integer) value);

		} else if (value instanceof Double) {
			bundle.putDouble(key, (Double) value);

		} else if (value instanceof String) {
			bundle.putString(key, (String) value);

		} else {

			if (value != null) {
				bundle.putString(key, value.toString());
			}

		}
	}

    private static Map< String, Boolean> DebugCfg = null;
	public static int script_instanceID = -1;
}
