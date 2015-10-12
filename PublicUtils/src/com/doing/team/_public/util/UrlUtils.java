/*
 * Tint Browser for Android
 * 
 * Copyright (C) 2012 - to infinity and beyond J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.doing.team._public.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.webkit.URLUtil;

import com.qihoo.haosou.msearchpublic.util.LogUtils;

/**
 * Url management utils.
 */
public class UrlUtils {

    public static final Pattern ACCEPTED_URI_SCHEMA = Pattern.compile("(?i)" + // switch
            // on
            // case
            // insensitive
            // matching
            "(" + // begin group for schema
            "(?:http|https|file|360around):\\/\\/" + "|(?:inline|data|about|javascript):" + ")" + "(.*)");

    /**
     * Check if a string is an url. For now, just consider that if a string
     * contains a dot, it is an url.
     *
     * @param url The url to check.
     * @return True if the string is an url.
     */
    public static boolean isUrl(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        return ACCEPTED_URI_SCHEMA.matcher(url).matches();
    }
    public static boolean isAcceptUrl(String url){
    	Matcher m = ACCEPTED_URI_SCHEMA.matcher(url);
    	return m.matches();
    }

    /**
     * Check en url. Add http:// before if missing.
     *
     * @param url The url to check.
     * @return The modified url if necessary.
     */
    public static String checkUrl(String url) {
        if (!TextUtils.isEmpty(url)) {

            if (((!url.startsWith("http://") && !url.startsWith("http://")) && url.split(".").length != 2)
                    && !url.startsWith("javascript") && (!url.startsWith("file://"))) {

                url = "http://" + url;

            }
        }

        return url;
    }

    /**
     * 解析出url请求的路径，包括页面
     *
     * @param strURL url地址
     * @return url路径
     */
    public static String getUrlPage(String strURL) {
        if (strURL==null) {
            return "";
        }
        String strPage = null;
        String[] arrSplit = null;
        strURL = strURL.trim().toLowerCase();
        strPage = strURL;
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 0) {
            if (arrSplit.length > 1) {
                if (arrSplit[0] != null) {
                    strPage = arrSplit[0];
                }
            }
        }
        return strPage;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    public static String truncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;
        strURL = strURL.trim().toLowerCase();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }
        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> getUrlKvMap(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = truncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        // 每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            // 解析出键值
            if (arrSplitEqual.length > 1) {
                // 正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    // 只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }


    public static String getUrlValueWithKey(String url, String key) {
        String value = "";
        String[] arrSplit = null;
        String strUrlParam = truncateUrlPage(url);
        if (strUrlParam == null) {
            return "";
        }
        // 每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            // 解析出键值
            if (arrSplitEqual.length > 1) {
                // 正确解析
                if (arrSplitEqual[0].equals(key)) {
                    value = arrSplitEqual[1];
                    break;
                }
            }
        }
        return value;
    }
    
    public static String isSearchListUrl(String url){
        String keyword = null;
        if(!TextUtils.isEmpty(url) && URLUtil.isNetworkUrl(url)){
            if((url.contains("m.map.haosou.com") && url.contains("#search/list/keyword")) || (url.contains("m.map.haosou.com") && url.contains("#search/city_list/keyword"))){
                int start = url.indexOf("keyword");
                int temp = url.indexOf("&", start);
                int end = temp;
                if(temp == -1){
                    end = url.length() - 1;
                }
                String subString = url.substring(start, end);
                String[] tempSplit = subString.split("[=]");
                if(tempSplit.length > 1){
                    keyword = tempSplit[1];
                }
                
                /*String[] arrSplit = url.split("[&]");
                if(arrSplit.length > 1){
                    String[] tempSplit = arrSplit[0].split("[=]");
                    if(tempSplit.length > 1){
                        keyword = tempSplit[1];
                    }
                }*/
            }
        }
        return keyword;
    }

    public static String addOrmodefyUrlValueWithKey(String url, String key, String newValue) {
        try {
            URI uri;
            URL webUrl = new URL(url);
            String webHost = webUrl.getHost();
            if (!webHost.contains("so.com") && !webHost.contains("haosou.com")) {
                return url;
            }
            String query = webUrl.getQuery();

            if (TextUtils.isEmpty(query)) {
                query = key + "=" + newValue;
                uri = new URI(webUrl.getProtocol(), webUrl.getUserInfo(), webHost, webUrl.getPort(), webUrl.getPath(), query, webUrl.getRef());
                return uri.toString();
            } else {
                String[] params = query.split("[&]");
                boolean flag = false;
                Map<String, String> paramsMap = new HashMap<String, String>();
                for (String str : params) {
                    String[] split = str.split("[=]");
                    if (split.length == 2) {
                        if (split[0].equals(key)) {
                            flag = true;
                            paramsMap.put(split[0], newValue);
                        } else {
                            paramsMap.put(split[0], split[1]);
                        }
                    }
                }
                if (!flag) {
                    paramsMap.put(key, newValue);
                }
                StringBuffer buffer = new StringBuffer();
                Iterator<Map.Entry<String, String>> iterator = paramsMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> next = iterator.next();
                    buffer.append(next.getKey());
                    buffer.append('=');
                    buffer.append(next.getValue());
                    buffer.append('&');
                }
                buffer.deleteCharAt(buffer.length() - 1);
                String newQuery = buffer.toString();
                return url.replace(query, newQuery);
            }

        } catch (MalformedURLException e) {
            LogUtils.e(e);
            return url;
        } catch (URISyntaxException e) {
            LogUtils.e(e);
            return url;
        }
    }



}
