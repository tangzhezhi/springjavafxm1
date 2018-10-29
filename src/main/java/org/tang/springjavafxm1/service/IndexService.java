package org.tang.springjavafxm1.service;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.Setter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.tang.springjavafxm1.Main;
import org.tang.springjavafxm1.fxmlView.IndexView;
import org.tang.springjavafxm1.utils.Config;
import org.tang.springjavafxm1.utils.ConfigureUtil;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Setter
public class IndexService {
	private ResourceBundle bundle;
	private Map<String, MenuItem> menuItemMap;

	public void setLanguageAction(String languageType) throws Exception {
		File file = ConfigureUtil.getConfigureFile("systemConfigure.properties");
		FileUtils.touch(file);
		PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
		if ("简体中文".equals(languageType)) {
			Config.defaultLocale = Locale.SIMPLIFIED_CHINESE;
			xmlConfigure.setProperty("Locale", Locale.SIMPLIFIED_CHINESE);
		} else if ("English".equals(languageType)) {
			Config.defaultLocale = Locale.US;
			xmlConfigure.setProperty("Locale", Locale.US);
		}
		xmlConfigure.save();
		Main.showView(IndexView.class);
	}

	public ContextMenu getSelectContextMenu(String selectText) {
		ContextMenu contextMenu = new ContextMenu();
		for (MenuItem menuItem : menuItemMap.values()) {
			if (menuItem.getText().contains(selectText)) {
				MenuItem menu_tab = new MenuItem(menuItem.getText(),menuItem.getGraphic());
				menu_tab.setOnAction(event1 -> {
					menuItem.fire();
				});
				contextMenu.getItems().add(menu_tab);
			}
		}
		return contextMenu;
	}
}
