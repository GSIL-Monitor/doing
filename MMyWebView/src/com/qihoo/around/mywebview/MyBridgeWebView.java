/**
 * @author zhaozuotong
 * @since 2015-5-8 下午3:22:34
 */

package com.qihoo.around.mywebview;

import com.qihoo.around._public.eventbus.QEventBus;
import com.qihoo.around._public.eventdefs.BrowserEvents;
import com.qihoo.around._public.util.DeviceUtils;
import com.qihoo.around._public.util.NetworkUtils;
import com.qihoo.around.mywebview.jsBridge.CallBackFunction;
import com.qihoo.around.mywebview.manager.NetworkManager;
import com.qihoo.around.mywebview.manager.WebViewManager;
import com.qihoo.around.mywebview.utils.LogUtils;
import com.qihoo.around.webviewdemo.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

public class MyBridgeWebView extends FrameLayout{

    /*private static final String JS_SOHISTORY_BACK = "javascript:window.history.back=function so_back(){msohistory.back()}";
    private static final String JS_SOHISTORY_GO = "javascript:window.history.go=function so_go(pos){msohistory.go(pos)}";
    private static final String UPDATE_APP_STATUS = "javascript:window.updateAppStatus()";*/
    private BridgeWebView mWebview;
    private ViewStub mStubErrorPageView;
    private View mErrorPageView;
    private ImageView mCacheImageView;
    private View mLoadingView;
    private Context mContext;
    private final String BLANK_URL = "about:blank";
    public static final String SEARCH_URL_J_PREFIX = "http://j.www.so.com/";
    private int goBackCount = -1;

    /**
     * @param context
     */
    public MyBridgeWebView(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public MyBridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public MyBridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        QEventBus.getEventBus().registerSticky(this);
    }

    @Override
    protected void onDetachedFromWindow() {        
        QEventBus.getEventBus().unregister(this);
        super.onDetachedFromWindow();
    }

    @SuppressLint("NewApi")
    private void initView(final Context context) {
        mContext = context;
        View view = inflate(context, R.layout.progress_webview, this);
        if (isInEditMode())
            return;

        // mWebview = (QihooWebview) view.findViewById(R.id.webView);
        mWebview = (BridgeWebView) view.findViewById(R.id.mywebView);

        mStubErrorPageView = (ViewStub) findViewById(R.id.stub_error_page);
        mCacheImageView = (ImageView) findViewById(R.id.stub_cache);

        mWebview.setBackgroundColor(Color.parseColor("#EEEEEE"));
        // slidinglayer 的一个bug，部分机型 webview不显示，后期需要改
        if (Build.VERSION.SDK_INT >= 11) {
            mWebview.getSettings().setDisplayZoomControls(false);
        }
        String value=DeviceUtils.getSystemProperty(DeviceUtils.MIUI_PROP);
        Boolean isXiaomi = "Xiaomi".equalsIgnoreCase(Build.MANUFACTURER) || "Xiaomi".equals(Build.BRAND);
        if(!isXiaomi){
            isXiaomi = !TextUtils.isEmpty(DeviceUtils.getSystemProperty(DeviceUtils.MIUI_PROP));
        }
        if (Build.VERSION.SDK_INT == 15 || Build.VERSION.SDK_INT == 14 || isXiaomi || "Coolpad".equals(Build.BRAND) || "Huawei".equals(Build.BRAND) || "ZTE".equals(Build.BRAND)) {
            mWebview.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    /*public void InjectAllWebSite() {
        if (mWebview != null) {

            mWebview.loadUrl(JS_SOHISTORY_BACK);
            mWebview.loadUrl(JS_SOHISTORY_GO);
        }
    }*/

    public void showLoadingView(boolean isshow) {
        if (mLoadingView == null) {
            mLoadingView = findViewById(R.id.loading_view);
        }
        int show = isshow ? View.VISIBLE : View.GONE;
        mLoadingView.setVisibility(show);
    }

    /**
     * 显示错误页面
     *
     * @param isShow
     */
    public void showErrorPage(boolean isShow) {
        if (mErrorPageView == null) {
            if (!isShow) {
                return;
            }
            if (mStubErrorPageView == null) {
                return;
            }
            ViewParent parent = mStubErrorPageView.getParent();
            if (parent == null) {
                return;
            }
            if (!(parent instanceof ViewGroup)) {
                return;
            }
            View view = mStubErrorPageView.inflate();
            mErrorPageView = view.findViewById(R.id.result_error_layout);
            mErrorPageView.findViewById(R.id.btn_refresh).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    reload();
                }
            });
            TextView setNet = (TextView) mErrorPageView.findViewById(R.id.txt_setting_net);
            setNet.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            setNet.setText(R.string.setting_net);
            setNet.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    startSystemSetting();
                    try {
                        getContext().startActivity(new Intent(Settings.ACTION_SETTINGS));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        if (isShow) {
            mErrorPageView.setVisibility(View.VISIBLE);
        } else {
            mErrorPageView.setVisibility(View.INVISIBLE);
        }
    }
    public void setOnScrollChangedListener(BridgeWebView.OnScrollChangedListenser l) {
        if (l != null && mWebview != null) {
            mWebview.setOnScrollChangedListener(l);
        }
    }

    public boolean IsErrorShow() {
        if (mErrorPageView == null) {
            return false;
        }
        return mErrorPageView.getVisibility() == View.VISIBLE;
    }

    public void drawContent(Canvas c) {
        draw(c);
    }

    public void loadUrl(String url) {
        loadUrl(url, null);

    }

    // use others methods
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        if (mWebview == null) {
            initView(getContext());
        }
        if (mWebview != null) {
            boolean isSafe = URLUtil.isFileUrl(url);
            if (isSafe) {
                mWebview.getSettings().setJavaScriptEnabled(false);
            }
            if (additionalHttpHeaders == null || additionalHttpHeaders.size() == 0) {
                LogUtils.e("aaaaa",url);
                mWebview.loadUrl(url);
            } else {
                mWebview.loadUrl(url, additionalHttpHeaders);
            }
            if (url.startsWith("http")) {
                 QEventBus.getEventBus().postSticky(new BrowserEvents.onProgressChanged(mWebview, 1));
            }
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setScrollBarShow(boolean status) {
        if (mWebview != null)
            mWebview.setVerticalScrollBarEnabled(status);
        if (mWebview != null)
            mWebview.setHorizontalScrollBarEnabled(status);
    }

    public void setWebChromeClient(WebChromeClient client) {
        if (mWebview != null)
            mWebview.setWebChromeClient(client);
    }

    public void setWebViewClient(WebViewClient client) {
        if (mWebview != null)
            mWebview.setWebViewClient(client);
    }

    public void clearAllLayer() {
        if (mWebview != null)
            mWebview.LoadBankUrl();
        if (mWebview != null)
            mWebview.clearHistory();
        if (mWebview != null)
            mWebview.removeAllViews();
        if (mWebview != null)
            mWebview.clearDisappearingChildren();
        /*if (mWebview != null)
            mWebview.clearCache(true);
        if (mWebview != null)
            mWebview.clearFormData();*/

    }

    public void clearCache() {
        if (mWebview != null)
            mWebview.clearCache(false);
        if (mWebview != null)
            mWebview.clearFormData();
    }

    public void clearHistory() {
        if (mWebview != null) {
            mWebview.clearHistory();
        }
    }

    public void setDownloadListener(DownloadListener l) {
        if (mWebview != null)
            mWebview.setDownloadListener(l);
    }

    public void stopLoading() {
        if (mWebview != null)
            mWebview.stopLoading();
    }

    // reload maybe lost the user agent information
    public void reload() {
        if (mWebview != null) {
            mWebview.loadUrl(mWebview.getUrl());
        }
        /*if (!NetworkManager.getInstance(mContext).isNoNetwork()) {
            showErrorPage(false);
        }*/
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            showErrorPage(false);
        }
    }

    public void Refresh() {
        if (mWebview != null) {
            mWebview.reload();
        }
        /*if (!NetworkManager.getInstance(mContext).isNoNetwork()) {
            showErrorPage(false);
        }*/
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            showErrorPage(false);
        }
    }

    public Bitmap getFavicon() {
        if (mWebview != null)
            return mWebview.getFavicon();
        else
            return null;
    }

    public String getTitle() {
        if (mWebview != null)
            return mWebview.getTitle();
        else
            return "";
    }

    public String getOriginalUrl() {
        if (mWebview != null)
            return mWebview.getOriginalUrl();
        else
            return "";
    }

    public String getUrl() {
        if (mWebview != null)
            return mWebview.getUrl();
        else
            return "";
    }

    public void clearView() {
        if (mWebview != null) {
            // mWebview.clearView();
            mWebview.loadUrl(BLANK_URL);
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
    }

    public void addJavascriptInterface(Object object,String value){
        if (mWebview != null)
            mWebview.addJavascriptInterface(object, value);
    }
    public void goForward() {
        if (mWebview != null)
            mWebview.goForward();
    }

    public void goBack() {
        if (mWebview == null) {
            return;
        }
        // 获取历史列表
        WebBackForwardList mWebBackForwardList = mWebview.copyBackForwardList();
        if (mWebBackForwardList == null) {
            return;
        }

        int hiscount = mWebBackForwardList.getSize();
        
        /*if (hiscount >= 1) {
            try {
                int curItem = mWebBackForwardList.getCurrentIndex();
                String url = mWebBackForwardList.getItemAtIndex(curItem).getUrl();
                String url1 = mWebBackForwardList.getItemAtIndex(curItem - 1).getUrl();
                int backvalue = -1;
                if (url.equals(url1)) {
                    backvalue = -2;
                }
                mWebview.goBackOrForward(backvalue);
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }*/
        
        if(!WebViewManager.getInstance().getIsInterface()){
            if (hiscount >= 1) {
                try {
                    /*int curItem = mWebBackForwardList.getCurrentIndex();
                    String url = mWebBackForwardList.getItemAtIndex(curItem).getUrl();
                    String url1 = mWebBackForwardList.getItemAtIndex(curItem - 1).getUrl();
                    int backvalue = -1;
                    if (url.equals(url1)) {
                        backvalue = -2;
                    }*/
                    mWebview.goBackOrForward(goBackCount);
                    goBackCount = -1;
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }
        }else{
            clearAllLayer();
            /*if(mWebview.canGoBack()){
                mWebview.goBackOrForward(-1);
            }*/
            QEventBus.getEventBus().post(new BrowserEvents.loadUrl1(WebViewManager.getInstance().getUrl(),false,"baidu",false));
            
            WebViewManager.getInstance().setIsInterface(false);
        }
        
        /*if(!WebViewManager.getInstance().getIsInterface()){
            if (hiscount >= 1) {
                try {
                    int curItem = mWebBackForwardList.getCurrentIndex();
                    String url = mWebBackForwardList.getItemAtIndex(curItem).getUrl();
                    String url1 = mWebBackForwardList.getItemAtIndex(curItem - 1).getUrl();
                    int backvalue = -1;
                    if (url.equals(url1)) {
                        backvalue = -2;
                    }
                    mWebview.goBackOrForward(backvalue);
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }
        }else{
            if(WebViewManager.getInstance().getPageNum() > hiscount - 1){
                try {
                    mWebview.goBackOrForward(-hiscount + 1);
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }else{
                try {
                    mWebview.goBackOrForward(-WebViewManager.getInstance().getPageNum());
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }
            WebViewManager.getInstance().setIsInterface(false);
        }*/
    }

    public boolean canGoForward() {
        if (mWebview != null)
            return mWebview.canGoForward();
        else
            return false;
    }

    public boolean CheckGoBackUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith(SEARCH_URL_J_PREFIX)) {
                return true;
            }
            if (url.equalsIgnoreCase(BLANK_URL)) {
                return true;
            }
        }
        return false;
    }

    public boolean canGoBack() {
        if (mWebview == null) {
            return false;
        }

        WebBackForwardList mWebBackForwardList = mWebview.copyBackForwardList();
        if (mWebBackForwardList == null) {
            return mWebview.canGoBack();
        }

        /*
         * List<String> urllist=new ArrayList<String>(); int
         * count=mWebBackForwardList.getSize(); for(int
         * index=0;index<count;index++){
         * urllist.add(mWebBackForwardList.getItemAtIndex(index).getUrl()); }
         */
        int curindex = mWebBackForwardList.getCurrentIndex();
        if (curindex == 1) {
            String url = mWebBackForwardList.getItemAtIndex(curindex).getUrl().toLowerCase();
            String url1 = mWebBackForwardList.getItemAtIndex(curindex - 1).getUrl().toLowerCase();
            if (url.contains(url1)) {
                return false;
            }
        }

        // Fix Bug 54682 - 【标签】页面没加载完时点后退按钮，有时会没退到之前的页面
        if (curindex > 0) {
            String urlPrev = mWebBackForwardList.getItemAtIndex(curindex - 1).getUrl().toLowerCase();
            if (urlPrev.equals("about:blank")) {
                return false;
            }
            if(curindex > 1){
                String url = mWebBackForwardList.getItemAtIndex(curindex).getUrl().toLowerCase();
                int i = 0;
                for(i=curindex-1;i>=0;i--){
                    if(url.contains(mWebBackForwardList.getItemAtIndex(i).getUrl().toLowerCase())){
                        continue;
                    }
                    if(mWebBackForwardList.getItemAtIndex(i).getUrl().equals("about:blank")){
                        return false;
                    }
                    break;
                }
                if(i == -1){
                    return false;
                }
                goBackCount = -(curindex - i);
                /*String urlPrev1 = mWebBackForwardList.getItemAtIndex(curindex - 2).getUrl();
                if (url.equals(urlPrev) && urlPrev1.equals("about:blank")) {
                    return false;
                }*/
            }
        }
        return mWebview.canGoBack();
    }

    public boolean canScrollLeft() {
        if (mWebview != null)
            return mWebview.canScrollLeft();
        else
            return false;
    }

    public boolean isEqualView(WebView webView) {
        if (mWebview != null)
            return mWebview.equals(webView);
        else
            return false;
    }

//    public QihooWebview getRealWebview() {
    public BridgeWebView getWebview() {
        if (mWebview == null) {
            mWebview = new BridgeWebView(getContext());
        }
        return mWebview;
    }

    // webview 的 pause 或 resume 关系的视频的是否播放。
    public void onPause() {
        try {
            if (mWebview != null) {
                mWebview.getClass().getMethod("onPause").invoke(mWebview, (Object[]) null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("NewApi")
    public void onResume(){
        try {
            if (mWebview != null) {
                mWebview.getClass().getMethod("onResume").invoke(mWebview, (Object[]) null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private Bitmap mCacheBitmap;

    public void showCacheView() {
        if (mWebview == null)
            return;
        unShowCacheView();
        mCacheImageView.setVisibility(View.VISIBLE);

        try {
            mCacheBitmap = Bitmap.createBitmap(mWebview.getWidth(), mWebview.getHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            draw(new Canvas(mCacheBitmap));
            mCacheImageView.setImageBitmap(mCacheBitmap);
        } catch (Exception e) {
            mCacheImageView.setVisibility(View.GONE);
        }
    }

    public void unShowCacheView() {
        mCacheImageView.setVisibility(View.GONE);
        mCacheImageView.setImageBitmap(null);
        if (mCacheBitmap != null && !mCacheBitmap.isRecycled()) {
            mCacheBitmap.recycle();
            mCacheBitmap = null;
        }
    }

    public void clearContent() {
        if (mWebview != null) {
            mWebview.removeAllViews();
            mWebview.LoadBankUrl();
            mWebview.clearView();
            clearHistory();
            showErrorPage(false);
        }
    }

    public void destory() {
        NetworkManager.getInstance(mContext).unRegisterReceiver();
        removeAllViews();
        //LocalAppUpdateManager.getInstance().removeObserver(this);
        if (mWebview != null) {
            mWebview.clearCache(false);
            mWebview.destroyDrawingCache();
            mWebview.removeAllViews();
            mWebview.LoadBankUrl();
            mWebview.clearHistory();
            mWebview.destroy();
            mWebview = null;
        }
        // unShowCacheView();
        mErrorPageView = null;

        // mRefreshImageView = null;
//        System.gc();
    }


    private PageLoadingWatchInfo.LoadingListener _loadinglistener = new PageLoadingWatchInfo.LoadingListener() {

        @Override
        public void onNetWorkError(MyBridgeWebView browserWebView) {
            if (null != browserWebView)
                browserWebView.showErrorPage(true);
        }

        @Override
        public void OnTimeOut(MyBridgeWebView browserWebView) {
            if (null != browserWebView)
                browserWebView.showErrorPage(true);
        }
    };

    /*public void onEventMainThread(BrowserEvents.onReceivedError event) {
        if (event == null) {
            return;
        }
        showErrorPage(true);
    }*/
    
    @Override
    public void setTag(Object tag) {
        if (mWebview != null) {
            mWebview.setTag(tag);
        }
    }

    @Override
    public Object getTag() {
        if (mWebview != null) {
            return mWebview.getTag();
        }else {
            return null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    
    public void callHandler(String handlerName, String data, CallBackFunction callBack){
        if (mWebview != null) {
            mWebview.callHandler(handlerName,data,callBack);
        }
    }
    public void send(String message){
        if (mWebview != null) {
            mWebview.send(message);
        }
    }
}
