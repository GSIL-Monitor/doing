package com.doing.team.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpRequestBase;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.doing.team._public.util.DeviceUtils;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

public class CookieMgr {

    public synchronized static void setCookie(Context context, String domain, String cookie) {
        try {
            CookieSyncManager.getInstance();
        } catch (IllegalStateException ex) {
            CookieSyncManager.createInstance(context);
            CookieManager.getInstance().setAcceptCookie(true);
            LogUtils.d("HttpUtil", "Created CookieSyncManager instance");
        }
        CookieManager cookieManager = CookieManager.getInstance();
        boolean oldCookieSetting = cookieManager.acceptCookie();
        cookieManager.setAcceptCookie(true);
        if (cookie != null && !cookie.contains("domain=")) {
            cookie += " domain=" + domain;
        }
        cookieManager.setCookie(domain, cookie);
        CookieSyncManager.createInstance(context).sync();
        cookieManager.setAcceptCookie(oldCookieSetting);
    }

    public synchronized static void setNightModeCookie(Context context, String theme) {
        Map<String, String> cookies = new HashMap<String, String>();
        cookies.put("night_mode", !TextUtils.isEmpty(theme) && theme.equals("night") ? "1" : "0");
        setCookies(context, "so.com", cookies);
        setCookies(context, "haoso.com", cookies);
        setCookies(context, "haosou.com", cookies);
    }

    public synchronized static void setImageBlockCookie(Context context, boolean imgBlock) {
        Map<String, String> cookies = new HashMap<String, String>();
        cookies.put("image_block", imgBlock ? "1" : "0");
        setCookies(context, "so.com", cookies);
        setCookies(context, "haoso.com", cookies);
        setCookies(context, "haosou.com", cookies);
    }
    
    public synchronized static void setUserCenterCookie(Context context,String Q,String T) {
        Map<String, String> cookies = new HashMap<String, String>();
        cookies.put("Q", Q);
        cookies.put("T", T);
        cookies.put("__wid", DeviceUtils.getVerifyId(context));
        setCookies(context, "so.com", cookies);
        setCookies(context, "haoso.com", cookies);
        setCookies(context, "haosou.com", cookies);
    }
    
    /**
	 * 获取cookie里QT
	 * @return
	 */
    public synchronized static HashMap<String, String> getQTCookie(Context mContext) {
	        HashMap<String, String> ret = new HashMap<String, String>();
	        CookieSyncManager.createInstance(mContext);
	        CookieSyncManager.getInstance().sync();
	        CookieManager cookieManager = CookieManager.getInstance();
	        String cookie = cookieManager.getCookie("so.com");
	        if(TextUtils.isEmpty(cookie)){
	        	cookie=cookieManager.getCookie("haosou.com");
	        }
	        if (!TextUtils.isEmpty(cookie)) {
	            String cookies[] = cookie.split(";");
	            for (String val : cookies) {
	                val = val.trim();
	                if (val.contains("T" + "=")) {
	                    ret.put("T", val.substring(2));
	                }
	                
	                if (val.contains("Q" + "=")){
	                    ret.put("Q", val.substring(2));
	                }
	            }
	        }

	        return ret;
	    }

    public synchronized static void setCookie(Context context, String domain, String key,
            String value) {
        setCookie(context, domain, key + "=" + value + ";");
    }

    public synchronized static void setCookies(Context context, String domain, String[] cookies) {
        if (cookies == null || cookies.length <= 0) {
            return;
        }

        for (String cookie : cookies) {
            setCookie(context, domain, cookie);
        }
    }
    
    public synchronized static void removeCookie(Context context) {        
        CookieSyncManager.createInstance(context);          
        CookieManager cookieManager = CookieManager.getInstance();         
        cookieManager.removeAllCookie();        
        CookieSyncManager.getInstance().sync();      
    }

    public synchronized static void setCookies(Context context, String domain,
            Map<String, String> cookies) {
        if (cookies == null || cookies.size() <= 0) {
            return;
        }

        for (String key : cookies.keySet()) {
            String value = cookies.get(key);
            setCookie(context, domain, key, value);
        }

    }

    public synchronized static String getCookies(Context context, String domain) {
        CookieManager cookieManager = CookieManager.getInstance();
        return cookieManager.getCookie(domain);
    }

    public synchronized static CookieMap getCookieMap(Context context, String domain) {
        return CookieMap.fromCookies(getCookies(context, domain));
    }

    public static class CookieMap extends HashMap<String, String> {
        private static final long serialVersionUID = -4439552262504675698L;

        /**
         * 从Cookie字符串中解析，并创建CookieMap
         *
         * @param cookiesText
         * @return
         */
        public static CookieMap fromCookies(String cookiesText) {
            CookieMap map = new CookieMap();
            if (!TextUtils.isEmpty(cookiesText)) {
                String[] itemStrings = cookiesText.split(";");
                for (String itemString : itemStrings) {
                    if (TextUtils.isEmpty(itemString)) {
                        continue;
                    }

                    //itemString = itemString.substring(0, itemString.length() - 1);
                    String[] keyValuesStrings = itemString.split("=");
                    if (keyValuesStrings.length != 2) {
                        continue;
                    }

                    map.put(keyValuesStrings[0], keyValuesStrings[1]);
                }
            }

            return map;
        }

        /**
         * 返回Cookie字符串
         *
         * @return
         */
        public String toCookieString() {
            if (size() <= 0) {
                return null;
            }

            StringBuilder builder = new StringBuilder();
            for (String cookie : toCookieStringArray()) {
                builder.append(cookie);
            }

            return builder.toString();
        }

        /**
         * 返回Cookie的字符数组（每项均含一个Cookie）
         * 
         * @return
         */
        public String[] toCookieStringArray() {
            if (size() <= 0) {
                return null;
            }

            String[] result = new String[size()];
            int index = 0;
            for (Entry<String, String> cookie : entrySet()) {
                result[index++] = cookie.getKey() + "=" + cookie.getValue() + ";";
            }

            return result;
        }

        /**
         * 将Cookie设置到HttpPost中
         *
         * @param httpPost
         */
        public void setCookie(HttpRequestBase httpPost) {
            assert httpPost != null;

            if (size() <= 0) {
                return;
            }

            String cookies = toCookieString();
            httpPost.addHeader("Cookie", cookies);
        }
    }
}
