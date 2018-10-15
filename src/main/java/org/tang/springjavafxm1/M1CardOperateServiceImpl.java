package org.tang.springjavafxm1;

import javafx.scene.control.TextField;
import org.nfctools.mf.MfAccess;
import org.nfctools.mf.MfCardListener;
import org.nfctools.mf.MfReaderWriter;
import org.nfctools.mf.card.MfCard;
import org.nfctools.mf.classic.Key;
import org.nfctools.spi.acs.Acr122ReaderWriter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.tang.springjavafxm1.acr122.Acr122Device;
import org.tang.springjavafxm1.acr122.MifareUtils;

import javax.smartcardio.CardException;
import java.io.IOException;

import static org.tang.springjavafxm1.acr122.HexUtils.hexStringToBytes;

/**
 * m1 卡片操作
 */
@Component
public class M1CardOperateServiceImpl implements  M1CardOperateService {

    @Override
    public void readPhysicCardNo(TextField physicCardField) {

        MfCardListener listener = (mfCard, mfReaderWriter) -> {
            try {
                String key = "FFFFFFFFFFFF";
                byte[] keyBytes = hexStringToBytes(key);
                // Reading with key A
                MfAccess access = new MfAccess(mfCard, 0, 0, Key.A, keyBytes);
                String cardNo1 = MifareUtils.readMifareClassic1KBlock(mfReaderWriter, access);
                System.out.println("cardNo::"+cardNo1);

                if(StringUtils.hasText(cardNo1)){
                    String cardTmp = Long.valueOf(cardNo1.substring(0,8),16).toString();
                    physicCardField.setText(cardTmp);
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
