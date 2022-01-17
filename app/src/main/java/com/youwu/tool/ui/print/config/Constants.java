package com.youwu.tool.ui.print.config;


import com.youwu.tool.app.AppApplication;

public class Constants {

    public static final String PACKAGE = AppApplication.getInstance().getPackageName();
    public static final String ACTION_SPPCONNECT = PACKAGE + "_" + "action_sppconnect";
    public static final int template_background = 30;
    public static final int template_edit_to_print= 0x11;


    public static final int RESULTCODE_OPEN_BLE = 0x20;
    public static final int RESULTCODE_CLOSE_BLE = 0x21;
    public static final int REQUESTCODE_OPEN_BLE = 0x22;

}
