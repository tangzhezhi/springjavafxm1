package org.tang.springjavafxm1.fxmlView;

import com.jfoenix.controls.JFXDecorator;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.scene.Parent;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.context.annotation.Scope;
import org.tang.springjavafxm1.utils.Config;
import org.tang.springjavafxm1.utils.JavaFxViewUtil;

import java.util.Optional;
import java.util.ResourceBundle;

/** 
 * @ClassName: IndexView
 * @Description: 
 * @author: xufeng
 * @date: 2017/11/22 17:38  
 */
@Scope("prototype")
@FXMLView(value = "/org/tang/springjavafxm1/fxmlView/Index.fxml", bundle = "locale.Menu")
public class IndexView extends AbstractFxmlView {
    public IndexView() throws Exception {
        //反射修改默认语言
        ResourceBundle bundle = ResourceBundle.getBundle(this.getResourceBundle().get().getBaseBundleName(), Config.defaultLocale);
        FieldUtils.writeField(this,"bundle",Optional.ofNullable(bundle),true);
        GUIState.getStage().setTitle(bundle.getString("Title"));//修改标题国际化
    }

    @Override
    public Parent getView() {
        JFXDecorator decorator = null;
        try {
            decorator = JavaFxViewUtil.getJFXDecorator(GUIState.getStage(), GUIState.getStage().getTitle() + Config.xJavaFxToolVersions,"/images/icon.jpg",super.getView());
        } catch (Exception e) {
            e.printStackTrace();
        }
        decorator.setOnCloseButtonAction(()->{System.exit(0);});
        return decorator;
    }
}
