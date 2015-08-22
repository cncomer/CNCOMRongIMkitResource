package io.rong.app.adapter;

/**
 * Created by bestjoy on 15/7/22.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bestjoy.app.rongimuikit.R;
import io.rong.app.model.UserInfoV2;

import io.rong.imkit.RongContext;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.model.UserInfo;

public class SupportTrueNameMessageListAdapter extends MessageListAdapter {
    private static final String TAG = "SupportTrueNameMessageListAdapter";
    LayoutInflater mInflater;
    Context mContext;
    Drawable mDefaultDrawable;
    private boolean mIsShowNickName;

    public SupportTrueNameMessageListAdapter(Context context) {
        super(context);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mDefaultDrawable = context.getResources().getDrawable(R.drawable.rc_ic_def_msg_portrait);
    }

    public void setIsShowNickName(boolean showNickName) {
        mIsShowNickName = showNickName;
    }


    protected View newView(Context context, int position, ViewGroup group) {
        View view = super.newView(context, position, group);
//        View result = this.mInflater.inflate(R.layout.rc_item_message, null);
//
//        ViewHolder holder = new ViewHolder();
//        holder.leftIconView = ((AsyncImageView) result.findViewById(R.id.rc_left));
//        holder.rightIconView = ((AsyncImageView) result.findViewById(R.id.rc_right));
//        holder.nameView = ((TextView) result.findViewById(R.id.rc_title));
//        holder.contentView = ((ProviderContainerView) result.findViewById(R.id.rc_content));
//        holder.layout = ((ViewGroup) result.findViewById(R.id.rc_layout));
//        holder.progressBar = ((ProgressBar) result.findViewById(R.id.rc_progress));
//        holder.warning = ((ImageView) result.findViewById(R.id.rc_warning));
//        holder.time = ((TextView) result.findViewById(R.id.rc_time));
//
//        result.setTag(holder);

        return view;
    }

    protected void bindView(View v, int position, UIMessage data) {
        super.bindView(v, position, data);
        TextView name = ((TextView) v.findViewById(R.id.rc_title));
        UserInfoV2 userInfoV2 = null;
        UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(data.getSenderUserId());
        if (userInfo == null) {
            userInfoV2 = new UserInfoV2(data.getSenderUserId(), null, null);
        } else if (userInfo instanceof UserInfoV2){
            userInfoV2 = (UserInfoV2) userInfo;
        } else if (userInfo instanceof UserInfo) {
            userInfoV2 = new UserInfoV2(userInfo.getUserId(), userInfo.getName(), userInfo.getPortraitUri());
        }
        name.setText(!mIsShowNickName?userInfoV2.getName():userInfoV2.getNickName());
        Log.d(TAG, "bindView " + userInfoV2);

    }
}