package com.doing.team._public.util;

import android.content.Context;
import android.widget.Toast;

import com.qihoo.haosou.msearchpublic.util.LogUtils;

public class ToastUtils {
    public static Toast sToast;
    public static boolean isDebug = true;

    public static void show(Context context, String msg) {
        if (!isDebug) {
            return;
        }
        if (sToast != null) {
            sToast.cancel();
        }
        sToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        sToast.show();
    }

    public static void show(Context context, String msg, int duration) {
        if (!LogUtils.isDebug()) {
            return;
        }
        if (sToast != null) {
            sToast.cancel();
        }
        sToast = Toast.makeText(context, msg, duration);
        sToast.show();
    }

}
