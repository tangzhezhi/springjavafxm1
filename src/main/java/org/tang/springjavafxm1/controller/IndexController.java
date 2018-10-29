package org.tang.springjavafxm1.controller;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.tang.springjavafxm1.model.ToolFxmlLoaderConfiguration;
import org.tang.springjavafxm1.service.IndexService;
import org.tang.springjavafxm1.utils.Config;
import org.tang.springjavafxm1.utils.JavaFxViewUtil;
import org.tang.springjavafxm1.utils.SpringUtil;
import org.tang.springjavafxm1.utils.XJavaFxSystemUtil;
import org.tang.springjavafxm1.view.IndexView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: IndexController
 * @Description: 主页
 * @author: xufeng
 * @date: 2017年7月20日 下午1:50:00
 */
@FXMLController
public class IndexController extends IndexView {
	private static Logger log = Logger.getLogger(IndexController.class);
	private Map<String, Menu> menuMap = new HashMap<String, Menu>();
	private Map<String, MenuItem> menuItemMap = new HashMap<String, MenuItem>();
	private IndexService indexService = new IndexService();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		initView();
		initEvent();
		initService();
	}

	private void initView() {
		List<ToolFxmlLoaderConfiguration> toolList = XJavaFxSystemUtil.loaderToolFxmlLoaderConfiguration();
		List<ToolFxmlLoaderConfiguration> plugInToolList = XJavaFxSystemUtil.loaderPlugInToolFxmlLoaderConfiguration();
		toolList.addAll(plugInToolList);
		menuMap.put("toolsMenu", toolsMenu);
		for (ToolFxmlLoaderConfiguration toolConfig : toolList) {
			try {
				if (StringUtils.isEmpty(toolConfig.getResourceBundleName())) {
					if (StringUtils.isNotEmpty(bundle.getString(toolConfig.getTitle()))) {
						toolConfig.setTitle(bundle.getString(toolConfig.getTitle()));
					}
				} else {
					ResourceBundle resourceBundle = ResourceBundle.getBundle(toolConfig.getResourceBundleName(),
							Config.defaultLocale);
					if (StringUtils.isNotEmpty(resourceBundle.getString(toolConfig.getTitle()))) {
						toolConfig.setTitle(resourceBundle.getString(toolConfig.getTitle()));
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			if (toolConfig.getIsMenu()) {
				Menu menu = new Menu(toolConfig.getTitle());
				if (StringUtils.isNotEmpty(toolConfig.getIconPath())) {
					ImageView imageView = new ImageView(new Image(toolConfig.getIconPath()));
					imageView.setFitHeight(18);
					imageView.setFitWidth(18);
					menu.setGraphic(imageView);
				}
				menuMap.put(toolConfig.getMenuId(), menu);
			}
		}

		for (ToolFxmlLoaderConfiguration toolConfig : toolList) {
			if (toolConfig.getIsMenu()) {
				menuMap.get(toolConfig.getMenuParentId()).getItems().add(menuMap.get(toolConfig.getMenuId()));
			}
		}

		for (ToolFxmlLoaderConfiguration toolConfig : toolList) {
			if (toolConfig.getIsMenu()) {
				continue;
			}
			MenuItem menuItem = new MenuItem(toolConfig.getTitle());
			if (StringUtils.isNotEmpty(toolConfig.getIconPath())) {
				ImageView imageView = new ImageView(new Image(toolConfig.getIconPath()));
				imageView.setFitHeight(18);
				imageView.setFitWidth(18);
				menuItem.setGraphic(imageView);
			}
			if ("Node".equals(toolConfig.getControllerType())) {
				menuItem.setOnAction((ActionEvent event) -> {
					if(StringUtils.isNotEmpty(toolConfig.getUrl())){
						addContent(menuItem.getText(), toolConfig.getUrl(), toolConfig.getResourceBundleName(),
								toolConfig.getIconPath());
					}else{
						addContent(menuItem.getText(),toolConfig.getClassName(), toolConfig.getIconPath());
					}
				});
				if (toolConfig.getIsDefaultShow()) {
					if(StringUtils.isNotEmpty(toolConfig.getUrl())){
						addContent(menuItem.getText(), toolConfig.getUrl(), toolConfig.getResourceBundleName(),
								toolConfig.getIconPath());
					}else{
						addContent(menuItem.getText(),toolConfig.getClassName(), toolConfig.getIconPath());
					}
				}
			} else if ("WebView".equals(toolConfig.getControllerType())) {
				menuItem.setOnAction((ActionEvent event) -> {
					addWebView(menuItem.getText(), toolConfig.getUrl(), toolConfig.getIconPath());
				});
				if (toolConfig.getIsDefaultShow()) {
					addWebView(menuItem.getText(), toolConfig.getUrl(), toolConfig.getIconPath());
				}
			}
			menuMap.get(toolConfig.getMenuParentId()).getItems().add(menuItem);
			menuItemMap.put(menuItem.getText(), menuItem);
		}
	}

	private void initEvent() {

	}

	private void initService() {
		indexService.setBundle(bundle);
		indexService.setMenuItemMap(menuItemMap);
	}

	@FXML
	private void exitAction(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	private void closeAllTabAction(ActionEvent event) {
		tabPaneMain.getTabs().clear();
	}

	@FXML
	private void openAllTabAction(ActionEvent event) {
		for (MenuItem value : menuItemMap.values()) {
			value.fire();
		}
	}

	/**
	 * @Title: addContent
	 * @Description: 添加Content内容
	 */
	private void addContent(String title, String className, String iconPath) {
		try {
			Class<AbstractFxmlView> viewClass = (Class<AbstractFxmlView>) ClassLoader.getSystemClassLoader().loadClass(className);
			AbstractFxmlView fxmlView = SpringUtil.getBean(viewClass);
			Tab tab = new Tab(title);
			try {
				tab.setContent(fxmlView.getView());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (StringUtils.isNotEmpty(iconPath)) {
				ImageView imageView = new ImageView(new Image(iconPath));
				imageView.setFitHeight(18);
				imageView.setFitWidth(18);
				tab.setGraphic(imageView);
			}
			tabPaneMain.getTabs().add(tab);
			tabPaneMain.getSelectionModel().select(tab);
			tab.setOnCloseRequest((Event event)->{
				JavaFxViewUtil.setControllerOnCloseRequest(fxmlView.getPresenter(),event);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: addContent
	 * @Description: 添加Content内容
	 */
	private void addContent(String title, String url, String resourceBundleName, String iconPath) {
		try {
			FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(getClass().getResource(url));
			if (StringUtils.isNotEmpty(resourceBundleName)) {
				ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName, Config.defaultLocale);
				generatingCodeFXMLLoader.setResources(resourceBundle);
			}

			Tab tab = new Tab(title);
			if (StringUtils.isNotEmpty(iconPath)) {
				ImageView imageView = new ImageView(new Image(iconPath));
				imageView.setFitHeight(18);
				imageView.setFitWidth(18);
				tab.setGraphic(imageView);
			}

			tab.setContent(generatingCodeFXMLLoader.load());
			tabPaneMain.getTabs().add(tab);
			tabPaneMain.getSelectionModel().select(tab);
			tab.setOnCloseRequest((Event event)->{
				JavaFxViewUtil.setControllerOnCloseRequest(generatingCodeFXMLLoader.getController(),event);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: addWebView
	 * @Description: 添加WebView视图
	 */
	private void addWebView(String title, String url, String iconPath) {
		WebView browser = new WebView();
		WebEngine webEngine = browser.getEngine();
		if(url.startsWith("http")){
			webEngine.load(url);
		}else{
			webEngine.load(IndexController.class.getResource(url).toExternalForm());
		}

		Tab tab = new Tab(title);
		if (StringUtils.isNotEmpty(iconPath)) {
			ImageView imageView = new ImageView(new Image(iconPath));
			imageView.setFitHeight(18);
			imageView.setFitWidth(18);
			tab.setGraphic(imageView);
		}
		tab.setContent(browser);
		tabPaneMain.getTabs().add(tab);
		tabPaneMain.getSelectionModel().select(tab);
	}
}
