package org.tang.springjavafxm1.service;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * m1 卡片操作
 */
public interface M1CardOperateService {

    /**
     * 读物理卡号
     * @param physicCardField
     * @param cardContentTextArea
     * @return
     */
    void readPhysicCardNo(TextField physicCardField, TextField password, TextArea cardContentTextArea);

}
