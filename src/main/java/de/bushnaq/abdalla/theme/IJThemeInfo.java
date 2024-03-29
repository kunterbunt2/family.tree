/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bushnaq.abdalla.theme;

import java.io.File;

/**
 * @author Karl Tauber
 */
public class IJThemeInfo {
    public final boolean dark;
    public final String lafClassName;
    public final String license;
    public final String name;
    public final String resourceName;
    public final String sourceCodeUrl;
    public final File themeFile;
    final String licenseFile;
    final String sourceCodePath;

    IJThemeInfo(String name, String resourceName, boolean dark, String license, String licenseFile, String sourceCodeUrl, String sourceCodePath, File themeFile, String lafClassName) {
        this.name = name;
        this.resourceName = resourceName;
        this.dark = dark;
        this.license = license;
        this.licenseFile = licenseFile;
        this.sourceCodeUrl = sourceCodeUrl;
        this.sourceCodePath = sourceCodePath;
        this.themeFile = themeFile;
        this.lafClassName = lafClassName;
    }
}
