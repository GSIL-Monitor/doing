package com.qihoo.around.mywebview;

import android.webkit.WebView;

public class PageLoadingWatchInfo {
	
	public interface LoadingListener{
		public void onNetWorkError(MyBridgeWebView browserWebView);
		public void OnTimeOut(MyBridgeWebView browserWebView);
	}
	
	private WebView mWebView;
	private LoadingListener mListener;
	private MyBridgeWebView mBrowserWebView;
	private long mPageStartTime;
	
	public PageLoadingWatchInfo(MyBridgeWebView browserWebView, WebView webView,
			LoadingListener listener) {
		mWebView = webView;
		mBrowserWebView = browserWebView;
		mListener = listener;
		mPageStartTime = System.currentTimeMillis();
	}
	
	public boolean watch() {
		int progress = mWebView.getProgress();
		if (progress >= 100)
			return true;
		
		long curTime = System.currentTimeMillis();
		if (progress <= 1 && (curTime - mPageStartTime) / 1000 >= 10) {
			mListener.OnTimeOut(mBrowserWebView);
			return true;
		}
		
		if ((curTime - mPageStartTime) / 1000 >= 90) {
			mListener.onNetWorkError(mBrowserWebView);
			return true;
		}
		return false;
	}
	
	public boolean equal(PageLoadingWatchInfo other) {
		if (this.mWebView == other.mWebView)
			return true;
		else
			return false;
	}
}