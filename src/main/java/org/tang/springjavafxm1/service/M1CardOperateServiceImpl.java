package org.tang.springjavafxm1.service;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.nfctools.mf.MfAccess;
import org.nfctools.mf.MfCardListener;
import org.nfctools.mf.classic.Key;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.tang.springjavafxm1.acr122.Acr122Device;
import org.tang.springjavafxm1.acr122.MifareUtils;

import javax.smartcardio.CardException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.tang.springjavafxm1.acr122.HexUtils.hexStringToBytes;

/**
 * m1 卡片操作
 */
@Component
public class M1CardOperateServiceImpl implements M1CardOperateService {


    @Override
    public void readPhysicCardNo(TextField physicCardField, TextField password, TextArea cardContentTextArea) {

        MfCardListener listener = (mfCard, mfReaderWriter) -> {
            String key = "FFFFFFFFFFFF";
            byte[] keyBytes = null;
            try {
                if(StringUtils.isEmpty(password.getText())){
                    keyBytes = hexStringToBytes(key);
                }
                else{
                    keyBytes = hexStringToBytes(password.getText());
                }

                // Reading with key A
                MfAccess access = new MfAccess(mfCard, 0, 0, Key.A, keyBytes);
                String cardNo1 = MifareUtils.readMifareClassic1KBlock(mfReaderWriter, access);

                List<String> keys = new ArrayList<>();
                keys.add(key);

                List<String> data = MifareUtils.readMifareClassic1KCard(mfReaderWriter,mfCard,keys);

                System.out.println("cardNo::"+cardNo1);

                if(StringUtils.hasText(cardNo1)){
                    String cardTmp = Long.valueOf(cardNo1.substring(0,8),16).toString();
                    physicCardField.setText(cardTmp);


                    String dataArea = data.stream().
//                            map(s->{
//                        return s+"\n";
//                    }).
                    collect(Collectors.joining());

                    cardContentTextArea.setText(dataArea);
                }

            } catch (CardException ce) {
                System.out.println("Card removed or not present.");
            }
        };

        // Start listening
        try {
            listen(listener);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    ;


    /**
     * Listens for cards using the provided listener.
     * @param listener a listener
     */
    private static void listen(MfCardListener listener) throws IOException {
        Acr122Device acr122;
        try {
            acr122 = new Acr122Device();
        } catch (RuntimeException re) {
            System.out.println("No ACR122 reader found.");
            return;
        }

        String name = acr122.getCardTerminal().getName();

        if(!StringUtils.hasText(name)){
            acr122.close();
            acr122.open();
            acr122.listen(listener);
        }
        else{
            acr122.listen(listener);
        }
    }



}
