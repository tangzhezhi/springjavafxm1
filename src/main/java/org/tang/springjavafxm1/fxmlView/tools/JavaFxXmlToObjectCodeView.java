package org.tang.springjavafxm1.fxmlView.tools;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
@Lazy
@FXMLView
public class JavaFxXmlToObjectCodeView extends AbstractFxmlView {

}
