package com.qihoo.around.sharecore;

import com.qihoo.around.sharecore.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by renjihai on 2015/1/22.
 */
public class QQWebShareActivity extends Activity{
    private WebView mWebView = null;
    private String TAG = "QQWebShareActivity";

    private boolean isNightMode = false;
    private String shareUrl = null;
    private View maskView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq_web_share);
        initData();
        maskView = findViewById(R.id.qq_mask_view);
        if (isNightMode){
            maskView.setVisibility(View.VISIBLE);
        }else{
            maskView.setVisibility(View.GONE);
        }
        mWebView = (WebView) findViewById(R.id.qq_web);
        mWebView.setWebViewClient(new QQWebViewClient());
        mWebView.setWebChromeClient(new QQWebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(shareUrl);
    }

    private void initData(){
    	try {
    		isNightMode = getIntent().getBooleanExtra(ShareConstans.IS_NIGHT_MODE,false);
            shareUrl = getIntent().getStringExtra(ShareConstans.SHARE_URL);
		} catch (Exception e) {
			// TODO: handle exception
		}
    }


    /**
     * 自定义WebViewClient类.
     */
    private class QQWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "Redirect URL: " + url);
            handlerUrl(view,url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "onPageStarted URL: " + url);
            handlerUrl(view,url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "onPageFinished url:" + url);
            handlerUrl(view,url);
        }


        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            Log.d(TAG, "onLoadResource:" + url);
            handlerUrl(view,url);
        }

    }


    /**
     * 处理url。
     *
     * @param webView
     * @param url
     */
    private void handlerUrl(WebView webView,String url) {
        //分享完成之后，点击界面上的"好搜"，将得到该url： tencent101023385://tauth.qq.com/?#action=shareToQzone&result=complete&response={"ret":0}
        Log.d(TAG,"handlerUrl: "+url);
        if (url.startsWith("tencent")) {
            webView.stopLoading();
            //TODO 分享成功
            if (!isFinishing()){
                String[] arr = url.split("[&]");
                if(arr.length >= 2){
                    //String[] temp = arr[1].split("[=]");
                    //if(temp.length >= 2){
                        if(arr[1].contains("complete")){
                            Toast.makeText(this,R.string.share_successed,Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                   // }
                }
                Toast.makeText(this,R.string.share_canceled,Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private class QQWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mWebView.loadUrl(isNightMode ? ShareConstans.JS_NIGHT_MODE : ShareConstans.JS_DAY_MODE);
        }
    }

}
