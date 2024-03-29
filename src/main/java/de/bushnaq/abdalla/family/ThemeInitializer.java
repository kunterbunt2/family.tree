package de.bushnaq.abdalla.family;

import de.bushnaq.abdalla.theme.IJThemesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Order(1)
public class ThemeInitializer {
    //    @Autowired
//    private RcApplicationConfiguration settings;
    @Autowired
    private IJThemesManager themesManager;

    @PostConstruct
    public void postConstruct() {
//        themesManager.setCurrentTheme(settings.intellijThemeFileName.getValue(), settings.enableTableGrid.getValue());
        themesManager.setCurrentTheme("arc_theme_dark.theme.json", true);
    }

}
