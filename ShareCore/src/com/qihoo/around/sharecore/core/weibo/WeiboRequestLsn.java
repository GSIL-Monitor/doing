package com.qihoo.around.sharecore.core.weibo;

import android.content.Context;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

public class WeiboRequestLsn implements RequestListener {
    private static WeiboRequestLsn instance;
    private static volatile Object sObject = new Object();
    private Context appContext;

    public static WeiboRequestLsn getInstance(Context appConText) {
        synchronized (sObject) {
            if (instance == null) {
                instance = new WeiboRequestLsn(appConText);
            }
            return instance;
        }
    }

    private WeiboRequestLsn(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public void onComplete(String arg0) {
        Toast.makeText(appContext, "分享成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWeiboException(WeiboException arg0) {
        String msg = (arg0 == null || arg0.getMessage() == null) ? "分享失败" : ("分享失败: " + arg0
                .getMessage());
        Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
    }

}
