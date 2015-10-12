package com.qihoo.around.sharecore.core;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.qihoo.around.sharecore.openid.OpenId;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

public class ShareToTencent {

    /**
     * 
     * @param context
     * @param title
     * @param summary
     * @param url
     * @param imgUrls
     * @param listener
     */
    public static void shareToQzone(Tencent mTencent, final Activity context, String title,
            String summary, String url, ArrayList<String> imgUrls, final IUiListener listener) {
        Bundle params = new Bundle();
        params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "身边生活");
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrls);
        mTencent.shareToQzone(context, params, listener);
    }


    /**
     *
     * @param mTencent
     * @param context
     * @param title
     * @param summary
     * @param url
     * @param webImgUrl
     * @param localImgUrl
     * @param listener
     */
    public static void shareToQQ(Tencent mTencent, Activity context, String title, String summary,
            String url,String webImgUrl, String localImgUrl, IUiListener listener) {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, webImgUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, localImgUrl);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        mTencent.shareToQQ(context, params, listener);
    }

    /**
     *
     * @param mTencent
     * @param context
     * @param title
     * @param summary
     * @param url
     * @param localImgUrl
     * @param listener
     */
    public static void shareToQQ(Tencent mTencent, Activity context, String title, String summary,
                                 String url,String localImgUrl, IUiListener listener) {
        shareToQQ(mTencent,context,title,summary,url,null,localImgUrl,listener);
    }


    /**
     * 获取拼接的URL.
     *
     * @return
     */
    public static String getQQWebShareURL(String title, String content, String url, String imgPath) {
        String BASE_URL = "http://openmobile.qq.com/api/check?";
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL).append("page=shareindex.html&style=9&sdkv=2.6&sdkp=a&action=shareToQQ&site=身边生活&appName=身边生活")
                .append("&title=").append(title)
                .append("&appId=").append(OpenId.TENCENT_APP_ID)
                .append("&status_machine=").append(Build.MODEL)
                .append("&status_os=").append(Build.VERSION.RELEASE)
                .append("&summary=").append(content)
                .append("&targetUrl=").append(url);
        if (!TextUtils.isEmpty(imgPath)) {
            if (imgPath.contains("around/") || imgPath.contains("/data/data/com.qihoo.around/")) {
                //本地图片
                sb.append("&imageLocalUrl=").append("file://" + imgPath);
            } else {
                sb.append("&imageUrl=").append(imgPath);
            }
        }
        return sb.toString();
    }

    /**
     * 获取拼接的URL.
     *
     * @return
     */
    public static String getQZoneWebShareURL(String title,String content ,String url,String imgPath) {
        String BASE_URL = "http://openmobile.qq.com/api/check2?";
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL).append("page=qzshare.html&loginpage=loginindex.html&logintype=qzone&sdkv=2.0&sdkp=a&action=shareToQQ&site=身边生活&appName=身边生活")
                .append("&title=").append(title)
                .append("&appId=").append(OpenId.TENCENT_APP_ID)
                .append("&status_machine=").append(Build.MODEL)
                .append("&status_os=").append(Build.VERSION.RELEASE)
                .append("&summary=").append(content)
                .append("&targetUrl=").append(url);
        if (!TextUtils.isEmpty(imgPath)) {
            if (imgPath.contains("around/")||imgPath.contains("/data/data/com.qihoo.around/")) {
                //本地图片
                sb.append("&imageLocalUrl=").append("file://"+imgPath);
            } else {
                sb.append("&imageUrl=").append(imgPath);
            }
        }
        return sb.toString();
    }
}
