/**
 * @author zhaozuotong
 * @since 2015-5-8 下午7:12:59
 */

package com.qihoo.around.mywebview;
import com.qihoo.around._public.eventbus.QEventBus;
import com.qihoo.around._public.eventdefs.BrowserEvents;
import com.qihoo.around.mywebview.utils.UrlConfigUtils;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyChromeClient extends WebChromeClient {
    private DelegatedOnReceiveTitle delegatedOnReceiveTitle;
    public MyChromeClient() {
    }


    /*@Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (mPageProgressView != null) {
            int progress =(int)( 40 + newProgress*0.6);
            mPageProgressView.setProgress(progress);
        }
    }*/
    
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        LogUtils.d("FunctionTracer", "newProgress:" + newProgress );
        String url = view.getUrl();
        if (UrlConfigUtils.IsBlankUrl(url)) {
            return;
        }
        super.onProgressChanged(view, newProgress);

        QEventBus.getEventBus().post(new BrowserEvents.onProgressChanged(view, newProgress));

    }
    
    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (UrlConfigUtils.IsBlankUrl(view.getUrl())) {
            return;
        }
        delegatedOnReceiveTitle.onReceivedTitle(view,title);
        super.onReceivedTitle(view, title);

    }
    public interface DelegatedOnReceiveTitle {
        void onReceivedTitle(WebView view, String title);
    }
    public void setDelegatedOnReceiveTitle(DelegatedOnReceiveTitle delegatedOnReceiveTitle) {
        this.delegatedOnReceiveTitle = delegatedOnReceiveTitle;
    }
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin,GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, false);      
        super.onGeolocationPermissionsShowPrompt(origin, callback);  
    } 
    
}
