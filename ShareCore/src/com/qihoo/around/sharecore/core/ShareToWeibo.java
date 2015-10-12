package com.qihoo.around.sharecore.core;

import com.qihoo.around.sharecore.core.weibo.StatusesAPI;
import com.qihoo.around.sharecore.openid.OpenId;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

public class ShareToWeibo {
    private static Oauth2AccessToken mAccessToken;

    public static IWeiboShareAPI get(Activity activity) {
        return WeiboShareSDK.createWeiboAPI(activity, OpenId.WEIBO_APP_ID);
    }

    /**
     * 上传图片并发布一条新微博。调用该方法之前,必须调用
     * {@link ShareToWeibo#isAuthed(android.content.Context)} 检查是否已经认证,如果没有,先调用
     * {@link ShareToWeibo#weiboAuth(android.app.Activity, com.sina.weibo.sdk.auth.sso.SsoHandler, com.sina.weibo.sdk.auth.WeiboAuthListener)}
     * 进行认证之后再进行分享
     *
     * @param content
     *            分享的文本信息 内部可以添加链接
     * @param shareBitmap
     *            分享的图片
     * @param listener
     *            回调
     */
    public static void share(Context appContext, String content, Bitmap shareBitmap,
            RequestListener listener) {
        StatusesAPI mStatusesAPI = new StatusesAPI(appContext, mAccessToken);
        mStatusesAPI.upload(content, shareBitmap, null, null, listener);
    }

    /**
     * 上传图片并发布一条新微博。调用该方法之前,必须调用
     * {@link ShareToWeibo#isAuthed(android.content.Context)} 检查是否已经认证,如果没有,先调用
     * {@link ShareToWeibo#weiboAuth(android.app.Activity, com.sina.weibo.sdk.auth.sso.SsoHandler, com.sina.weibo.sdk.auth.WeiboAuthListener)}
     * 进行认证之后再进行分享
     *
     * @param content
     *            分享的文本信息 内部可以添加链接
     * @param listener
     *            回调
     */
    public static void share(Context appContext, String content, RequestListener listener) {
        share(appContext, content, null, null, listener);
    }

    static void share(Context context, String content, String lat, String lon,
            RequestListener listener) {
        StatusesAPI mStatusesAPI = new StatusesAPI(context, mAccessToken);
        mStatusesAPI.update(content, lat, lon, listener);
    }

    /**
     *
     * @param activity
     * @param mSsoHandler
     *            针对sso认证,需要在onActiviyResult中调用
     *            {@link com.sina.weibo.sdk.auth.sso.SsoHandler#authorizeCallBack(int, int, android.content.Intent)
     *            的} , 所以创建{@link com.sina.weibo.sdk.auth.sso.SsoHandler}
     *            实例时,需要调用 {@link #getWeiboAuth(android.content.Context)}
     * @param authListener
     */
    public static void weiboAuth(final Activity activity, SsoHandler mSsoHandler,
            final WeiboAuthListener authListener) {
        WeiboAuthListener listener = new WeiboAuthListener() {
            @Override
            public void onWeiboException(WeiboException arg0) {
                if (authListener != null) {
                    authListener.onWeiboException(arg0);
                }
                Log.e("WeiboAuth", "onWeiboException: " + arg0.getMessage());
            }

            @Override
            public void onComplete(Bundle values) {
                mAccessToken = Oauth2AccessToken.parseAccessToken(values);
                if (mAccessToken.isSessionValid()) {
                    AccessTokenKeeper.writeWeiboAccessToken(activity, mAccessToken);
                }
                if (authListener != null) {
                    authListener.onComplete(values);
                }
                Log.e("WeiboAuth", "onComplete: " + values.keySet().toArray());
            }

            @Override
            public void onCancel() {
                if (authListener != null) {
                    authListener.onCancel();
                }
                Log.e("WeiboAuth", "onCancel: ");
            }
        };
        mSsoHandler.authorize(listener);
    }

    /**
     * 获得{@link com.sina.weibo.sdk.auth.WeiboAuth}实例
     * 
     * @param context
     * @return
     */
    public static WeiboAuth getWeiboAuth(Activity context) {
        WeiboAuth authInfo = new WeiboAuth(context, OpenId.WEIBO_APP_ID, OpenId.WEIBO_REDIRECT_URL,
                OpenId.WEIBO_APP_SCOPE);
        return authInfo;
    }

    /**
     * 检查是否已经进行认证
     * 
     * @param context
     * @return
     */
    public static boolean isAuthed(Context context) {
        if (mAccessToken == null) {
            mAccessToken = AccessTokenKeeper.readWeiboAccessToken(context);
        }
        return mAccessToken != null && mAccessToken.isSessionValid();
    }
    public static void clear(Context context) {
        mAccessToken = null;
        AccessTokenKeeper.clear(context);
    }
}
