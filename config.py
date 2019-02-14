"""
# Copyright 2017 FrogSquare. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#	http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""

build = {
"admob"         : True,
"adcolony"      : True,
"chartboost"    : True,
"vungle"        : True,
"mopub"         : True,
"unity_ads"     : True,
"awesome_ads"   : False,
"appodeal"      : False,
"inmobi"        : False,
}

import os

from colors import *

def can_build(env, plat = None):
    #return False
    if plat == None:
        print("`GodotAds`"+RED+" master "+RESET+" branch not compatable with godot 2.X")
        print("Try using `GodotAds` "+GREEN+" 2.X "+RESET+" branch for Godot 2.X")
        return False

    if plat == "android":
        print("GodotAds: " + GREEN + "Enabled" + RESET)
        return True
    else:
        print("GodotAds: " + RED + "Disabled" + RESET)
        return False
    pass   

def configure(env):
    cur_dir = os.path.dirname(os.path.abspath(__file__)).replace(os.path.sep, '/')
    libpath = os.path.join(cur_dir, "libs").replace(os.path.sep, '/')
    respath = os.path.join(cur_dir, "res").replace(os.path.sep, '/')

    if env["platform"] == "android":
        env.android_add_maven_repository('url "https://maven.google.com"')
        env.android_add_maven_repository(\
            "url 'https://oss.sonatype.org/content/repositories/snapshots'")

        env.android_add_to_manifest("android/AndroidManifestChunk.xml")
        env.android_add_to_permissions("android/AndroidPermissionsChunk.xml");

        #env.android_add_gradle_classpath("com.google.gms:google-services:3.1.1")
        #env.android_add_gradle_plugin("com.google.gms.google-services")

        env.android_add_java_dir("android");
        env.android_add_res_dir("res");

        if "frogutils" in [os.path.split(path)[1] for path in env.android_java_dirs]: pass
        else: env.android_add_java_dir("frogutils");

        env.android_add_dependency("implementation ('com.google.android.gms:play-services-ads:16.0.0') {\
                exclude group: 'com.android.support'\
                exclude module: 'support-v4'}")

        if (build["admob"]):
            env.android_add_java_dir("admob");
        if (build["adcolony"]):
            env.android_add_java_dir("adcolony");
            env.android_add_to_manifest("adcolony/AndroidManifestChunk.xml")

            env.android_add_maven_repository('url "https://adcolony.bintray.com/AdColony"')
            env.android_add_dependency("implementation 'com.android.support:support-annotations:25.0.1'")
            env.android_add_dependency("implementation 'com.adcolony:sdk:3.3.7'")

        if (build["chartboost"]):
            env.android_add_java_dir("chartboost");
            env.android_add_to_manifest("chartboost/AndroidManifestChunk.xml")

            env.android_add_dependency("implementation fileTree(dir: '"+libpath+"', include: ['*.jar'])")

        if (build["vungle"]):
            env.android_add_to_attributes("vungle/AndroidAttributes.xml")

            env.android_add_maven_repository('url "https://jitpack.io"')
            env.android_add_java_dir("vungle");
            env.android_add_to_manifest("vungle/AndroidManifestChunk.xml")

            env.android_add_dependency("implementation 'com.github.vungle:vungle-android-sdk:6.3.24'")
        if (build["mopub"]):
            env.android_add_default_config("minSdkVersion 16")

            env.android_add_java_dir("mopub");
            env.android_add_res_dir("mopub/res");
            env.android_add_to_manifest("mopub/AndroidManifestChunk.xml")

            env.android_add_maven_repository('url "https://s3.amazonaws.com/moat-sdk-builds"')
            env.android_add_dependency("implementation('com.mopub:mopub-sdk:5.4.1@aar') { transitive = true }")
            #env.android_add_dependency("implementation('com.mopub.volley:mopub-volley:2.0.0@aar') { transitive = true }")

        if (build["unity_ads"]):
            env.android_add_java_dir("unity_ads");
            env.android_add_flat_dir(libpath)
            env.android_add_flat_dir(respath)
            env.android_add_dependency("implementation(name:'unity-ads', ext:'aar')")

        if (build["awesome_ads"]): pass
        if (build["appodeal"]): pass
        if (build["inmobi"]): pass

    pass
