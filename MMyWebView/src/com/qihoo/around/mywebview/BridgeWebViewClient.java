/**
 * @author zhaozuotong
 * @since 2015-5-8 下午12:23:28
 */

package com.qihoo.around.mywebview;
import com.qihoo.around._public.eventbus.QEventBus;
import com.qihoo.around._public.eventdefs.BrowserEvents;
import com.qihoo.around._public.util.UrlUtils;
import com.qihoo.around.mywebview.jsBridge.BridgeHandler;
import com.qihoo.around.mywebview.jsBridge.BridgeUtil;
import com.qihoo.around.mywebview.jsBridge.CallBackFunction;
import com.qihoo.around.mywebview.jsBridge.DefaultHandler;
import com.qihoo.around.mywebview.jsBridge.Message;
import com.qihoo.around.mywebview.jsBridge.WebViewJavascriptBridge;
import com.qihoo.around.mywebview.utils.UrlConfigUtils;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BridgeWebViewClient extends WebViewClient implements WebViewJavascriptBridge {
    
    String toLoadJs = "WebViewJavascriptBridge.js";
    //String toLoadJs2 = "plusComp.js";
    Map<String, CallBackFunction> responseCallbacks = new HashMap<String, CallBackFunction>();
    Map<String, BridgeHandler> messageHandlers = new HashMap<String, BridgeHandler>();
    List<Message> startupMessage = new ArrayList<Message>();
    BridgeHandler defaultHandler = new DefaultHandler();
    BridgeWebView mWebView;
    long uniqueId = 0;
    Activity mActivity;
    private webPageListener mWPageListener;
    
    public BridgeWebViewClient(BridgeWebView webview,Activity activity){
        this.mActivity = activity;
        this.mWebView = webview;
        this.mWebView.setResponseListener(new responseListener() {
            
            @Override
            public void putResponseCallbacks(String jsUrl, CallBackFunction returnCallback) {
                // TODO Auto-generated method stub
                responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
            }
            
            @Override
            public void putMessageHandlers(String handlerName, BridgeHandler handler) {
                // TODO Auto-generated method stub
                messageHandlers.put(handlerName, handler);
            }
            
            @Override
            public void doSendThis(String handlerName, String data, CallBackFunction callBack) {
                // TODO Auto-generated method stub
                doSend(handlerName,data,callBack);
            }
            @Override
            public void sendThis(String message) {
                // TODO Auto-generated method stub
                send(message);
            }
        });
    }
    public interface responseListener {
        public void putResponseCallbacks(String jsUrl, CallBackFunction returnCallback);
        public void putMessageHandlers(String handlerName, BridgeHandler handler);
        public void doSendThis(String handlerName, String data, CallBackFunction callBack);
        public void sendThis(String message);
    }
    
    public interface webPageListener{
        public void onPageStarted(WebView view, String url, Bitmap favicon);
        public void onPageFinished(WebView view, String url);
        public void onReceivedClientTitle(WebView view, String url);
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl);
    }
    
    public void setWebPageListener(webPageListener listener){
        this.mWPageListener = listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogUtils.e("wzh","shouldOverrideUrlLoading :" + url+"......"+view.getOriginalUrl()+"....."+view.getUrl());
        String originalUrl = view.getOriginalUrl();
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
            handlerReturnData(url);
            return true;
        } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
            flushMessageQueue();
            return true;
        } else if (commOurDealwith(view, url)) {
            return true;
        }else if (!TextUtils.isEmpty(originalUrl) && originalUrl.contains("m.map.haosou.com") && originalUrl.contains("/keyword")){
            QEventBus.getEventBus().post(new BrowserEvents.LoadUrl(url,false,"",false,false));
            return true;
        }else {
            /*if(mWebView != null){
                mWebView.loadUrl(url);  
                // 消耗掉这个事件。Android中返回True的即到此为止吧,事件就不会冒泡传递了，我们称之为消耗掉  
                return true;  
            }*/
            return super.shouldOverrideUrlLoading(view, url);

        }
    }
    @Override            
    public void onReceivedSslError(WebView view,SslErrorHandler handler, SslError error) {
        handler.proceed(); // 接受所有证书            
    }
    
    @Override
    public void onLoadResource(WebView view, String url) {
        // TODO Auto-generated method stub
        super.onLoadResource(view, url);
    }
    protected boolean commOurDealwith(final WebView view, String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        
        if (startActivityForUrl(mActivity, url)) {
            return true;
        }
        if (TryStartActivity(mActivity,url)) {
            return true;
        }

        return false;

    }
    
    /**
     * check url for startactivity
     */
    private boolean startActivityForUrl(Context cxt, String url) {
        Intent intent = null;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (cxt.getPackageManager().resolveActivity(intent, 0) == null) {
            String packagename = intent.getPackage();
            if (packagename != null) {
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://search?q=pname:" + packagename));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                if (isSpecializedHandlerAvailable(cxt, intent)) {
                    try {
                        cxt.startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        } else {
            boolean bIsAccept = UrlUtils.isAcceptUrl(url);
            if (!bIsAccept && isSpecializedHandlerAvailable(cxt, intent)) {
                cxt.startActivity(intent);
                return true;
            }
        }
        return false;
    }
    
    /**
     * check installed for intent filter
     */
    private boolean isSpecializedHandlerAvailable(Context cxt, Intent intent) {
        PackageManager pm = cxt.getPackageManager();
        List<ResolveInfo> handlers = pm.queryIntentActivities(intent,
                PackageManager.GET_RESOLVED_FILTER);
        if (handlers == null || handlers.size() == 0) {
            return false;
        }
        for (ResolveInfo resolveInfo : handlers) {
            IntentFilter filter = resolveInfo.filter;
            if (filter == null) {
                continue;
            }
            if (filter.countDataAuthorities() == 0
                    && filter.countDataPaths() == 0) {
                // Generic handler, skip
                continue;
            }
            return true;
        }
        return false;
    }
    
    private boolean TryStartActivity(Activity activity, final String url) {
        if (url.startsWith("about:")
                || (url.startsWith("http:") || url.startsWith("https://") || url
                .startsWith("file"))) {
            return false;
        }

        /**
         * 该漏洞是由于安卓系统的intent意图协议URL造成,黑客通过一个URL就可以让当前的APP发送intent意图！！！
         * 严重的可以跨进程发送意图，默认情况可以对自己APP的任意组件发送意图。
         * 请各产品周知一下，该漏洞不仅影响浏览器还影响使用了webview组件的APP，
         * 产品中使用了webview访问了远程网页，lodaurl之前都必须检查URL，屏蔽掉含intent:的协议头的URL！！！
         */
        if (url.startsWith("intent:") || url.startsWith("content:")) {
            return true;
        }

        Intent intent;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (Exception ex) {
            return false;
        }

        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setComponent(null);
        try {
            if (activity.startActivityIfNeeded(intent, -1)) {
                return true;
            }
        } catch (Exception ex) {
            return true;
        }

        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (UrlConfigUtils.IsBlankUrl(url)) {
            return;
        }
        QEventBus.getEventBus().post(
                new BrowserEvents.onPageStarted(view, url));
        QEventBus.getEventBus().post(
                new BrowserEvents.onReceivedTitle(view, url));
        if(mWPageListener != null){
            mWPageListener.onPageStarted(view, url, favicon);
            mWPageListener.onReceivedClientTitle(view, url);
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        if (toLoadJs != null) {
            BridgeUtil.webViewLoadLocalJs(view, toLoadJs);
        }
        
        //用于联合登陆等
        /*if(toLoadJs2!=null){
        	BridgeUtil.webViewLoadLocalJs(view, toLoadJs2);
        }*/

        //
        if (startupMessage != null) {
            for (Message m : startupMessage) {
                dispatchMessage(m);
            }
            startupMessage = null;
        }
        QEventBus.getEventBus().post(
                new BrowserEvents.onPageFinished(view, url));
        QEventBus.getEventBus().post(
                new BrowserEvents.onReceivedTitle(view, url));
        QEventBus.getEventBus().post(new BrowserEvents.onChangeForceUseMyTitle(false));
        if(mWPageListener != null){
            mWPageListener.onPageFinished(view, url);
            mWPageListener.onReceivedClientTitle(view, url);
        }
    }
    
    private void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }
    
    private void queueMessage(Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }
    
    /**
     * 
     * @param handler
     *            default handler,handle messages send by js without assigned handler name,
     *            if js message has handler name, it will be handled by named handlers registered by native
     */
    public void setDefaultHandler(BridgeHandler handler) {
       this.defaultHandler = handler;
    }

    @SuppressLint("NewApi")
    private void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        //escape special characters for json string
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            mWebView.loadUrl(javascriptCommand);
        }
    }
    
    @SuppressLint("NewApi")
    public void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            mWebView.loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // deserializeMessage
                    List<Message> list = null;
                    try {
                        list = Message.toArrayList(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Message m = list.get(i);
                        String responseId = m.getResponseId();
                        // 是否是response
                        if (!TextUtils.isEmpty(responseId)) {
                            CallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            responseCallbacks.remove(responseId);
                        } else {
                            CallBackFunction responseFunction = null;
                            // if had callbackId
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        Message responseMsg = new Message();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                    }
                                };
                            } else {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                    }
                                };
                            }
                            BridgeHandler handler = null;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                            } else {
                                handler = defaultHandler;
                            }
                            if(handler != null){
                                handler.handler(m.getData(), responseFunction);
                            }
                        }
                    }
                }
            });
        }
    }
    
    @Override
    public void send(String data) {
        send(data, null);
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    public void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (errorCode == ERROR_UNKNOWN || errorCode == ERROR_HOST_LOOKUP
                || errorCode == ERROR_PROXY_AUTHENTICATION
                || errorCode == ERROR_CONNECT || errorCode == ERROR_IO
                || errorCode == ERROR_TIMEOUT
                || errorCode == ERROR_UNSUPPORTED_SCHEME
                || errorCode == ERROR_BAD_URL) {

            /*QEventBus.getEventBus().post(
                    new BrowserEvents.onReceivedError(view));*/
            
            if(mWPageListener != null){
                mWPageListener.onReceivedError(view, errorCode, description, failingUrl);
            }
        }
        super.onReceivedError(view, errorCode, description, failingUrl);
    }
}
