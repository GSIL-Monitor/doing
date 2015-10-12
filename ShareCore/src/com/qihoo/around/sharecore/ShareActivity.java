package com.qihoo.around.sharecore;
import com.qihoo.around.sharecore.aidl.IOnShareCallback;
import com.qihoo.around.sharecore.aidl.IShareResourceFetcher;
import com.qihoo.around.sharecore.bean.AsyncTaskStart;
import com.qihoo.around.sharecore.core.AccessTokenKeeper;
import com.qihoo.around.sharecore.core.ShareToDouban;
import com.qihoo.around.sharecore.core.ShareToTencent;
import com.qihoo.around.sharecore.core.ShareToWeibo;
import com.qihoo.around.sharecore.core.ShareToWeixin;
import com.qihoo.around.sharecore.core.douban.IDoubanListener;
import com.qihoo.around.sharecore.openid.OpenId;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.LogUtil;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * renjh1 2014年12月26日
 */

public class ShareActivity extends Activity implements IShareEvent, IWeiboHandler.Response,
        IDoubanListener {

    private static final String TAG = "ShareActivity";
    private static int MAX_MESSAGE_LENGTH = 60;
    private SsoHandler mSsoHandler = null;
    private IWeiboShareAPI mWeiboShareAPI = null;
    private IShareResourceFetcher shareCallback;
    private Tencent mTencent = null;
    ShareDialog shareDialog = null;

    private boolean isAsync = false;
    private boolean isNightMode = false;

    private boolean mCancelable = true;

    OnShareCallback onShareCallback = new OnShareCallback();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	isAsync = getIntent().getBooleanExtra(ShareConstans.IS_ASYNC,false);
            isNightMode = getIntent().getBooleanExtra(ShareConstans.IS_NIGHT_MODE,false);	
		} catch (Exception e) {
			// TODO: handle exception
		}
        initUI();
        shareCallback = ShareManager.getInstance().getShareCallback();
        mWeiboShareAPI = ShareToWeibo.get(this);
        mWeiboShareAPI.handleWeiboResponse(getIntent(),this);
        if (mWeiboShareAPI.isWeiboAppInstalled()) {
            mWeiboShareAPI.registerApp();
        }
    }

    /**
     * 弹出分享框
     */
    private void initUI() {
        DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                onEventCancel();
            }
        };
        shareDialog = new ShareDialog(this, this, onCancelListener,isNightMode);
        shareDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理微博Sso分享之后的回调
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        /**
         * 处理腾讯分享之后的回调
         */
        if (requestCode == Constants.REQUEST_QQ_SHARE && resultCode == Constants.ACTIVITY_OK) {
            Tencent.handleResultData(data, iUiListener);
        }
    }

    /**
     * 获取微博Sso分享实例
     * @return
     */
    public synchronized SsoHandler getSsoHandler() {
        if (mSsoHandler == null) {
            mSsoHandler = new SsoHandler(ShareActivity.this,
                    ShareToWeibo.getWeiboAuth(ShareActivity.this));
        }
        return mSsoHandler;
    }

    /**
     * 获取QQ/QQ空间分享实例
     * @return
     */
    public synchronized Tencent getTencent() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(OpenId.TENCENT_APP_ID, ShareActivity.this);
        }
        return mTencent;
    }

    @Override
    public void finish() {
        super.finish();
        //ShareManager.getInstance().setShareCallback(null);
        Log.e("share","finish!!!!!!!!!!!!!!!!");

    }

    /**
     * @see {@link Activity#onNewIntent}
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onBackPressed() {
        if (!mCancelable){
            return ;
        }
    }

    /**
     * 静默分享到微博
     *
     * @throws android.os.RemoteException
     */
    private void shareToWeiboLocal() throws RemoteException {
        /*String msg = shareCallback.getShareSummary(ShareConstans.TYPE_WEIBO);
        Log.e("share", "content: " + msg);
        Bitmap bitmap = shareCallback.getShareImg(ShareConstans.TYPE_WEIBO);
        Bitmap thumbBitmap = shareCallback.getShareThumbImg(ShareConstans.TYPE_WEIBO);
        if (bitmap != null) {
            ShareToWeibo.share(getApplicationContext(), msg, bitmap,
                    WeiboRequestLsn.getInstance(getApplicationContext()));
        } else if (thumbBitmap != null) {
            ShareToWeibo.share(getApplicationContext(), msg, thumbBitmap,
                    WeiboRequestLsn.getInstance(getApplicationContext()));
        } else {
            ShareToWeibo.share(getApplicationContext(), msg,
                    WeiboRequestLsn.getInstance(getApplicationContext()));
        }
        finish();*/
        LogUtil.d("share", "Do share to Weibo Local!");
        try {
            Intent intent = new Intent(ShareActivity.this,WeiboShareActivity.class);
            intent.putExtra(ShareConstans.IS_NIGHT_MODE, isNightMode);
            startActivity(intent);
        } catch (Exception ex) {
            LogUtil.e("share", ex.toString());
            Toast.makeText(this, R.string.share_open_share_activity_failed, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。 注意：当
     * {@link com.sina.weibo.sdk.api.share.IWeiboShareAPI#getWeiboAppSupportAPI()}
     * >= 10351 时，支持同时分享多条消息， 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     *
     * @throws android.os.RemoteException
     */
    private void shareToWeiboSso() throws RemoteException {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        String shareDetail = shareCallback.getShareSummary(ShareConstans.TYPE_WEIBO);
        Log.e("share","content: "+shareDetail);
        if (shareDetail != null) {
            weiboMessage.textObject = getTextObj(shareDetail);
        }
        Bitmap shareBitmap = shareCallback.getShareImg(ShareConstans.TYPE_WEIBO);
        if (shareBitmap != null) {
            weiboMessage.imageObject = getImageObject(shareBitmap);
        }
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(request);
        /*
        
        
        AuthInfo authInfo = ShareToWeibo.getWeiboAuth(this).getAuthInfo();
        Oauth2AccessToken accessToken = AccessTokenKeeper.readWeiboAccessToken(getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException arg0) {
            }

            @Override
            public void onComplete(Bundle bundle) {
                // TODO Auto-generated method stub
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeWeiboAccessToken(getApplicationContext(), newToken);
            }

            @Override
            public void onCancel() {
            }
        });*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /**
         * 处理QQ/QZONE等页面分享后无回调的问题,主动将该页面关闭
         */
        if (shareDialog != null && !shareDialog.isShowing()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 500);
        }
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(String content) {
        TextObject textObject = new TextObject();
        textObject.text = content;
        return textObject;
    }

    /**
     * 获取图片信息
     *
     * @param bitmap
     * @return
     */
    private ImageObject getImageObject(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * 微信分享
     *
     * @param scene
     *            分享途径
     * @throws android.os.RemoteException
     */
    private void shareToWeixin(String src, int scene) {
        try {
            String title = shareCallback.getShareTitle(src);
            String summary = shareCallback.getShareSummary(src);
            String url = shareCallback.getShareUrl(src);
            Bitmap thumbBitmap = shareCallback.getShareThumbImg(src);
            boolean share1 = ShareToWeixin.share(getApplicationContext(), title, summary, url,
                    thumbBitmap, scene);
            if (!share1) {
                Toast.makeText(ShareActivity.this, R.string.share_weixin_is_not_install, Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            showToast(R.string.share_failed);
        }
        finish();
    }

    /**
     * 微博分享后的回调
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            showToast(R.string.share_successed);
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            showToast(R.string.share_canceled);
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            showToast(R.string.share_failed);
            break;
        }
        finish();
    }

    protected void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int strId) {
        Toast.makeText(this, strId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 微博分享
     */
    @Override
    public void onEventWeibo() {
        // 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
        try {
            if (isAsync){
                shareCallback.onAsyncShare(ShareConstans.TYPE_WEIBO,onShareCallback.configShareType(ShareConstans.TYPE_WEIBO));
            }else{
                shareToWeibo();
            }
        }catch (Exception e) {
            Log.d(TAG, "", e);
            onError(null);
            return;
        }
    }

    private void shareToWeibo() throws RemoteException {
        String from = AccessTokenKeeper.readWeiboAccessTokenFrom(getApplicationContext());
        
        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
        	LogUtil.d("share", "is Weibo APP Support API!");
            //shareToWeiboSso();
            if (ShareToWeibo.isAuthed(getApplicationContext()) && !TextUtils.isEmpty(from)) {
                LogUtil.e("share", "is APP Weibo Authed!");
                //shareToWeiboSso();
                if(from.equals("APP")){
                    shareToWeiboLocal();
                }else{
                    ShareToWeibo.clear(getApplicationContext());
                    ShareToWeibo.weiboAuth(ShareActivity.this, getSsoHandler(), weiboAuthListener);
                }
            } else {
                LogUtil.e("share", "APP weibo Auth!");
                ShareToWeibo.weiboAuth(ShareActivity.this, getSsoHandler(), weiboAuthListener);
            }
            AccessTokenKeeper.writeWeiboAccessTokenFrom(getApplicationContext(), "APP");
        } else {
            if (ShareToWeibo.isAuthed(getApplicationContext()) && !TextUtils.isEmpty(from)) {
            	LogUtil.e("share", "is Weibo Authed!");
            	if(from.equals("WEB")){
            	    shareToWeiboLocal();
                }else{
                    ShareToWeibo.clear(getApplicationContext());
                    ShareToWeibo.weiboAuth(ShareActivity.this, getSsoHandler(), weiboAuthListener);
                }
            } else {
            	LogUtil.e("share", "weibo Auth!");
                ShareToWeibo.weiboAuth(ShareActivity.this, getSsoHandler(), weiboAuthListener);
            }
            AccessTokenKeeper.writeWeiboAccessTokenFrom(getApplicationContext(), "WEB");
        }
    }

    @Override
    public void onEventQQ() {
        try {
            if (isAsync){
                shareCallback.onAsyncShare(ShareConstans.TYPE_QQ,onShareCallback.configShareType(ShareConstans.TYPE_QQ));
            }else{
                shareToQQ();
            }
        } catch (Exception e) {
            Log.d(TAG, "", e);
            onError(null);
            return;
        }
    }

    private void shareToQQ() throws RemoteException {
        String title = shareCallback.getShareTitle(ShareConstans.TYPE_QQ);
        String summary = shareCallback.getShareSummary(ShareConstans.TYPE_QQ);
        String url = shareCallback.getShareUrl(ShareConstans.TYPE_QQ);
        String localImgPath = shareCallback.getShareLocalImg(ShareConstans.TYPE_QQ);
        String webImgUrl = shareCallback.getShareImgUrl(ShareConstans.TYPE_QQ);

        if (getTencent().isSupportSSOLogin(this)) {
            if (TextUtils.isEmpty(webImgUrl)) {
                ShareToTencent.shareToQQ(getTencent(), ShareActivity.this, title, summary, url,
                        localImgPath, iUiListener);
            } else {
                ShareToTencent.shareToQQ(getTencent(), ShareActivity.this, title, summary, url,
                        webImgUrl, localImgPath, iUiListener);
            }
        } else {
            String imgPath = null;
            if (!TextUtils.isEmpty(localImgPath)) {
                imgPath = localImgPath;
            } else {
                imgPath = webImgUrl;
            }
            String shareUrl = ShareToTencent.getQQWebShareURL(title, summary, url, imgPath);
            if (TextUtils.isEmpty(shareUrl)) {
                Toast.makeText(this, R.string.share_link_is_error, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Intent intent = new Intent(ShareActivity.this, QQWebShareActivity.class);
                    intent.putExtra(ShareConstans.IS_NIGHT_MODE, isNightMode);
                    intent.putExtra(ShareConstans.SHARE_URL, shareUrl);
                    startActivity(intent);
                } catch (Exception ex) {
                    Toast.makeText(this, R.string.share_open_share_activity_failed, Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }


    @Override
    public void onEventQZone() {
        try {
            if (isAsync){
                shareCallback.onAsyncShare(ShareConstans.TYPE_QZONE,onShareCallback.configShareType(ShareConstans.TYPE_QZONE));
            }else{
                shareToQZone();
            }
        } catch (Exception e) {
            Log.d(TAG, "", e);
            onError(null);
            return;
        }
    }

    private void shareToQZone() throws RemoteException {
        String title = shareCallback.getShareTitle(ShareConstans.TYPE_QZONE);
        String summary = shareCallback.getShareSummary(ShareConstans.TYPE_QZONE);
        String url = shareCallback.getShareUrl(ShareConstans.TYPE_QZONE);
        String[] imgUrls = shareCallback.getShareWebImgs(ShareConstans.TYPE_QZONE);
        ArrayList<String> imgs = null;
        if (imgUrls != null) {
            imgs = new ArrayList<String>();
            for (String string : imgUrls) {
                imgs.add(string);
            }
        } else {
            imgs = new ArrayList<String>();
            imgs.add("http://p9.qhimg.com/t016bc0ed7c629a36d1.png");
        }

        if (getTencent().isSupportSSOLogin(this)) {
            ShareToTencent.shareToQzone(getTencent(), ShareActivity.this, title, summary, url,
                    imgs, iUiListener);
        } else {
            String imgPath = null;
            if (imgUrls != null && imgUrls.length > 0) {
                imgPath = imgUrls[0];
            } else {
                imgPath = "http://p9.qhimg.com/t016bc0ed7c629a36d1.png";
            }
            String shareUrl = ShareToTencent.getQZoneWebShareURL(title, summary, url, imgPath);
            if (TextUtils.isEmpty(shareUrl)) {
                Toast.makeText(this, R.string.share_link_is_error, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Intent intent = new Intent(ShareActivity.this, QQWebShareActivity.class);
                    intent.putExtra(ShareConstans.IS_NIGHT_MODE, isNightMode);
                    intent.putExtra(ShareConstans.SHARE_URL, shareUrl);
                    startActivity(intent);
                } catch (Exception ex) {
                    Toast.makeText(this, R.string.share_open_share_activity_failed, Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    /**
     * 微信朋友圈分享
     */
    @Override
    public void onEventWeixinTimeline() {
        try {
            if (isAsync){
                shareCallback.onAsyncShare(ShareConstans.TYPE_WEIXIN_TIMELINE,onShareCallback.configShareType(ShareConstans.TYPE_WEIXIN_TIMELINE));
            }else{
                shareToWeixin(ShareConstans.TYPE_WEIXIN_TIMELINE, SendMessageToWX.Req.WXSceneTimeline);
            }
        }catch (Exception e) {
            Log.d(TAG, "", e);
            onError(null);
            return;
        }
    }

    /**
     * 微信朋友分享
     */
    @Override
    public void onEventWeixinFriends() {
        try {
            if (isAsync){
                shareCallback.onAsyncShare(ShareConstans.TYPE_WEIXIN_FRTENDS,onShareCallback.configShareType(ShareConstans.TYPE_WEIXIN_FRTENDS));
            }else{
                shareToWeixin(ShareConstans.TYPE_WEIXIN_FRTENDS, SendMessageToWX.Req.WXSceneSession);
            }
        }catch (Exception e) {
            Log.d(TAG, "", e);
            onError(null);
            return;
        }

    }

    /**
     * 豆瓣分享
     */
    @Override
    public void onEventDouban() {
        try {
            if (isAsync){
                shareCallback.onAsyncShare(ShareConstans.TYPE_DOUBAN,onShareCallback.configShareType(ShareConstans.TYPE_DOUBAN));
            }else{
                shareToDouban();
            }
        } catch (Exception e) {
            Log.d(TAG, "", e);
            onError(null);
            return;
        }
    }
	
    private void shareToDouban() throws RemoteException {
        String title = shareCallback.getShareTitle(ShareConstans.TYPE_DOUBAN);
        String summary = shareCallback.getShareSummary(ShareConstans.TYPE_DOUBAN);
        String url = shareCallback.getShareUrl(ShareConstans.TYPE_DOUBAN);
        String imageUrl = shareCallback.getShareImgUrl(ShareConstans.TYPE_DOUBAN);
        ShareToDouban.share(ShareActivity.this, summary, title, url, url, imageUrl, this);
    }

    @Override
	public void onEventMessage() {
		// TODO Auto-generated method stub
    	try {
    		if (isAsync){
                shareCallback.onAsyncShare(ShareConstans.TYPE_MESSAGE,onShareCallback.configShareType(ShareConstans.TYPE_MESSAGE));
            }else{
                shareToMessage();
            }
        }catch (Exception e) {
            Log.d(TAG, "", e);
            onError(null);
            return;
        }
	}

    @SuppressLint("InlinedApi")
	private void shareToMessage() throws RemoteException {
    	boolean found = false;
    	String title = shareCallback.getShareTitle(ShareConstans.TYPE_MESSAGE);
        String summary = shareCallback.getShareSummary(ShareConstans.TYPE_MESSAGE);
        String url = shareCallback.getShareUrl(ShareConstans.TYPE_MESSAGE);
        String from = shareCallback.getShareFrom(ShareConstans.TYPE_MESSAGE);
        String finalContent = "";
        if(summary.contains("告诉大家一个精彩发现")){
    		summary = summary.replace("大家", "你");
    	}
        int mMessageLength = MAX_MESSAGE_LENGTH - url.length() - from.length() - 3;
        LogUtil.d("share", "Total len:" + mMessageLength + ";url:" + url);
		if(mMessageLength <= 0){
			finalContent = url;
		}else{
			int len = title.length() + summary.length();
			String content = summary;
    		if(len > mMessageLength){
    			int pos = summary.indexOf("\"");
    			int leave = summary.length() - len + mMessageLength;
    			content = summary.substring(0,pos);
    			if(leave > 0)
    				content += summary.substring(pos,leave-1) + "\"";
    			int lenTitle = mMessageLength - content.length();
    			if(title.length() > lenTitle)
    				title = title.substring(0,lenTitle);
    		}
        	finalContent = title + "," + content + "," + url + " " + from;
		}
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
            	Log.e("share", "Get Share Name:" + info.activityInfo.packageName);
                if (info.activityInfo.packageName.toLowerCase().contains(ShareConstans.TYPE_MESSAGE) ||
                        info.activityInfo.name.toLowerCase().contains(ShareConstans.TYPE_MESSAGE) ) {
                    share.putExtra(Intent.EXTRA_TITLE,  title);
                    share.putExtra(Intent.EXTRA_TEXT, finalContent);
                    share.putExtra(Intent.EXTRA_ORIGINATING_URI,url);
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return;
            Intent chooseIntent = Intent.createChooser(share, "分享");
            chooseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(chooseIntent);
            finish();
        }
    }
    
	@Override
	public void onEventShareMore() {
		// TODO Auto-generated method stub
		try {
			if (isAsync){
                shareCallback.onAsyncShare(ShareConstans.TYPE_MORE,onShareCallback.configShareType(ShareConstans.TYPE_MORE));
            }else{
                shareMore();
            }
        }catch (Exception e) {
            Log.d(TAG, "", e);
            onError(null);
            return;
        }
	}
	
	@SuppressLint("InlinedApi")
	private void shareMore() throws RemoteException {
		Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String title = shareCallback.getShareTitle(ShareConstans.TYPE_MORE);
        String summary = shareCallback.getShareSummary(ShareConstans.TYPE_MORE);
        String url = shareCallback.getShareUrl(ShareConstans.TYPE_MORE);
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()){
        	List<Intent> targetIntents = new ArrayList<Intent>();
        	List<String> removeShareList = getRemoveShareList();
    		for (ResolveInfo info : resInfo) {
    			Log.e("share", "Info name:" + info.activityInfo.name + ";Package:" + info.activityInfo.packageName);
                if(!isInRemoveList(removeShareList, info.activityInfo.name,info.activityInfo.packageName)){
            	    Intent targetedIntent = new Intent(android.content.Intent.ACTION_SEND);
                   	targetedIntent.setType("text/plain");
               		targetedIntent.putExtra(Intent.EXTRA_TITLE, title);
               		targetedIntent.putExtra(Intent.EXTRA_SUBJECT, title);
               		targetedIntent.putExtra(Intent.EXTRA_TEXT, title + "," + summary + "," + url);
               		targetedIntent.setPackage(info.activityInfo.packageName);
               		targetIntents.add(targetedIntent);
               }
            }
            Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), "分享"); 
            if (chooserIntent == null) {
                return;
            }
            chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
            		targetIntents.toArray(new Parcelable[] {}));
            try {
                startActivity(chooserIntent);
                finish();
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "找不到分享组件！",Toast.LENGTH_SHORT).show();
            }
        }
    }
	
	public boolean isInRemoveList(List<String>list, String name,String pkg){
		for (String item : list) {
			if(name.toLowerCase().contains(item) || pkg.toLowerCase().contains(item))
				return true;
		}
		return false;
	}
	
	public List<String> getRemoveShareList(){
    	List<String> removeList = new ArrayList<String>();
    	removeList.add("com.qihoo.appstore");
    	removeList.add("com.android.mms");
    	removeList.add("com.sina");
    	removeList.add("com.tencent");
    	removeList.add("jackpal.androidterm");
    	removeList.add("com.qihoo.yunpan");
    	return removeList;
    }

    /**
     * 复制链接
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void onEventClipBoard() {
        String content = null;
        try {
            content = shareCallback.getShareUrl(ShareConstans.TYPE_COPY_LINK);
        } catch (Exception e) {
            Log.d(TAG,"",e);
            onError(null);
            return ;
        }
        if (content == null) {
            Toast.makeText(this, R.string.share_copy_link_is_null, Toast.LENGTH_SHORT).show();
        } else {
            try {
                if (Build.VERSION.SDK_INT >= 11) {
                    android.content.ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // cmb.setText(content);
                    cmb.setPrimaryClip(ClipData.newPlainText("share content", content));
                } else {
                    android.text.ClipboardManager cmb = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(content);
                }
            } catch(Exception ignored) {
            }
            Toast.makeText(this, R.string.share_copy_link_successed, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    /**
     * 取消分享
     */
    @Override
    public void onEventCancel() {
        showToast(R.string.share_canceled);
        finish();
    }

    @Override
    public void onComplete(Object object) {
        showToast(R.string.share_successed);
        finish();
    }

    @Override
    public void onCancel() {
        Log.e("aaaaa","onEventCancel???????????????????");
        showToast(R.string.share_canceled);
        finish();
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	LogUtil.e("share", "on Stop in ShareActivity!");
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtil.e("share", "on Destory in ShareActivity!");
	}
    

	@Override
    public void onError(Exception ex) {
        String msg = null;
        if (ex != null && ex.getMessage() != null) {
            msg = ex.getMessage();
        }
        String shareFailed=getString(R.string.share_failed);
        showToast(TextUtils.isEmpty(msg)?shareFailed:(shareFailed+": "+msg));
        finish();
    }

    IUiListener iUiListener = new IUiListener() {

        @Override
        public void onError(UiError ex) {
            if (ex != null && ex.errorDetail != null) {
                // LogUtils.e("Error", ex.getMessage());
            }
            ShareActivity.this.onError(ex==null?null:new Exception(ex.errorMessage));
        }

        @Override
        public void onComplete(Object arg0) {
            ShareActivity.this.onComplete(arg0);
        }

        @Override
        public void onCancel() {
            ShareActivity.this.onCancel();
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what){
                case  ShareConstans.MSG_WHAT_START:
                    AsyncTaskStart asyncStart = (AsyncTaskStart) msg.obj;
                    mCancelable = asyncStart.isCancelable();
                    if (asyncStart.isShowProgress()){
                        //TODO
                    }
                    break;
                case  ShareConstans.MSG_WHAT_PROGRESS:
                    int total = msg.arg1;
                    int cur = msg.arg2;
//                    Toast.makeText(ShareActivity.this,String.format("一共: %1$s,已完成: %2$s",total,cur),Toast.LENGTH_SHORT).show();
                    break;
                case  ShareConstans.MSG_WHAT_SUCCESS:
                    try {
                        String mShareType = (String) msg.obj;
                        if (ShareConstans.TYPE_QQ.equals(mShareType)){
                            shareToQQ();
                        }else if (ShareConstans.TYPE_QZONE.equals(mShareType)){
                            shareToQZone();
                        }else if (ShareConstans.TYPE_WEIXIN_TIMELINE.equals(mShareType)){
                            shareToWeixin(ShareConstans.TYPE_WEIXIN_TIMELINE,SendMessageToWX.Req.WXSceneTimeline);
                        }else if (ShareConstans.TYPE_WEIXIN_FRTENDS.equals(mShareType)){
                            shareToWeixin(ShareConstans.TYPE_WEIXIN_FRTENDS,SendMessageToWX.Req.WXSceneSession);
                        }else if (ShareConstans.TYPE_WEIBO.equals(mShareType)){
                            shareToWeibo();
                        }else if (ShareConstans.TYPE_DOUBAN.equals(mShareType)){
                            shareToDouban();
                        }else if (ShareConstans.TYPE_MESSAGE.equals(mShareType)){
                        	shareToMessage();
                        }else if(ShareConstans.TYPE_MORE.equals(mShareType)){
                        	shareMore();
                        }else{
                            ShareActivity.this.onError(new Exception(getString(R.string.share_channel_is_error)));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        onError(e);
                    }
                    break;
                case  ShareConstans.MSG_WHAT_ERROR:
                    String errMsg = msg.obj==null?null: (String) msg.obj;
                    Exception ex = null;
                    if (!TextUtils.isEmpty(errMsg)){
                        ex=new Exception(errMsg);
                    }
                    onError(ex);
                    break;
                default:
                    break;
            }
        }
    };

    class OnShareCallback extends IOnShareCallback.Stub{
        private String mShareType = null;
        @Override
        public void onStart(boolean cancelable) throws RemoteException {
            Message message = Message.obtain();
            message.what = ShareConstans.MSG_WHAT_START;
            message.obj = new AsyncTaskStart(cancelable,false,null,null);
            handler.sendMessage(message);
        }

        @Override
        public void onStartProgressDialog(boolean cancelable, String title, String msg) throws RemoteException {
            Message message = Message.obtain();
            message.what = ShareConstans.MSG_WHAT_START;
            message.obj = new AsyncTaskStart(cancelable,true,title,msg);
            handler.sendMessage(message);
        }

        @Override
        public void onProgress(final int total,final int cur) throws RemoteException {

            Message message = Message.obtain();
            message.what = ShareConstans.MSG_WHAT_PROGRESS;
            message.arg1= total;
            message.arg2 = cur;
            handler.sendMessage(message);
        }

        @Override
        public void onSuccess() throws RemoteException {
            Message message = Message.obtain();
            message.what = ShareConstans.MSG_WHAT_SUCCESS;
            message.obj = mShareType;
            handler.sendMessage(message);
        }

        @Override
        public void onError() throws RemoteException {
            Message message = Message.obtain();
            message.what = ShareConstans.MSG_WHAT_ERROR;
            handler.sendMessage(message);
        }

        @Override
        public void onErrorWithMsg(String errMsg) throws RemoteException {
            Message message = Message.obtain();
            message.what = ShareConstans.MSG_WHAT_ERROR;
            message.obj = errMsg;
            handler.sendMessage(message);
        }

        public OnShareCallback configShareType(String shareType){
            this.mShareType= shareType;
            return this;
        }
    }

    WeiboAuthListener weiboAuthListener = new WeiboAuthListener() {
        @Override
        public void onWeiboException(WeiboException arg0) {
            String authFailed = getString(R.string.share_auth_failed);
            String msg = (arg0 == null || arg0.getMessage() == null) ? authFailed : (authFailed+": " + arg0
                    .getMessage());
            Toast.makeText(ShareActivity.this, msg, Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onComplete(Bundle arg0) {
            Log.e("WeiboAuth", "WeiboAuthListener: !!!!!!!!!!!!!");
            try {
                shareToWeibo();
            } catch (RemoteException e) {
                e.printStackTrace();
                onError(e);
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(ShareActivity.this, R.string.share_auth_canceled, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

}