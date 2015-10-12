/**
 * @author mudelong
 * @since 2014年8月6日 下午8:51:28
 */

package com.doing.team._public.util;

import android.os.Build;

/**
 * 机型适配
 */
public class AdaptationUtil {

    public static final int ANDROID_4 = 14;
    public static final int VIDEO_MIN_SDK_VERSION = 14;
    public static final int BARCODE_MIN_SDK_VERSION = 14;
    public static final int QUICK_SERARCH_VERSION = 11;
    public static final int NOTIFICATION_BIGCONTENTVIEW_VERSION = 16;
    public static final int PLUGINS_MIN_SDK_VERSION = 14;

    public static boolean isWantuSdkVersion() {
        return false;
    }

    public static boolean isAboveAndroid4() {
        return Build.VERSION.SDK_INT >= ANDROID_4;
    }
    
    //是否显示二维码入口
    public static boolean isBarCodeSdkVersion() {
        return Build.VERSION.SDK_INT >= BARCODE_MIN_SDK_VERSION;
    }

    /**
     * 常驻通知栏内部空间点击需要在SDK 3.0以上（11）。
     * 
     * @return
     */
    public static boolean isQuickSearchVersion() {
        return Build.VERSION.SDK_INT >= QUICK_SERARCH_VERSION;
    }

    /**
     * 通知栏的BigContentView需要在SDK4.1以上（16）
     * 
     * @return
     */
    public static boolean isBigContentViewVersion() {
        return Build.VERSION.SDK_INT >= NOTIFICATION_BIGCONTENTVIEW_VERSION;
    }

    /**
     * 判断是否支持影视插件播放搜索页面视频
     * 
     * @return
     */
    public static boolean isVideoPluginSupport() {
        return Build.VERSION.SDK_INT >= VIDEO_MIN_SDK_VERSION;
    }

    /*
     * return: if true: use setBackground()
     * if false:use setbackgroundDrawable()
     */
    public static boolean isBackgroundSupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * 判断支持插件的最低版本
     * @return
     */
    public static boolean isPluginsSupport(){
        return Build.VERSION.SDK_INT>=PLUGINS_MIN_SDK_VERSION;
    }
}
