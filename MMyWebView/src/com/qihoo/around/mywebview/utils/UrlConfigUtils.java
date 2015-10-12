/**
 * @author zhaozuotong
 * @since 2015-5-11 上午10:32:05
 */

package com.qihoo.around.mywebview.utils;

import android.text.TextUtils;

public class UrlConfigUtils {

    public static final String BLANK_URL="about:blank";
    public static boolean IsBlankUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.equalsIgnoreCase(BLANK_URL);
    }
}
