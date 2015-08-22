package com.bestjoy.app.rongcloud.event;

/**
 * Created by bestjoy on 15/7/23.
 */
public class ConnectEvent {
    public String mToken = "";

    public boolean mIsReconnect = false;
    public boolean mIsConnect = false;

    public int mCallId;

    public Object mObject;
}
