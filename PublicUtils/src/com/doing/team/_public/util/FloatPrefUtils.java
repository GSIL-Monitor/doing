package com.doing.team._public.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * SharedPreferences工具类，可进程间共享
 * @author wangzefeng
 *
 */
public class FloatPrefUtils {

    private static final String FILE_NAME = "float_pref";
    private static final String LOCAL_FLOAT_STATUS_JSON = "float_status.json";
    
    //升级
    public static final String PREF_DOWNLOADED_APK_IN_SILENCE = "apk_silence";
    //配置
    public static final String PREF_FIRST_INSTALL_OR_OPEN= "push_app_first_or_open";
    public static final String PREF_LAST_PULL_DATE = "last_pull_date";
    public static final String PREF_LAST_PULL_NETWORK = "last_pull_network";
    public static final String PREF_LAST_PULL_DATE_MAIN_PROCESS = "last_pull_date_main_process";
    public static final String PREF_LAST_COUNT_BOOT_DATE = "last_count_boot_date";
    
    public static final String PREF_LAST_PULL_CONF_DATE = "last_pull_conf_date";
    public static final String PREF_PUSH_SETTING = "push_switch";	// 推送开关

    public static final String BROCAST_FILTER_PUSHMSG_RECEIVE = "brocast_pushmsg_receive";
    //订单小助手
    public static final String PREF_THIRD_ORDER_HAS_COME = "third_order_has_come"; 
    public static final String PREF_THIRD_ORDER_PHONE_NUMBER = "third_order_phone_number";
    public static final String PREF_THIRD_ORDER_NAME = "third_order_name";
    public static final String PREF_THIRD_ORDER_IDCARD = "third_order_idcard";
    public static final String PREF_THIRD_CACHE_ORDERID = "third_cache_order_id";
    public static final String PREF_THIRD_CACHE_ORDER_SMS = "third_cache_order_sms";
    public static final String PREF_THIRD_ORDER_HAS_READ = "third_order_has_read";
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_MULTI_PROCESS);
    }

    public static boolean getBooleanPref(Context context, String name, boolean defValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        try {
            return prefs.getBoolean(name, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static boolean getBooleanJson(Context context, String name, boolean defValue) {
    	FileSaver fileSaver = new FileSaver(context);
    	String json = fileSaver.LoadJsonFromFile(LOCAL_FLOAT_STATUS_JSON);
    	if(json.equals("")){
    		return defValue;
    	}
    	try {
			JSONObject object = new JSONObject(json);
			if(object.has(name)){
				return object.optBoolean(name,defValue);
			}
			return defValue;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return defValue;
		}
    }
    
    public static void setBooleanPref(Context context, String name, boolean value) {
        SharedPreferences.Editor prefs = getSharedPreferences(context).edit();
        prefs.putBoolean(name, value);
        prefs.commit();
    }

    public static void setBooleanJson(Context context, String name, boolean value) {

    	FileSaver fileSaver = new FileSaver(context);
    	String json = fileSaver.LoadJsonFromFile(LOCAL_FLOAT_STATUS_JSON);
    	try {
    		if(json.equals("")){
    			json = "{}";
    		}
			JSONObject object = new JSONObject(json);
			if(object.has(name)){
				object.remove(name);
			}
			object.put(name, value);
			fileSaver.SaveJsonToFile(LOCAL_FLOAT_STATUS_JSON, object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
    }
    
    public static int getIntPref(Context context, String name, int defValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        try {
            return prefs.getInt(name, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static void setIntPref(Context context, String name, int value) {
        SharedPreferences.Editor prefs = getSharedPreferences(context).edit();
        prefs.putInt(name, value);
        prefs.commit();
    }
    
    public static String getStringPref(Context context, String name, String defValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        try {
            return prefs.getString(name, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static void setStringPref(Context context, String name, String value) {
        SharedPreferences.Editor prefs = getSharedPreferences(context).edit();
        prefs.putString(name, value);
        prefs.commit();
    }

    public static long getLongPref(Context context, String name, long defValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        try {
            return prefs.getLong(name, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static void setLongPref(Context context, String name, long value) {
        SharedPreferences.Editor prefs = getSharedPreferences(context).edit();
        prefs.putLong(name, value);
        prefs.commit();
    }
}
