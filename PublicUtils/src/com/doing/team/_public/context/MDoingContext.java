package com.doing.team._public.context;

import com.doing.team._public.order.OrderConfig;

import android.content.Context;

/**
 * 公共Context
 * @author wangzhiheng
 *
 */
public class MDoingContext {

    private static Context mApplicationContext;
    private static String VersionName = "";
    private static int VersionCode = 0;
    private static String Channel = "";
    private static String lastChannel = "";
    private static String AssetChannel = "";
    private static OrderConfig orderConfig = null;

    private static boolean isShowHistoryView = true;

    public static Context getApplicationContext() {
        return mApplicationContext;
    }

    public static void setApplicationContext(Context context) {
        mApplicationContext = context;
    }

    public static String getVersionName() {
        return VersionName;
    }

    public static void setVersion(String Name, int Code) {
        VersionName = Name;
        VersionCode = Code;
    }

    public static int getVersionCode() {
        return VersionCode;
    }

    public static void setChannel(String channel) {
        Channel = channel;
    }

    public static void setLastChannel(String channel) {
        lastChannel = channel;
    }

    public static String getLastChannel() {
        return lastChannel;
    }
    
    public static String getChannel() {
        return Channel;
    }

    public static void setAssetChannel(String channel) {
        AssetChannel = channel;
    }

    public static String getAssetChannel() {
        return AssetChannel;
    }
    
    public static void setShowHistoryView(boolean show) {
        isShowHistoryView = show;
    }
    
    public static boolean isShowHistoryView() {
        return isShowHistoryView;
    }
 
    
}
