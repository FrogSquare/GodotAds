# GodotAds
Godot all in one ads module for Android. (Customizable)

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://github.com/FrogSquare/GodotFireBase)
[![GodotEngine](https://img.shields.io/badge/Godot_Engine-2.X%20/%203.X-blue.svg)](https://github.com/godotengine/godot)
[![LICENCE](https://img.shields.io/badge/License-Apache_V2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![PATREON](https://img.shields.io/badge/Patreon-support-yellow.svg)](https://www.patreon.com/bePatron?u=5130479)

# Tip
If you are using goodt 2.X copy `build.gradle.template` and `AndroidManifest.xml.template` from godot 3.1 (master branch) into `$(GODOT_2_ROOT)/platform/android/`,

# Cloning
```
cd ${GODOT_ROOT}/modules/
git clone https://github.com/FrogSquare/GodotAds GodotAds
git clone https://github.com/FrogSquare/GodotSql GodotSql
```
 and you must configure your module by editing `${GODOT_ROOT}/modules/GodotAds/config.py`

```
build = {
"admob"         : True,
"adcolony"      : True,
"chartboost"    : True,
"vungle"        : True,
"mopub"         : True,
"unity_ads"     : True,
}
```

# Setting up

**sdk and dependency** for (Chartboost and Vungle)
Download the sdk to `${GODOT_ROOT}/modules/GodotAds/libs/`

Chartboost: [Chartboost SDk](http://www.chartboo.st/sdk/android)
Vungle: [Vungle SDK](https://dashboard.vungle.com/dashboard/api/1/sdk/android?v=4)
UnityAds: [Unity-ads SDK](https://github.com/Unity-Technologies/unity-ads-android/releases/download/2.1.1/unity-ads.aar)

#On (on 2.X)
```
var gdads = Globals.get_singleton("GodotAds")
gdads.init(get_instance_ID())
gdads.set_debug(boolean) # set true for logs
```
# On 3.X (latest from git)
```
var gdads = Engine.get_singleton("GodotAds")
gdads.init(get_instance_ID())
```
calling `init()` on `GodotAds` is optional.

Initialize AdMob
```
var AdMob = Globals.get_singleton("AdMob")

var _dict = Dictionary()
_dict["BannerAd"] = false
_dict["InterstitialAd"] = false
_dict["RewardedVideoAd"] = false
_dict["BannerGravity"] = "BOTTOM" # or TOP
_dict["BannerAdId"] = "your banner ad id"
_dict["InterstitialAdId"] = "your interstitial ad id"
_dict["RewardedVideoAdId"] = "rewarded video ad id"

AdMob.init(_dict, get_instance_ID())
```

Initialize AdColony
```
var AdColony = Globals.get_singleton("GDAdColony")

var _dict = Dictionary()
_dict["app_id"] = "adcolotn app id"
_dict["zone_ids"] = "adcolony interstitial zone ids" # (e.g) "jkedbciujdcoidcj,iyhfecujncuofevef,ikyvejcnilnuvel"
_dict["reward_ids"] = "adcolony rewarded zone id" # (e.g) "jkedbciujdcoidcj,iyhfecujncuofevef,ikyvejcnilnuvel"
_dict["dialog"] = true # or false

AdColony.init(_dict, get_instance_ID())
```

Initialize Chartboost
```
var Chartboost = Globals.get_singleton("GDChartboost")

var _dict = Dictionary()
_dict["app_id"] = "Your chartboost app id" 
_dict["app_signature"] = "your chartboost signature"

Chartboost.init(_dict, get_instance_ID())
```

Initialize Vungle
```
var Vungle = Globals.get_singleton("GDVungle")

var _dict = Dictionary()
_dict["app_id"] = "your vungle app"

Vungle.init(_dict, get_instance_ID())
```

Initialize MoPub
```
var Mopub = Globals.get_singleton("GDMopub")

var _dict = Dictionary()
_dict["BannerAd"] = true
_dict["InterstitialAd"] = true
_dict["BannerGravity"] = "BOTTOM" # or TOP
_dict["BannerAdId"] = "your banner unit id"
_dict["InterstitialAdId"] = "your interstitial unit id"

Mopub.init(_dict, get_instance_ID())
```

Initialize UnityAds
```
var Unityads = Globals.get_singleton("GDUnityAds")

var _dict = Dictionary()
_dict["GameId"] = "Your game ID"

Unityads.init(_dict, get_instance_ID())
```

# Callbacks
adding the callback funtion so we can recive event log/states from the module
```
func _receive_message (tag, from, key, value):
	if tag == "GodotAds" and from == "AdMob":
        if key == "AdMob_Banner" and value == "loaded":
            # Show banner here.
```

# API
**AdMob**
```
AdMob.show_banner_ad(true) # show banner ad
AdMob.show_banner_ad(false) # hide banner ad

AdMob.show_interstitial_ad() # Show Interstitial Ad

AdMob.show_rewarded_video() # Show Rewarded Ad
```

**AdColony**
```
AdColony.show(String zone_id) # Show AdColony for the zone id
```

**Chartboost**
```
Chartboost.show_interstitial_ad()
Chartboost.show_rewarded_video()
```

**Vungle**
```
Vungle.show()
```

**MoPub**
```
AdMob.show_banner_ad(true) # show banner ad
AdMob.show_banner_ad(false) # hide banner ad

AdMob.show_interstitial_ad() # Show Interstitial Ad
```

**Unity Ads**
```
Unityads.show("Location id")
```

# Log adb
```
adb -d logcat godot:V GoogleService:V FrogSquare:V DEBUG:V AndroidRuntime:V ValidateServiceOp:V *:S
```
