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

build_admob = True
build_adcolony = True
build_chartboost = True
build_vungle = False

import os

def can_build(plat):
	return plat == "android"

def configure(env):
	if env["platform"] == "android":
		env.android_add_maven_repository('url "https://maven.google.com"')
		env.android_add_maven_repository(\
		"url 'https://oss.sonatype.org/content/repositories/snapshots'")

		env.android_add_to_manifest("android/AndroidManifestChunk.xml")
		env.android_add_to_permissions("android/AndroidPermissionsChunk.xml");

		env.android_add_gradle_classpath("com.google.gms:google-services:3.1.1")
		#env.android_add_gradle_plugin("com.google.gms.google-services")

		if "FireBase" in env.module_list: pass
		else: env.android_add_java_dir("utils");

		env.android_add_java_dir("android");
		env.android_add_res_dir("res");

		if (build_admob):
			env.android_add_java_dir("admob");
			env.android_add_dependency("compile 'com.google.android.gms:play-services-ads:11.2.0'")

		if (build_adcolony):
			env.android_add_java_dir("adcolony");
			env.android_add_maven_repository('url "https://adcolony.bintray.com/AdColony"')
			env.android_add_dependency("compile 'com.google.android.gms:play-services-ads:11.2.0'")
			env.android_add_dependency("compile 'com.android.support:support-annotations:25.0.1'")
			env.android_add_dependency("compile 'com.adcolony:sdk:3.2.1'")

		if (build_chartboost):
			env.android_add_java_dir("chartboost");
			cur_dir = os.path.dirname(os.path.abspath(__file__))
			print(cur_dir)

			env.android_add_dependency("compile files('"+cur_dir+"/libs/chartboost.jar')")
		if (build_vungle):
			env.android_add_java_dir("vungle");

		#env.android_add_default_config("minSdkVersion 15")
