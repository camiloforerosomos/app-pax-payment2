package com.example.paxpayment.service;

import android.content.Context;

import com.pax.poslink.CommSetting;
import com.pax.poslink.PosLink;
import com.pax.poslink.poslink.POSLinkCreator;

public class PosLinkSingleton {

    private static PosLink linkInstance;

    public static PosLink getLinkInstance(Context context) {
        if (linkInstance == null) {
            linkInstance = new PosLink();

            // Crear las commsettings

            CommSetting commSetting = new CommSetting();

            commSetting.setType(CommSetting.AIDL);

            commSetting.setTimeOut("60000");
            commSetting.setSerialPort("COM1");

            commSetting.setMacAddr("");
            commSetting.setEnableProxy(false);

            POSLinkCreator posLinkCreator = new POSLinkCreator();
            linkInstance = posLinkCreator.createPoslink(context);

            linkInstance.SetCommSetting(commSetting);
        }
        return linkInstance;
    }


}
