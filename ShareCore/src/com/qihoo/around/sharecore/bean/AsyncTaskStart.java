package com.qihoo.around.sharecore.bean;

/**
 * Created by renjihai on 2015/1/15.
 */
public class AsyncTaskStart {
    private boolean cancelable = true;
    private String title;
    private String msg;
    private boolean showProgress;

    public AsyncTaskStart(boolean cancelable, boolean showProgress, String title, String msg) {
        this.cancelable = cancelable;
        this.title = title;
        this.msg = msg;
        this.showProgress = showProgress;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }
}


