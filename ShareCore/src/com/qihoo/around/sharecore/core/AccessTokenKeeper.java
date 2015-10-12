/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qihoo.around.sharecore.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * 该类定义了微博授权时所需要的参数。
 * 
 * @author SINA
 * @since 2013-10-07
 */
public class AccessTokenKeeper {
	private static final String PREFERENCES_NAME = "a_t_4_o_i"; // access_token_for_open_id

	private static final String WEIBO_KEY_UID = "s_w_u_i";// sina_weibo_user_id
	private static final String WEIBO_KEY_ACCESS_TOKEN = "s_w_a_t"; // sina_weibo_access_token
	private static final String WEIBO_KEY_EXPIRES_IN = "s_w_e_i"; // sina_weibo_expires_i

	private static final String DOUBAN_KEY_UID = "d_b_u_i";
	private static final String DOUBAN_KEY_ACCESS_TOKEN = "d_b_a_t"; // sina_weibo_access_token
	private static final String DOUBAN_KEY_EXPIRES_IN = "d_b_e_i"; // sina_weibo_expires_i
	
	private static final String WEIBO_TOKEN_FROM = "w_b_t_f"; 

	/**
	 * 保存 Token 对象到 SharedPreferences。
	 * 
	 * @param context
	 *            应用程序上下文环境
	 * @param token
	 *            Token 对象
	 */
	public static void writeWeiboAccessToken(Context context, Oauth2AccessToken token) {
		if (null == context || null == token) {
			return;
		}

		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(WEIBO_KEY_UID, token.getUid());
		editor.putString(WEIBO_KEY_ACCESS_TOKEN, token.getToken());
		editor.putLong(WEIBO_KEY_EXPIRES_IN, token.getExpiresTime());
		editor.commit();
	}
	
	public static void writeWeiboAccessTokenFrom(Context context, String from) {
        if (null == context || TextUtils.isEmpty(from)) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString(WEIBO_TOKEN_FROM, from);
        editor.commit();
    }
	
	public static String readWeiboAccessTokenFrom(Context context) {
        if (null == context) {
            return null;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        String from = pref.getString(WEIBO_TOKEN_FROM, "");
        return from;
    }

	/**
	 * 保存 Token 对象到 SharedPreferences。
	 * 
	 * @param context
	 *            应用程序上下文环境
	 * @param token
	 *            Token 对象
	 */
	public static void writeDoubanAccessToken(Context context, Oauth2AccessToken token) {
		if (null == context || null == token) {
			return;
		}

		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(DOUBAN_KEY_UID, token.getUid());
		editor.putString(DOUBAN_KEY_ACCESS_TOKEN, token.getToken());
		editor.putLong(DOUBAN_KEY_EXPIRES_IN, token.getExpiresTime());
		editor.commit();
	}

	/**
	 * 从 SharedPreferences 读取 Token 信息。
	 * 
	 * @param context
	 *            应用程序上下文环境
	 * 
	 * @return 返回 Token 对象
	 */
	public static Oauth2AccessToken readWeiboAccessToken(Context context) {
		if (null == context) {
			return null;
		}
		Oauth2AccessToken token = new Oauth2AccessToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		token.setUid(pref.getString(WEIBO_KEY_UID, ""));
		token.setToken(pref.getString(WEIBO_KEY_ACCESS_TOKEN, ""));
		token.setExpiresTime(pref.getLong(WEIBO_KEY_EXPIRES_IN, 0));
		return token;
	}

	/**
	 * 从 SharedPreferences 读取 Token 信息。
	 * 
	 * @param context
	 *            应用程序上下文环境
	 * 
	 * @return 返回 Token 对象
	 */
	public static Oauth2AccessToken readDoubanAccessToken(Context context) {
		if (null == context) {
			return null;
		}
		Oauth2AccessToken token = new Oauth2AccessToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		token.setUid(pref.getString(DOUBAN_KEY_UID, ""));
		token.setToken(pref.getString(DOUBAN_KEY_ACCESS_TOKEN, ""));
		token.setExpiresTime(pref.getLong(DOUBAN_KEY_EXPIRES_IN, 0));
		return token;
	}
	
	/**
     * 清空 SharedPreferences 中 Token信息。
     * 
     * @param context
     *            应用程序上下文环境
     */
    public static void clear(Context context) {
        if (null == context) {
            return;
        }

        SharedPreferences pref = context
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

}
