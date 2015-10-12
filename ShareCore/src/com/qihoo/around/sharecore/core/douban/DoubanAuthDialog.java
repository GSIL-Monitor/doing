package com.qihoo.around.sharecore.core.douban;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qihoo.around.sharecore.core.ShareToDouban;
import com.qihoo.around.sharecore.openid.OpenId;
import com.qihoo.around.sharecore.R;

public class DoubanAuthDialog extends Dialog {
    private WebView webView = null;
    private ShareToDouban.DoubanAuthLsn mAuthListener = null;

    public DoubanAuthDialog(Context context) {
        super(context, R.style.SHARE_DIALOG_TRANSPARENCY);
        setContentView(R.layout.share_core_auth_web);
        webView = (WebView) findViewById(R.id.s_auth_webview);
        webView.setWebViewClient(client);
        setOnCancelListener(onCancelListener);
        String url = String.format("%1$s&client_id=%2$s&redirect_uri=%3$s", OpenId.DOUBAN_AUTH_URL,
                OpenId.DOUBAN_CLIENT_ID, OpenId.DOUBAN_REDIRECT_URI);
        webView.loadUrl(url);
    }

    public void setAuthListener(ShareToDouban.DoubanAuthLsn authListener) {
        this.mAuthListener = authListener;
    }

    WebViewClient client = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(OpenId.DOUBAN_REDIRECT_URI)) {
                Uri uri = Uri.parse(url);
                String code = uri.getQueryParameter("code");
                if (code != null) {
                    if (mAuthListener != null) {
                        mAuthListener.onAuth(code);
                    }
                    dismiss();
                }
                return true;
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            try {
                handler.proceed();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    OnCancelListener onCancelListener = new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
            if (mAuthListener != null) {
                mAuthListener.onCancel();
            }
        }
    };

}
