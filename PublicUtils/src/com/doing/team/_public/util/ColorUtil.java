/**
 * @author yinxiucheng
 * @since 2014-8-26 下午1:29:28
 */

package com.doing.team._public.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.text.TextUtils;


public class ColorUtil {
    /**
     * 将Json中的颜色字符串转化成颜色值
     * @param object
     * @param colorJsonKey
     * @return
     */
    public static int parseJsonColor(JSONObject object, String  colorJsonKey, int defaultValue){
        
        if (null == object) {
            return defaultValue;
        }
        String colorStr = "";
        try {
            colorStr = object.getString(colorJsonKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        if (!TextUtils.isEmpty(colorStr)) {
            if (colorStr.startsWith("0x")) {
                return Color.parseColor(colorStr.replace("0x", "#"));
            }else if (colorStr.startsWith("#")) {
                return Color.parseColor(colorStr);
            }else {
                return defaultValue;
            }
        }else {
            return defaultValue;
        }
    }
    
    public static int parseColor(String  colorStr, int defaultValue){
        if (!TextUtils.isEmpty(colorStr)) {
            if (colorStr.startsWith("0x")) {
                return Color.parseColor(colorStr.replace("0x", "#"));
            }else if (colorStr.startsWith("#")) {
                return Color.parseColor(colorStr);
            }else {
                return defaultValue;
            }
        }else {
            return defaultValue;
        }
    }
}
