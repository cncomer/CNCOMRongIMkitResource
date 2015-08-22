package io.rong.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bestjoy.app.rongimuikit.R;

import io.rong.imkit.fragment.ConversationFragment;

/**
 * Created by bestjoy on 15/7/23.
 */
public class SupportTrueNameConversationFragment extends ConversationFragment {
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rc_fr_conversation, container, false);
        return view;
    }

}
