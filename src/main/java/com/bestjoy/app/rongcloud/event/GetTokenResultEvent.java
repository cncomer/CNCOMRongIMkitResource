package com.bestjoy.app.rongcloud.event;

import android.text.TextUtils;

/**
 * Created by bestjoy on 15/7/23.
 */
public class GetTokenResultEvent {
    public int mCallId;
    public String mErrorMessage;
    public String mToken;
    public int mCode;

    public Object mObject;

    public boolean hasGetToken() {
        return !TextUtils.isEmpty(mToken);
    }
}
