package com.qihoo.around.mywebview.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class ApkUtil {
    public static String getApkPackageName(Context context, String apkPath) {
        if (context == null)
            return "";

        PackageInfo info = context.getPackageManager().getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null)
            return info.packageName;

        return "";
    }

    public static boolean isProductionPackage(Context context) {
        try{
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if(appInfo==null) {
                return true;
            }

            return appInfo.metaData.getBoolean("PRODPACK", false);
        }catch(Exception e){
            return true;
        }
    }
}
