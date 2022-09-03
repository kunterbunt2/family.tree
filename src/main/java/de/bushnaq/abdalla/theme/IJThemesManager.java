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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.json.Json;
import com.formdev.flatlaf.util.StringUtils;

/**
 * @author Karl Tauber
 */
@Component
public class IJThemesManager {
    private static final String THEMES_PACKAGE = "/com/formdev/flatlaf/intellijthemes/themes/";
    private static boolean dark;

    public static boolean isDark() {
        return dark;
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public final List<IJThemeInfo> bundledThemes = new ArrayList<>();
    private final List<IJThemeInfo> moreThemes = new ArrayList<>();
    private final Map<File, Long> lastModifiedMap = new HashMap<>();

    //    private boolean hasThemesFromDirectoryChanged() {
    //        for (Map.Entry<File, Long> e : lastModifiedMap.entrySet()) {
    //            if (e.getKey().lastModified() != e.getValue().longValue()) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }

    @SuppressWarnings("unchecked")
    public void loadBundledThemes(String themePath) throws Exception {
        bundledThemes.clear();

        // load themes.json
        Map<String, Object> json;
        try (Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(themePath), StandardCharsets.UTF_8)) {
            json = (Map<String, Object>) Json.parse(reader);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new Exception(e);
        }

        // add info about bundled themes
        for (Map.Entry<String, Object> e : json.entrySet()) {
            String resourceName = e.getKey();
            Map<String, String> value = (Map<String, String>) e.getValue();
            String name = value.get("name");
            boolean dark = Boolean.parseBoolean(value.get("dark"));
            String license = value.get("license");
            String licenseFile = value.get("licenseFile");
            String sourceCodeUrl = value.get("sourceCodeUrl");
            String sourceCodePath = value.get("sourceCodePath");

            bundledThemes.add(new IJThemeInfo(name, resourceName, dark, license, licenseFile, sourceCodeUrl, sourceCodePath, null, null));
        }
    }

    private void loadThemesFromDirectory() {
        // get current working directory
        File directory = new File("").getAbsoluteFile();

        File[] themeFiles = directory.listFiles((dir, name) -> {
            return name.endsWith(".theme.json") || name.endsWith(".properties");
        });
        if (themeFiles == null) {
            return;
        }

        lastModifiedMap.clear();
        lastModifiedMap.put(directory, directory.lastModified());

        moreThemes.clear();
        for (File f : themeFiles) {
            String fname = f.getName();
            String name = fname.endsWith(".properties") ? StringUtils.removeTrailing(fname, ".properties") : StringUtils.removeTrailing(fname, ".theme.json");
            moreThemes.add(new IJThemeInfo(name, null, false, null, null, null, null, f, null));
            lastModifiedMap.put(f, f.lastModified());
        }
    }

    //    @PostConstruct
    public void setCurrentTheme(String intellijThemeFileName, boolean enableTableGrid) {
        InputStream resourceAsStream = getClass().getResourceAsStream(THEMES_PACKAGE + intellijThemeFileName);
        try {
            FlatLaf createLaf = IntelliJTheme.createLaf(resourceAsStream);
            FlatLaf.install(createLaf);
            dark = createLaf.isDark();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        setCustomizedValues(enableTableGrid);
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    private void setCustomizedValues(boolean enableTableGrid) {
        UIManager.put("Table.showHorizontalLines", enableTableGrid);
        UIManager.put("Table.showVerticalLines", enableTableGrid);

        //        {
        //            Font font = UIManager.getFont("defaultFont");
        //            Font newFont = font.deriveFont(16);
        //            UIManager.put("defaultFont", newFont);
        //        }
        //        {
        //            Font font = UIManager.getFont("Label.font");
        //            Font newFont = font.deriveFont(16);
        //            UIManager.put("Label.font", newFont);
        //        }
        //        System.setProperty("sun.java2d.uiScale", "2.5");
    }

    public void setTheme(IJThemeInfo themeInfo, boolean enableTableGrid) {
        if (themeInfo == null) {
            return;
        }

        // change look and feel
        if (themeInfo.lafClassName != null) {
            if (themeInfo.lafClassName.equals(UIManager.getLookAndFeel().getClass().getName())) {
                return;
            }

            FlatAnimatedLafChange.showSnapshot();

            try {
                UIManager.setLookAndFeel(themeInfo.lafClassName);
            } catch (Exception ex) {
                ex.printStackTrace();
                //                showInformationDialog("Failed to create '" + themeInfo.lafClassName + "'.", ex);
            }
        } else if (themeInfo.themeFile != null) {
            FlatAnimatedLafChange.showSnapshot();

            try {
                if (themeInfo.themeFile.getName().endsWith(".properties")) {
                    FlatLaf.install(new FlatPropertiesLaf(themeInfo.name, themeInfo.themeFile));
                } else {
                    FlatLaf.install(IntelliJTheme.createLaf(new FileInputStream(themeInfo.themeFile)));
                }

                //                DemoPrefs.getState().put(DemoPrefs.KEY_LAF_THEME, DemoPrefs.FILE_PREFIX + themeInfo.themeFile);
            } catch (Exception ex) {
                ex.printStackTrace();
                //                showInformationDialog("Failed to load '" + themeInfo.themeFile + "'.", ex);
            }
        } else {
            FlatAnimatedLafChange.showSnapshot();
            dark = themeInfo.dark;
            InputStream resourceAsStream = getClass().getResourceAsStream(THEMES_PACKAGE + themeInfo.resourceName);
            IntelliJTheme.install(resourceAsStream);
        }
        setCustomizedValues(enableTableGrid);
        // update all components
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

}
