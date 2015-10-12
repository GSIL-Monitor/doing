/**
 * @author zhaozuotong
 * @since 2015-5-13 下午5:03:42
 */

package com.qihoo.around.mywebview.manager;

public class WebViewManager {

    private static WebViewManager mInstance;
    private Boolean isInterface = false;
    private int pageNum;
    private String url;
    private Boolean loginFromWeb = false;
    
    public static WebViewManager getInstance() {
        if (mInstance == null) {
            mInstance = new WebViewManager();
        }

        return mInstance;
    }
    
    public WebViewManager() {

    }

    public Boolean getIsInterface() {
        return this.isInterface;
    }

    public void setIsInterface(Boolean isInterface) {
        this.isInterface = isInterface;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getLoginFromWeb() {
        return this.loginFromWeb;
    }

    public void setLoginFromWeb(Boolean loginFromWeb) {
        this.loginFromWeb = loginFromWeb;
    }
    
    
}
