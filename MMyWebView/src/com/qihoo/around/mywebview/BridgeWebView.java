package com.qihoo.around.mywebview;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.qihoo.around._public.util.DeviceUtils;
import com.qihoo.around.mywebview.BridgeWebViewClient.responseListener;
import com.qihoo.around.mywebview.jsBridge.BridgeHandler;
import com.qihoo.around.mywebview.jsBridge.CallBackFunction;

@SuppressLint("SetJavaScriptEnabled")
public class BridgeWebView extends WebView {

	private final String TAG = "BridgeWebView";
	private final String BLANK_URL = "about:blank";
	
	public interface OnScrollChangedListenser {
	     public void onScrollChanged(int l, int t, int oldl, int oldt);
	}

	public OnScrollChangedListenser mOnScrollChangedListenser;
	
	public responseListener mResponseListener;

    public interface LongTouchListener {
        public void OnTouchImage(ContextMenu menu, String url);

        public void OnTouchLink(ContextMenu menu, String url);
    }

	public BridgeWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BridgeWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public BridgeWebView(Context context) {
		super(context);
		init(context);
	}

    @SuppressLint("NewApi")
    private void init(Context context) {
    	this.removeSearchBoxImpl();//安全漏洞
		this.setVerticalScrollBarEnabled(false);
		this.setHorizontalScrollBarEnabled(false);
		initSettings(context);
		//this.setWebViewClient(new BridgeWebViewClient());
	}
    
    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private void initSettings(Context context) {
        WebSettings settings = getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setUseWideViewPort(true);
        settings.setDefaultTextEncodingName(/*Config.DEFULT_ENCODE*/"gbk");
        settings.setLoadWithOverviewMode(true);
        // settings.setPluginState(PluginState.ON);
        settings.setAllowFileAccess(true);
        // settings.setPluginsEnabled(true);
        // settings.setAllowFileAccess(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        String dir = context.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath(); 
        settings.setGeolocationDatabasePath(dir);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setNeedInitialFocus(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUserAgentString(settings.getUserAgentString()+" 360around (" + DeviceUtils.getVersionName() + ")");
        settings.setDomStorageEnabled(true);
        settings.setSupportMultipleWindows(true);
        
        setScrollbarFadingEnabled(true);
        setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        setMapTrackballToArrowKeys(false); // use trackball direct

        ((Activity) getContext()).registerForContextMenu(this);

        // Fix Bug 54127 - 【webview】【119】好搜app问题页面不能输入内容，该手机的自带浏览器上问题页面可以输入内容
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
    }
    
    private String getVersionName() {
        try {
            String pkName = getContext().getPackageName();
            String versionName = getContext().getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
             e.printStackTrace();
        }
        return null;
    }

	public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
		this.loadUrl(jsUrl);
		if(mResponseListener != null){
		    mResponseListener.putResponseCallbacks(jsUrl, returnCallback);
		}
	}

	/**
	 * register handler,so that javascript can call it
	 * 
	 * @param handlerName
	 * @param handler
	 */
	public void registerHandler(String handlerName, BridgeHandler handler) {
		if (handler != null) {
		    if(mResponseListener != null){
		        mResponseListener.putMessageHandlers(handlerName, handler);
		    }
		}
	}

	/**
	 * call javascript registered handler
	 *
     * @param handlerName
	 * @param data
	 * @param callBack
	 */
	public void callHandler(String handlerName, String data, CallBackFunction callBack) {
	    if(mResponseListener != null){
	        mResponseListener.doSendThis(handlerName, data, callBack);
	    }
	}
	
	@Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mOnScrollChangedListenser != null)
            mOnScrollChangedListenser.onScrollChanged(l, t, oldl, oldt);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnScrollChangedListener(OnScrollChangedListenser l) {
        mOnScrollChangedListenser = l;
    }

    /**
     * 是否能向下滚动
     *
     * @return
     */
    public boolean canScrollDown() {
        // WebView的总高度
        float webViewContentHeight = getContentHeight() * getScale();
        // WebView的现高度
        float webViewCurrentHeight = (getHeight() + getScrollY());
        if (Math.abs((webViewContentHeight - webViewCurrentHeight)) < 0.000001f) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 能否向左边滑动
     *
     * @return
     */
    public boolean canScrollLeft() {
        return getScrollX() == 0;
    }

    /**
     * 能否向右边滑动
     *
     * @return
     */
    public boolean canScrollRight() {
        final int offset = computeHorizontalScrollOffset();
        final int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        if (range == 0)
            return false;
        return offset < range - 1;
    }

    @SuppressLint("NewApi")
    public void setLayerType(int layerType, Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setLayerType(layerType, paint);
        }
    }
    /**
     * fix the clear view not perform immediately
     */
    public void LoadBankUrl() {
        super.loadUrl(BLANK_URL);
    }
    
    public void send(String message){
        if(mResponseListener != null){
            mResponseListener.sendThis(message);
        }
    }
    
    public void setResponseListener(responseListener l){
        mResponseListener = l;
    }

    /**
     * 安全漏洞修复
     */
	private void removeSearchBoxImpl() {
		try {
			// if (hasHoneycomb() && !hasJellyBeanMR1()) {
			invokeMethod("removeJavascriptInterface", "searchBoxJavaBridge_");
			// }
		} catch (Exception e) {
		}

		try {
			// if (hasHoneycomb() && !hasJellyBeanMR1()) {
			invokeMethod("removeJavascriptInterface", "accessibility");
			// }
		} catch (Exception e) {
		}

		try {
			// if (hasHoneycomb() && !hasJellyBeanMR1()) {
			invokeMethod("removeJavascriptInterface", "accessibilityTraversal");
			// }
		} catch (Exception e) {
		}
	}

	private void invokeMethod(String method, String param) {
		Method m;
		try {
			m = WebView.class.getDeclaredMethod(method, String.class);
			m.setAccessible(true);
			m.invoke(this, param);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
