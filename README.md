# GodotAds
Godot all in one ads module for Android. (Customisable)

# Cloning
```
cd ${GODOT_ROOT}/modules/
git clone https://github.com/FrogSquare/GodotAds GodotAds
```
 and you must configure your module by editing `${GODOT_ROOT}/modules/GodotAds/config.py`

```
build_admob = True		# Include AdMob
build_adcolony = True		# Include AdColony
build_chartboost = True		# Include Chartboost
build_vungle = True		# Include Vungle
```

# Setting up

**sdk and dependency** for (Chartboost and Vungle)
Download the sdk to `${GODOT_ROOT}/modules/GodotAds/libs/`

Chartboost: [Android SDk](http://www.chartboo.st/sdk/android)
Vungle: [Android SDK](https://dashboard.vungle.com/dashboard/api/1/sdk/android?v=4)

Initialize AdMob
```
var AdMob = Globals.get_singleton("AdMob")

var _dict = Dictionary()
_dict["BannerAd"] = false
_dict["InterstitialAd"] = false
_dict["RewardedVideoAd"] = false
_dict["BannerGravity"] = "BOTTOM" # or Top
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

var _dict = Dictionary();
_dict["app_id"] = "your vungle app"

Vungle.init(_dict, get_instance_ID())
```

# Callbacks
adding the callback funtion so we can recive event log/states from the module
```
func _receive_message (from, key, value) {
	# Receive message
}
```

# API
**AdMob**
```
AdMob.show_banner_ad(true) # show ad
AdMob.show_banner_ad(false) # hide ad

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

# Log adb
```
adb -d logcat godot:V GoogleService:V GodotAds:V DEBUG:V AndroidRuntime:V ValidateServiceOp:V *:S
```
and if using our `GodotFireBase` module replace `GodotAds` with `FireBase`
```
adb -d logcat godot:V GoogleService:V FireBase:V DEBUG:V AndroidRuntime:V ValidateServiceOp:V *:S
```

