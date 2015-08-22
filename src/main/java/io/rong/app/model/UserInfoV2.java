package io.rong.app.model;

import android.net.Uri;
import android.os.Parcel;
import android.text.TextUtils;

import com.sea_monster.common.ParcelUtils;

import io.rong.imlib.model.UserInfo;

/**
 * Created by bestjoy on 15/7/22.
 */
public class UserInfoV2 extends UserInfo {
    private String mNickName="";

    public String mCompany="",mTitle="", mAddress="",mFrindid="",mLabels="";

    public UserInfoV2(String id, String name, Uri portraitUri) {
        super(id, name, portraitUri);
    }
    public UserInfoV2(String id, String name, String nickName, Uri portraitUri) {
        super(id, name, portraitUri);
        mNickName = nickName;
    }

    public UserInfoV2(String id, String name, String nickName, Uri portraitUri, String company, String title, String address, String friendid, String labels) {
        super(id, name, portraitUri);
        mNickName = nickName;
        mCompany = company;
        mTitle = title;
        mAddress = address;
        mFrindid = friendid;
        mLabels = labels;

    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public String getNickName() {
        //如果没有昵称，返回UserId
        if (TextUtils.isEmpty(mNickName)) {
            return getUserId();
        }
        return mNickName;
    }


    public UserInfoV2(Parcel in) {
        super(in);
        setNickName(ParcelUtils.readFromParcel(in));
    }

    public void writeToParcel(Parcel dest, int flags) {
       super.writeToParcel(dest, flags);
        ParcelUtils.writeToParcel(dest, getNickName());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("userId=").append(getUserId())
                .append(",name=").append(getName())
                .append(",nickName=").append(getNickName());
        sb.append("]");
        return sb.toString();
    }
}
