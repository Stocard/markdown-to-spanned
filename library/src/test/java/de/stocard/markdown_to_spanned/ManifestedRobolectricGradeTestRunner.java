package de.stocard.markdown_to_spanned;

/*
 * Copyright 2016 Stocard GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;
import org.robolectric.res.FsFile;

/**
 * More dynamic path resolution.
 *
 * This workaround is only for Mac Users necessary and only if they don't use the $MODULE_DIR$
 * workaround. Follow this issue at https://code.google.com/p/android/issues/detail?id=158015
 */
public class ManifestedRobolectricGradeTestRunner extends RobolectricGradleTestRunner {

	public ManifestedRobolectricGradeTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	protected AndroidManifest getAppManifest(Config config) {
		AndroidManifest appManifest = super.getAppManifest(config);
		FsFile androidManifestFile = appManifest.getAndroidManifestFile();

		if (androidManifestFile.exists()) {
			return appManifest;
		} else {
			String moduleRoot = getModuleRootPath(config);
			androidManifestFile = FileFsFile.from(moduleRoot, appManifest.getAndroidManifestFile().getPath().replace("bundles", "manifests/full"));
			FsFile resDirectory = FileFsFile.from(moduleRoot, appManifest.getResDirectory().getPath());
			FsFile assetsDirectory = FileFsFile.from(moduleRoot, appManifest.getAssetsDirectory().getPath());
			return new AndroidManifest(androidManifestFile, resDirectory, assetsDirectory);
		}
	}

	private String getModuleRootPath(Config config) {
		String moduleRoot = config.constants().getResource("").toString().replace("file:", "");
		return moduleRoot.substring(0, moduleRoot.indexOf("/build"));
	}
}
