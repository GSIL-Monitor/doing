package com.qihoo.around.sharecore;

import com.qihoo.around.sharecore.aidl.IShareResourceFetcher;

/**
 * renjh1 2014年12月27日
 */

public class ShareManager {
    private static ShareManager sInstance = null;
    private static volatile Object sObject = new Object();

    private IShareResourceFetcher mShareCallback = null;

    public void setShareCallback(IShareResourceFetcher callback) {
        this.mShareCallback = callback;
    }

    public IShareResourceFetcher getShareCallback() {
        return mShareCallback;
    }

    public static ShareManager getInstance() {
        synchronized (sObject) {
            if (sInstance == null) {
                sInstance = new ShareManager();
            }
            return sInstance;
        }
    }

    private ShareManager() {
    }
}
