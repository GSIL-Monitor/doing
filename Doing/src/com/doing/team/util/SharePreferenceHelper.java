package com.doing.team.util;

import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.doing.team.DoingApplication;
import com.doing.team.bean.RegisterRespond;
import com.doing.team.bean.UserInfo;
import com.doing.team.properties.Constant;
import com.google.gson.Gson;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

/**
 */
public class SharePreferenceHelper {
    private static SharedPreferences sSharedpreferences;

    private static SharedPreferences getSharePreference() {
        if (sSharedpreferences == null) {
            sSharedpreferences = PreferenceManager.getDefaultSharedPreferences(DoingApplication
                    .getInstance());
        }
        return sSharedpreferences;
    }

    public static boolean save(String key, String value) {
        SharedPreferences.Editor editor = getSharePreference().edit();
        try {
            editor.putString(key, value);
        } catch (Exception e) {
            editor.putString(key, value);
            LogUtils.e(e);
        }
        return editor.commit();
    }

    public static boolean saveResponceUserInfo(RegisterRespond userInfo) {
        SharedPreferences.Editor editor = getSharePreference().edit();
        String data = new Gson().toJson(userInfo);
        String encryptData = AESCoder.encryptToBase64(data, Constant.SECRET_KEY);
        editor.putString(Constant.RESPONSE_USER_INFO,encryptData);
        return editor.commit();
    }

    public static RegisterRespond getResponceUserInfo() {
        SharedPreferences sp = getSharePreference();
        String data = sp.getString(Constant.RESPONSE_USER_INFO, "");
        String decripyData = AESCoder.decryptBase64ToUtf8(data, Constant.SECRET_KEY);
        RegisterRespond userInfo = null;
        try {
            userInfo = new Gson().fromJson(decripyData,RegisterRespond.class);
        }catch (Exception e){
            LogUtils.e(e);
        }

        return userInfo;
    }

    public static boolean saveUserInfo(UserInfo userInfo) {
        SharedPreferences.Editor editor = getSharePreference().edit();
        editor.putString(Constant.USER_INFO, new Gson().toJson(userInfo));
        return editor.commit();
    }

    public static UserInfo getUserInfo() {
        SharedPreferences sp = getSharePreference();
        String data = sp.getString(Constant.USER_INFO, "");
        UserInfo userInfo = null;
        try {
            userInfo = new Gson().fromJson(data,UserInfo.class);
        }catch (Exception e){
            LogUtils.e(e);
        }

        return userInfo;
    }

    public static boolean registerSuccess() {
        SharedPreferences.Editor editor = getSharePreference().edit();
        try {
            editor.putBoolean(Constant.REGISTER, true);
        } catch (Exception e) {
            editor.putBoolean(Constant.REGISTER, true);
            LogUtils.e(e);
        }
        return editor.commit();
    }

    public static boolean isRegister() {
        SharedPreferences sp = getSharePreference();
        return sp.getBoolean(Constant.REGISTER, false);
    }


    public static boolean save(String keyName, List<?> list) {
        int size = list.size();
        if (size < 1) {
            return false;
        }
        SharedPreferences.Editor editor = getSharePreference().edit();
        if (list.get(0) instanceof String) {
            for (int i = 0; i < size; i++) {
                editor.putString(keyName + i, (String) list.get(i));
            }
        } else if (list.get(0) instanceof Long) {
            for (int i = 0; i < size; i++) {
                editor.putLong(keyName + i, (Long) list.get(i));
            }
        } else if (list.get(0) instanceof Float) {
            for (int i = 0; i < size; i++) {
                editor.putFloat(keyName + i, (Float) list.get(i));
            }
        } else if (list.get(0) instanceof Integer) {
            for (int i = 0; i < size; i++) {
                editor.putLong(keyName + i, (Integer) list.get(i));
            }
        } else if (list.get(0) instanceof Boolean) {
            for (int i = 0; i < size; i++) {
                editor.putBoolean(keyName + i, (Boolean) list.get(i));
            }
        }
        return editor.commit();
    }

    public static Map<String, ?> loadAllSharePreference(String key) {
        return getSharePreference().getAll();
    }

    public static boolean removeKey(String key) {
        SharedPreferences.Editor editor = getSharePreference().edit();
        editor.remove(key);
        return editor.commit();
    }

    public static boolean removeAllKey() {
        SharedPreferences.Editor editor = getSharePreference().edit();
        editor.clear();
        return editor.commit();
    }

    public static boolean HasKey(String key) {
        return getSharePreference().contains(key);
    }
}
