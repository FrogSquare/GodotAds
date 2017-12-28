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
"admob"		: True,
"adcolony"	: True,
"chartboost"	: True,
"vungle"	: True,
"mopub"		: True,
"unity_ads"	: True,
"awesome_ads"	: True,
"appodeal"	: False,
"inmobi"	: False,
}


import os

def can_build(plat):
	return (plat == "android")

def configure(env):
	cur_dir = os.path.dirname(os.path.abspath(__file__))

	if env["platform"] == "android":
		env.android_add_maven_repository('url "https://maven.google.com"')
		env.android_add_maven_repository(\
		"url 'https://oss.sonatype.org/content/repositories/snapshots'")

		env.android_add_to_manifest("android/AndroidManifestChunk.xml")
		env.android_add_to_permissions("android/AndroidPermissionsChunk.xml");

		#env.android_add_gradle_classpath("com.google.gms:google-services:3.1.1")
		#env.android_add_gradle_plugin("com.google.gms.google-services")

		if "FireBase" in env.module_list: pass
		else: env.android_add_java_dir("utils");

		env.android_add_java_dir("android");
		env.android_add_res_dir("res");

		env.android_add_dependency("compile 'com.google.android.gms:play-services-ads:11.6.0'")

		if (build["admob"]):
			env.android_add_java_dir("admob");
		if (build["adcolony"]):
			env.android_add_java_dir("adcolony");
			env.android_add_to_manifest("adcolony/AndroidManifestChunk.xml")

			env.android_add_maven_repository('url "https://adcolony.bintray.com/AdColony"')
			env.android_add_dependency("compile 'com.android.support:support-annotations:25.0.1'")
			env.android_add_dependency("compile 'com.adcolony:sdk:3.2.1'")

		if (build["chartboost"]):
			env.android_add_java_dir("chartboost");
			env.android_add_to_manifest("chartboost/AndroidManifestChunk.xml")

			env.android_add_dependency("compile fileTree(dir: '"+cur_dir+"/libs', include: ['*.jar'])")
		if (build["vungle"]):
			env.android_add_java_dir("vungle");
			env.android_add_to_manifest("vungle/AndroidManifestChunk.xml")

			env.android_add_dependency("compile 'com.google.android.gms:play-services-location:11.6.0'")
			env.android_add_dependency("compile fileTree(dir: '"+cur_dir+"/libs', include: ['*.jar'])")
		if (build["mopub"]):
			env.android_add_default_config("minSdkVersion 16")

			env.android_add_java_dir("mopub");
			env.android_add_res_dir("mopub/res");
			env.android_add_to_manifest("mopub/AndroidManifestChunk.xml")

			env.android_add_maven_repository('url "https://s3.amazonaws.com/moat-sdk-builds"')
			env.android_add_dependency("compile('com.mopub:mopub-sdk:4.16.0@aar') { transitive = true }")
		if (build["unity_ads"]):
			env.android_add_java_dir("unity_ads");
			env.android_add_flat_dir(cur_dir + "/libs")
			env.android_add_flat_dir(cur_dir + "/res")
			env.android_add_dependency("compile(name:'unity-ads', ext:'aar')")
		if (build["awesome_ads"]): pass
		if (build["appodeal"]): pass
		if (build["inmobi"]): pass

