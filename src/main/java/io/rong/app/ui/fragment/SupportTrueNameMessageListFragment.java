package io.rong.app.ui.fragment;

/**
 * Created by bestjoy on 15/7/22.
 */

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bestjoy.app.rongimuikit.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.rong.app.adapter.SupportTrueNameMessageListAdapter;
import io.rong.imkit.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.logic.LibException;
import io.rong.imkit.logic.MessageLogic;
import io.rong.imkit.model.ConversationTypeFilter;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.InputView.Event;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.PublicServiceInfo;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;

public class SupportTrueNameMessageListFragment extends UriFragment
        implements AbsListView.OnScrollListener {
    SupportTrueNameMessageListAdapter mAdapter;
    ListView mList;
    Conversation mConversation;
    static final int REQ_LIST = 1;
    static final int RENDER_LIST = 2;
    static final int RENDER_HISTORY = 6;
    static final int DELETE_MESSAGE = 11;
    static final int NOTIFY_LIST = 9;
    static final int REFRESH_LIST = 3;
    static final int SET_LAST = 10;
    static final int REFRESH_ITEM = 4;
    static final int REQ_HISTORY = 5;
    static final int SET_SMOOTH_SCROLL = 8;

    private static final int LISTVIEW_SHOW_COUNT = 5;
    View mHeaderView;
    ConversationTypeFilter mFilter;
    boolean mIsLoading;
    boolean mHasMore;

    public SupportTrueNameMessageListFragment() {
        mIsLoading = true;
        mHasMore = false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RongContext.getInstance().getEventBus().register(this);
        mAdapter = new SupportTrueNameMessageListAdapter(getActivity());
    }

    protected void initFragment(Uri uri) {
        String typeStr = uri.getLastPathSegment().toUpperCase();
        Conversation.ConversationType type = Conversation.ConversationType.valueOf(typeStr);

        String targetId = uri.getQueryParameter("targetId");

        String title = uri.getQueryParameter("title");
        //是否使用昵称,否则使用真名
        String userNickName = uri.getQueryParameter("userNickName");
        mAdapter.setIsShowNickName("true".equals(userNickName));
        if ((TextUtils.isEmpty(targetId)) || (type == null))
            return;

        mConversation = Conversation.obtain(type, targetId, title);

        if (mAdapter != null) {
            getHandler().post(new Runnable() {
                public void run() {
                    mAdapter.clear();
                    mAdapter.notifyDataSetChanged();
                }

            });
        }

        if (TextUtils.isEmpty(title)) {
            RongIM.getInstance().getRongIMClient().getConversation(mConversation.getConversationType(), mConversation.getTargetId(), new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    if (conversation != null) {
                        if (!(TextUtils.isEmpty(mConversation.getConversationTitle())))
                            conversation.setConversationTitle(mConversation.getConversationTitle());

                        mConversation = conversation;
                    }
                    getHandler().sendEmptyMessage(REQ_LIST);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    io.rong.imkit.RLog.e(this, "fail", errorCode.toString());
                }
            });
        } else {
            getHandler().sendEmptyMessage(REQ_LIST);
        }

        RongIM.getInstance().getRongIMClient().clearMessagesUnreadStatus(mConversation.getConversationType(), mConversation.getTargetId(), null);

        mFilter = ConversationTypeFilter.obtain(new Conversation.ConversationType[] { mConversation.getConversationType() });
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rc_fr_messagelist, container, false);
        mList = ((ListView)findViewById(view, R.id.rc_list));
        mHeaderView = inflater.inflate(R.layout.rc_item_progress, null);
        mList.addHeaderView(mHeaderView);
        mList.setOnScrollListener(this);
        mList.setSelectionAfterHeaderView();

        mAdapter.setOnItemHandlerListener(new MessageListAdapter.OnItemHandlerListener() {
            @Override
            public void onWarningViewClick(int i, Message data, final View view) {
                boolean isSendComplete = true;
                Object obj = view.getTag();

                if (obj != null) {
                    isSendComplete = ((Boolean)obj).booleanValue();
                } else {
                    view.setTag(Boolean.valueOf(false));
                }

                if (!(isSendComplete))
                    return;

                MessageContent content = data.getContent();
                io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(data.getTargetId(), data.getConversationType(), content);
                RongIM.getInstance().getRongIMClient().deleteMessages(new int[] { data.getMessageId() });

                if (content instanceof ImageMessage) {
                    ImageMessage msg = (ImageMessage)content;
                    content = ImageMessage.obtain(msg.getLocalUri(), msg.getLocalUri());
                    message.setContent(content);

                    RongIM.getInstance().getRongIMClient().sendImageMessage(message, null, null, new RongIMClient.SendImageMessageCallback() {
                        @Override
                        public void onAttached(Message message) {

                        }

                        @Override
                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                            view.setTag(Boolean.valueOf(true));
                        }

                        @Override
                        public void onSuccess(Message message) {
                            view.setTag(Boolean.valueOf(true));
                        }

                        @Override
                        public void onProgress(Message message, int i) {

                        }

                    });
                } else {
                    RongIM.getInstance().getRongIMClient().sendMessage(message, null, null, new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onSuccess(Integer integer) {
                            view.setTag(Boolean.valueOf(true));
                        }

                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                            view.setTag(Boolean.valueOf(true));
                        }
                    });
                }

            }
        });
        return view;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if ((firstVisibleItem == 0) && (mAdapter.getCount() > 0) && (!(mIsLoading)) && (mHasMore)) {
            mIsLoading = true;
            getHandler().sendEmptyMessage(REQ_HISTORY);
        }
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getActionBarHandler() != null) {
            getActionBarHandler().onTitleChanged(mConversation.getConversationTitle());
        }

        getView().setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                io.rong.imkit.RLog.d(this, "View", "Touch");
                return false;
            }

        });
        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RongContext.getInstance().getPrimaryInputProvider().onInactive(view.getContext());
                RongContext.getInstance().getSecondaryInputProvider().onInactive(view.getContext());
            }

        });
        super.onViewCreated(view, savedInstanceState);
    }

    public boolean onBackPressed() {
        return false;
    }

    private List<UIMessage> filterMessage(List<UIMessage> srcList) {
        List destList = null;
        if (mAdapter.getCount() > 0) {
            destList = new ArrayList();
            for (int i = 0; i < mAdapter.getCount(); ++i) {
                Iterator<UIMessage> iterator = srcList.iterator();
                io.rong.imlib.model.Message msg;
                while (iterator.hasNext()) {
                    msg = iterator.next();
                    if (!(destList.contains(msg)))
                        continue;
                    if (msg.getMessageId() != mAdapter.getItem(i).getMessageId()){
                        destList.add(msg);
                    }
                }
            }
        } else {
            destList = srcList;
        }

        return destList;
    }

    public boolean handleMessage(android.os.Message msg) {
        List list;
        UIMessage message;
        switch (msg.what) {
            case REQ_LIST:
                mAdapter.clear();
                MessageLogic.getInstance().getLatestMessages(mConversation.getConversationType(), mConversation.getTargetId(), 30, new RongIMClient.ResultCallback<List<UIMessage>>() {
                    public void onSuccess(List<UIMessage> messages) {
                        getHandler().obtainMessage(RENDER_LIST, messages).sendToTarget();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        io.rong.imkit.RLog.e(this, "IPC:getConversationMessage", errorCode.toString());
                }
            });
                break;
            case RENDER_LIST:
                if (msg.obj instanceof List) {
                    list = (List)msg.obj;
                    if (list.size() < 30) {
                        mHasMore = false;
                        mList.removeHeaderView(mHeaderView);
                        mHeaderView = null;
                    } else {
                        mHasMore = true;
                    }
                    mAdapter.clear();
                    mAdapter.addCollection(filterMessage(list));
                    if(list.size() <= 5) {
                        this.mList.setStackFromBottom(false);
                        this.mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    } else {
                        this.mList.setStackFromBottom(true);
                        this.mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    }
                    mAdapter.notifyDataSetChanged();
                    getHandler().sendEmptyMessage(SET_LAST);
                    io.rong.imkit.RLog.d(this, "RENDER_LIST", "count:" + list.size());
                }  else {
                    mHasMore = false;
                    mList.removeHeaderView(mHeaderView);
                    mHeaderView = null;
                }
                mIsLoading = false;
                break;
            case REFRESH_LIST:
                UIMessage model1 = (UIMessage)msg.obj;
                if(this.mAdapter.findPosition(model1) < 0 && msg.obj instanceof UIMessage) {
                    this.mAdapter.add(model1);
                }

                if(this.mList.getLastVisiblePosition() <= this.mList.getCount() - 1) {
                    this.mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                } else {
                    this.mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    this.mList.setStackFromBottom(true);
                }

                this.mAdapter.notifyDataSetChanged();
                RLog.d(this, "Handler", this.mList.getLastVisiblePosition() + ":" + this.mAdapter.getCount());
                break;
            case REFRESH_ITEM:
                int position1 = ((Integer)msg.obj).intValue();

                if (position1 >= this.mList.getFirstVisiblePosition() && position1 <= this.mList.getLastVisiblePosition()) {
                    RLog.d(this, "REFRESH_ITEM", "Index:" + position1);
                    this.mAdapter.getView(position1, this.mList.getChildAt(position1 - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
                }
                this.mList.setSelection(this.mAdapter.getCount() - 1);
                break;
            case REQ_HISTORY:
                io.rong.imkit.RLog.d(this, "MessageListFragment", "REQ_HISTORY");

                message = (UIMessage)this.mAdapter.getItem(0);

                MessageLogic.getInstance().getHistoryMessages(mConversation.getConversationType(), mConversation.getTargetId(), message.getMessageId(), 30, new RongIMClient.ResultCallback<List<UIMessage>>() {
                    @Override
                    public void onSuccess(List<UIMessage> messages) {
                        getHandler().obtainMessage(RENDER_HISTORY, messages).sendToTarget();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        io.rong.imkit.RLog.e(this, "IPC:getConversationMessage", errorCode.toString());
                    }

                });
                break;
            case RENDER_HISTORY:
                io.rong.imkit.RLog.d(this, "MessageListFragment", "RENDER_HISTORY");
                if (msg.obj instanceof List) {
                    list = (List) msg.obj;
                    if (list.size() < 30) {
                        mHasMore = false;
                        mList.removeHeaderView(mHeaderView);
                        mHeaderView = null;
                    } else {
                        mHasMore = true;
                    }
                    Collections.reverse(list);
                    Iterator position = list.iterator();
                    while(position.hasNext()) {
                        message = (UIMessage)position.next();
                        this.mAdapter.add(message, 0);
                    }
                    mList.setTranscriptMode(0);
                    mList.setStackFromBottom(false);
                    int index = mList.getFirstVisiblePosition();

                    mAdapter.notifyDataSetChanged();

                    if (index == 0)  mList.setSelection(list.size());
                } else {
                    mHasMore = false;
                    mList.removeHeaderView(mHeaderView);
                    mHeaderView = null;
                }
                mIsLoading = false;
                break;

            case SET_SMOOTH_SCROLL:
                break;
            case NOTIFY_LIST:
                if (mAdapter != null) mAdapter.notifyDataSetChanged();
                break;
            case SET_LAST:
                this.resetListViewStack();
                this.mAdapter.notifyDataSetChanged();
                break;
            case DELETE_MESSAGE:
                this.mAdapter.notifyDataSetChanged();
                this.getHandler().post(new Runnable() {
                    public void run() {
                        if (mList.getCount() > 0) {
                            View firstView = mList.getChildAt(mList.getFirstVisiblePosition());
                            View lastView = mList.getChildAt(mList.getLastVisiblePosition());
                            if (firstView != null && lastView != null) {
                                int listViewPadding = mList.getListPaddingBottom() + mList.getListPaddingTop();
                                int childViewsHeight = lastView.getBottom() - (firstView.getTop() == -1 ? 0 : firstView.getTop());
                                int listViewHeight = mList.getBottom() - listViewPadding;
                                RLog.e(this, "handle-DELETE_MESSAGE", "firstView-top-height:" + firstView.getTop());
                                RLog.e(this, "handle-DELETE_MESSAGE", "lastView-bottom-height:" + lastView.getBottom());
                                RLog.e(this, "handle-DELETE_MESSAGE", "childViews-height:" + childViewsHeight);
                                RLog.e(this, "handle-DELETE_MESSAGE", "listView-height:" + listViewHeight);
                                if (childViewsHeight < listViewHeight) {
                                    mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                                    mList.setStackFromBottom(false);
                                } else {
                                    mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                                }

                                mAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                });
        }

        return false;
    }

    private void resetListViewStack() {
        if(this.mList.getFirstVisiblePosition() == 0) {
            View firstView = this.mList.getChildAt(this.mList.getFirstVisiblePosition());
            View lastView = this.mList.getChildAt(this.mList.getLastVisiblePosition());
            if(firstView != null && lastView != null) {
                int listViewPadding = this.mList.getListPaddingBottom() + this.mList.getListPaddingTop();
                int childViewsHeight = lastView.getBottom() - (firstView.getTop() == -1?0:firstView.getTop());
                int listViewHeight = this.mList.getBottom() - listViewPadding;
                if(childViewsHeight < listViewHeight) {
                    this.mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    this.mList.setStackFromBottom(false);
                } else {
                    this.mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                }
            }
        } else {
            this.mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            this.mList.setStackFromBottom(true);
        }

    }

    public void onEventMainThread(io.rong.imlib.model.Message msg) {
        UIMessage message = UIMessage.obtain(msg);
        io.rong.imkit.RLog.d(this, "onEventBackgroundThread", "message : " + message.toString());
        if ((mConversation != null) && (mConversation.getTargetId().equals(message.getTargetId())) && (mConversation.getConversationType() == message.getConversationType())) {
            int position = mAdapter.findPosition(message);
            if (position == -1) {
                io.rong.imkit.RLog.d(this, "onEventBackgroundThread", "REFRESH_LIST : ");
                getHandler().obtainMessage(REFRESH_LIST, message).sendToTarget();
            } else {
                io.rong.imkit.RLog.d(this, "onEventBackgroundThread", "REFRESH_ITEM : status=" + message.getSentStatus());
                mAdapter.getItem(position).setSentStatus(message.getSentStatus());
                mAdapter.getItem(position).setExtra(message.getExtra());
                getHandler().obtainMessage(REFRESH_ITEM, Integer.valueOf(position)).sendToTarget();
            }
        }
    }

    public void onEventMainThread(io.rong.imkit.model.Event.OnMessageSendErrorEvent event) {
        io.rong.imlib.model.Message msg = event.getMessage();
        onEventMainThread(msg);
    }

    public void onEventMainThread(io.rong.imkit.model.Event.OnReceiveMessageEvent event) {
        onEventMainThread(event.getMessage());
    }

    public void onEventMainThread(MessageContent messageContent) {
        if ((mList != null) && (isResumed())) {
            int first = mList.getFirstVisiblePosition() - mList.getHeaderViewsCount();
            int last = mList.getLastVisiblePosition() - mList.getHeaderViewsCount();

            int index = first - 1;
            do
                if ((++index > last) || (index < 0) || (index >= this.mAdapter.getCount())) return;
            while (!(mAdapter.getItem(index).getContent().equals(messageContent)));
            this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
        }
    }

    public void onEventMainThread(io.rong.imkit.model.Event.OnReceiveMessageProgressEvent event) {
        if ((mList != null) && (isResumed())) {
            int first = mList.getFirstVisiblePosition() - mList.getHeaderViewsCount();
            int last = mList.getLastVisiblePosition() - mList.getHeaderViewsCount();

            int index = first - 1;
            do
                if ((++index > last) || (index < 0) || (index >= mAdapter.getCount())) return;
            while (mAdapter.getItem(index).getMessageId() != event.getMessage().getMessageId());
            mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition() + mList.getHeaderViewsCount()), mList);
            RongContext.getInstance().setMessageProgress(event.getMessage().getMessageId(), event.getProgress());
        }
    }

    public void onEventMainThread(Event event) {
        io.rong.imkit.RLog.d(this, "Input_event", event.toString());
        if (event == Event.ACTION) {
            if (mAdapter == null)
                return;

            getHandler().sendEmptyMessageDelayed(SET_LAST, 500L);
        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        if ((mList != null) && (isResumed())) {
            int first = mList.getFirstVisiblePosition() - mList.getHeaderViewsCount();
            int last = mList.getLastVisiblePosition() - mList.getHeaderViewsCount();

            int index = first - 1;

            while ((++index <= last) && (index >= 0) && (index < mAdapter.getCount()))
            {
                io.rong.imlib.model.Message message = mAdapter.getItem(index);

                if ((message != null) && (((TextUtils.isEmpty(message.getSenderUserId())) || (userInfo.getUserId().equals(message.getSenderUserId())))))
                    mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition() + mList.getHeaderViewsCount()), mList);
            }
        }
    }

    public void onEventMainThread(PublicServiceInfo publicServiceInfo) {
        if ((mList != null) && (isResumed()) && (mAdapter != null)) {
            int first = mList.getFirstVisiblePosition() - mList.getHeaderViewsCount();
            int last = mList.getLastVisiblePosition() - mList.getHeaderViewsCount();

            int index = first - 1;

            while ((++index <= last) && (index >= 0) && (index < mAdapter.getCount()))
            {
                io.rong.imlib.model.Message message = mAdapter.getItem(index);

                if ((message != null) && (((TextUtils.isEmpty(message.getTargetId())) || (publicServiceInfo.getTargetId().equals(message.getTargetId())))))
                    mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition() + mList.getHeaderViewsCount()), mList);
            }
        }
    }

    public void onPause()  {
        super.onPause();
        RongContext.getInstance().getEventBus().post(Event.INACTION);
    }

    public void onResume()  {
        super.onResume();

        if ((RongIM.getInstance() == null) || (RongIM.getInstance().getRongIMClient() == null)) {
            throw new ExceptionInInitializerError("RongIM hasn't been connected yet!!!");
        }

        RongIMClient.ConnectionStatusListener.ConnectionStatus status = RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus();
        if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE)) {
            showNotification(getResources().getString(R.string.rc_notice_network_unavailable));
            RongIM.getInstance().getRongIMClient().reconnect(null);
        } else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT)) {
            showNotification(getResources().getString(R.string.rc_notice_tick));
        } else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
            hiddenNotification();
        } else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
            showNotification(getResources().getString(R.string.rc_notice_disconnect));
        }
    }

    public void onEventMainThread(LibException e) {
        showNotification(e.toString());
    }

    public void onEventMainThread(final RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
        io.rong.imkit.RLog.d(this, "ConnectionStatus", status.toString() + " " + toString());
        io.rong.imkit.RLog.d(this, "ConnectionStatus", "isResume() = " + isResumed());
        if (isResumed()) {
            getHandler().post(new Runnable() {
                public void run() {
                    if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE) {
                        showNotification(getResources().getString(R.string.rc_notice_network_unavailable));
                    } else if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                        showNotification(getResources().getString(R.string.rc_notice_tick));
                    } else if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                        hiddenNotification();
                        initFragment(getUri());
                    } else if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED) {
                        showNotification(getResources().getString(R.string.rc_notice_disconnect));
                    }
                }
            });
        }
    }

    public void onEventMainThread(io.rong.imkit.model.Event.MessagesClearEvent clearMessagesEvent) {
        if ((mConversation != null) && (clearMessagesEvent != null) &&
                (mConversation.getTargetId().equals(clearMessagesEvent.getTargetId()))) {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onEventBackgroundThread(io.rong.imkit.model.Event.MessageDeleteEvent deleteEvent) {
        if (deleteEvent.getMessageIds() != null) {
            boolean hasChanged = false;
            for (Iterator iterator = deleteEvent.getMessageIds().iterator(); iterator.hasNext(); ) {
                long item = ((Integer)iterator.next()).intValue();
                int position = mAdapter.findPosition(item);
                if (position >= 0) {
                    mAdapter.remove(position);
                    hasChanged = true;
                }
            }

            if (hasChanged)
                getHandler().post(new Runnable() {
                    public void run() {
                        getHandler().obtainMessage(DELETE_MESSAGE).sendToTarget();
                    }
                });
        }
    }

    public void onEventMainThread(io.rong.imkit.model.Event.PublicServiceFollowableEvent event) {
        io.rong.imkit.RLog.d(this, "onEventBackgroundThread", "PublicAccountIsFollowEvent, follow=" + event.isFollow());
        if ((event != null) && (!(event.isFollow())))
            getActivity().finish();
    }

    public void onEventBackgroundThread(io.rong.imkit.model.Event.MessagesClearEvent clearEvent) {
        if ((clearEvent.getTargetId().equals(mConversation.getTargetId())) && (clearEvent.getType().equals(mConversation.getConversationType()))) {
            mAdapter.removeAll();
            getHandler().post(new Runnable() {
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void onDestroy() {
        if (mConversation != null) {
            RongIM.getInstance().getRongIMClient().clearMessagesUnreadStatus(mConversation.getConversationType(), mConversation.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                public void onSuccess(Boolean bool) {
                    io.rong.imkit.RLog.d(this, "onDestroy", "post ConversationUnreadEvent.");
                    RongContext.getInstance().getEventBus().post(new io.rong.imkit.model.Event.ConversationUnreadEvent(mConversation.getConversationType(), mConversation.getTargetId()));
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }

        RongContext.getInstance().getEventBus().unregister(this);
        super.onDestroy();
    }

    public static class Builder {
        private Conversation.ConversationType conversationType;
        private String targetId;
        private Uri uri;

        public Conversation.ConversationType getConversationType()
        {
            return conversationType;
        }

        public void setConversationType(Conversation.ConversationType conversationType) {
            conversationType = conversationType;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            targetId = targetId;
        }
    }
}
