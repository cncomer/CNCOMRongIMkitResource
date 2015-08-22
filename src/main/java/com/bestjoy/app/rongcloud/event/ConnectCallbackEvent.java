package com.bestjoy.app.rongcloud.event;

/**
 * Created by bestjoy on 15/7/23.
 */
public class ConnectCallbackEvent {
    public String mToken = "";

    public boolean mIsConnect = false;

    public String mErrorMessage="";

    public int mCallId;

    public int mCode;

    public Object mObject;

}
