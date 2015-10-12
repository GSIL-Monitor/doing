package com.qihoo.around.sharecore;

import com.qihoo.around.sharecore.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by renjh1 on 15-1-12.
 */
public class ShareDialog extends Dialog implements View.OnClickListener {
    private IShareEvent eventListenner;

    public ShareDialog(Context context, IShareEvent eventCallback, OnCancelListener cancelListener) {
        this(context,eventCallback,cancelListener,false);
    }

    public ShareDialog(Context context, IShareEvent eventCallback, OnCancelListener cancelListener,boolean isNightMode) {
        super(context, R.style.SHARE_DIALOG_TRANSPARENCY);
        if (isNightMode){
            setContentView(R.layout.share_core_dialog_night);
        }else{
            setContentView(R.layout.share_core_dialog);
        }
        this.eventListenner = eventCallback;
        if (cancelListener != null) {
            setCancelable(true);
            setOnCancelListener(cancelListener);
        }
        initUI();
    }

    private void initUI() {
        findViewById(R.id.share_item_cope_link).setOnClickListener(this);
        //findViewById(R.id.share_item_douban).setOnClickListener(this);
        findViewById(R.id.share_item_weixin_friends).setOnClickListener(this);
        findViewById(R.id.share_item_weixin_timeline).setOnClickListener(this);
        findViewById(R.id.share_item_qq).setOnClickListener(this);
        findViewById(R.id.share_item_qzone).setOnClickListener(this);
        findViewById(R.id.share_item_weibo).setOnClickListener(this);
        findViewById(R.id.share_cancel_btn).setOnClickListener(this);
        findViewById(R.id.share_item_msg).setOnClickListener(this);
        findViewById(R.id.share_item_more).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v
     *            The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (eventListenner == null) {
            Toast.makeText(getContext(), R.string.share_callback_is_null, Toast.LENGTH_SHORT).show();
            return;
        }
        int i = v.getId();
        if (i == R.id.share_cancel_btn) {
            eventListenner.onEventCancel();
        } else if (i == R.id.share_item_cope_link) {
            eventListenner.onEventClipBoard();
        } else if (i == R.id.share_item_weixin_friends) {
            eventListenner.onEventWeixinFriends();
        } else if (i == R.id.share_item_weixin_timeline) {
            eventListenner.onEventWeixinTimeline();
        } else if (i == R.id.share_item_weibo) {
            eventListenner.onEventWeibo();
        } /*else if (i == R.id.share_item_douban) {
            eventListenner.onEventDouban();
        }*/ else if (i == R.id.share_item_qq) {
            eventListenner.onEventQQ();
        } else if (i == R.id.share_item_qzone) {
            eventListenner.onEventQZone();
        } else if (i== R.id.share_item_msg) {
        	eventListenner.onEventMessage();
        } else if(i == R.id.share_item_more) {
        	eventListenner.onEventShareMore();
        }
        dismiss();
    }
}
