package com.doing.team;

import java.lang.reflect.Method;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.webkit.WebView;

import com.doing.team._public.context.MDoingContext;
import com.doing.team.bean.UserInfo;
import com.doing.team.eventbus.QEventBus;
import com.doing.team.util.SharePreferenceHelper;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

public class DoingApplication extends Application {

    private static DoingApplication s_application;
    private String mProcessName = "";
    private UserInfo mUserInfo ;

    public static DoingApplication getInstance() {
        return s_application;
    }

    public DoingApplication() {
        super();
        s_application = this;
    }

    @Override
    public void onCreate() {
        // 修复Android 2.3下AsyncTask的java.lang.NoClassDefFoundError问题
        try {
            Class.forName("android.os.AsyncTask");
        } catch (Throwable ignore) {
        }
        super.onCreate();
        MDoingContext.setApplicationContext(s_application);
        MDoingContext.setVersion(getString(R.string.app_version_name), getVersionCode());
        s_application = this;
        LogUtils.SetDebugEnable(s_application);
        mProcessName = getCurProcessName(s_application);
        mUserInfo = SharePreferenceHelper.getUserInfo();
        QEventBus.getEventBus().register(this);
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }
    public void saveUserinfo(UserInfo userInfo) {
        if (userInfo!=null){
            this.mUserInfo = userInfo;
            SharePreferenceHelper.saveUserInfo(mUserInfo);
        }
    }


    /**
     * 方法比较耗时,影响启动速度,将其排在启动后执行
     */
    public void enableWebContentsDebugging() {
        if (LogUtils.isDebug() && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)) {
            Method method = null;
            try {
                method = WebView.class.getDeclaredMethod("setWebContentsDebuggingEnabled",
                        new Class[]{boolean.class});
                if (method != null)
                    method.invoke(WebView.class, true);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }


    @Override
    public void onTerminate() {
        QEventBus.getEventBus().unregister(this);
        super.onTerminate();
    }


    public static String GetProcessName() {
        if (s_application == null) {
            return "";
        }
        return s_application.mProcessName;
    }

    /**
     * 获取当前进程名称
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        if (context == null)
            return "";
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid)
                return appProcess.processName;
        }
        return "";
    }

    private int getVersionCode() {
        int verCode = 0;
        PackageManager pm = getPackageManager();
        if (pm != null) {
            PackageInfo pkgInfo;
            try {
                pkgInfo = pm.getPackageInfo(getPackageName(), 0);
                verCode = pkgInfo.versionCode;
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
        return verCode;
    }


}
