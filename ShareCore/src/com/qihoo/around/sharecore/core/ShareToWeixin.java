package com.qihoo.around.sharecore.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.qihoo.around.sharecore.openid.OpenId;
import com.tencent.mm.sdk.openapi.*;

import java.io.ByteArrayOutputStream;

public class ShareToWeixin {
    public static final int LIMIT_WIDTH = 300;

    /**
     * 微信分享
     * 
     * @param context
     * @param title
     *            标题
     * @param content
     *            内容
     * @param shareUrl
     *            链接
     * @param thumBitmap
     *            缩略图 宽高不高超过{@link ShareToWeixin#LIMIT_WIDTH} , 超过时,会对该图进行缩放
     * @param scene
     *            分享的位置 微信好友 {@link com.tencent.mm.sdk.openapi.SendMessageToWX.Req#WXSceneSession} 微信朋友圈
     *            {@link com.tencent.mm.sdk.openapi.SendMessageToWX.Req#WXSceneTimeline}
     * @return
     */
    public static boolean share(Context context, String title, String content, String shareUrl,
            Bitmap thumBitmap, int scene) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = content;
        if (thumBitmap != null) {
            msg.setThumbImage(thumBitmap);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.format("appdata %1$s", System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;
        IWXAPI api = WXAPIFactory.createWXAPI(context, OpenId.WEIXIN_APP_ID, true);
        boolean registerApp = api.registerApp(OpenId.WEIXIN_APP_ID);
        if (!registerApp) {
            return false;
        }
        boolean b = api.sendReq(req);
        // Log.e("Bobo_Debug", "sendReq: " + b);
        return b;
    }
    
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }

}
