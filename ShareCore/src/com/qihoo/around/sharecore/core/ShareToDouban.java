package com.qihoo.around.sharecore.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.qihoo.around.sharecore.core.douban.DoubanAuthDialog;
import com.qihoo.around.sharecore.core.douban.IDoubanListener;
import com.qihoo.around.sharecore.openid.OpenId;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

public class ShareToDouban {

	/**
	 * 
	 * @param activity
	 * @param text
	 *            广播内容
	 * @param rec_title
	 *            推荐网址的标题
	 * @param rec_url
	 *            推荐网址的href
	 * @param rec_desc
	 *            推荐网址的描述
	 * @param rec_image
	 *            推荐网址的附图url
	 * @param listener
	 *            回调
	 */
	public static void share(final Activity activity, final String text, final String rec_title, final String rec_url,
			final String rec_desc, final String rec_image, final IDoubanListener listener) {

		Oauth2AccessToken oauth2AccessToken = AccessTokenKeeper.readDoubanAccessToken(activity.getApplicationContext());
		if (oauth2AccessToken != null && oauth2AccessToken.isSessionValid()) {
			shareToDouban(activity, oauth2AccessToken.getToken(), text, rec_title, rec_url, rec_desc, rec_image,
					listener);
		} else {
			final DoubanAuthDialog dialog = new DoubanAuthDialog(activity);
			DoubanAuthLsn authListener = new DoubanAuthLsn(activity, listener) {

				@Override
				public void onGetAccessToken() {
					Oauth2AccessToken oauth2AccessToken = AccessTokenKeeper.readDoubanAccessToken(activity
							.getApplicationContext());
					shareToDouban(activity, oauth2AccessToken.getToken(), text, rec_title, rec_url, rec_desc,
							rec_image, listener);
				}
			};
			dialog.setAuthListener(authListener);
			activity.runOnUiThread(new Runnable() {
				public void run() {
					dialog.show();
				}
			});
		}

	}

	public static abstract class DoubanAuthLsn implements IDoubanListener {
		public Activity mActivity;
		public IDoubanListener mShareListener;

		public DoubanAuthLsn(Activity activity, IDoubanListener shareListener) {
			this.mActivity = activity;
			this.mShareListener = shareListener;
		}

		@Override
		public void onComplete(Object object) {

		}

		@Override
		public void onCancel() {
			if (mShareListener != null) {
				mActivity.runOnUiThread(new Runnable() {
					public void run() {
						mShareListener.onCancel();
					}
				});
			}
		}

		@Override
		public void onError(final Exception ex) {
			if (mShareListener != null) {
				mActivity.runOnUiThread(new Runnable() {
					public void run() {
						mShareListener.onError(ex);
					}
				});
			}
		}

		public void onAuth(String code) {
			getAuthToken(mActivity, code, this);
		}

		public abstract void onGetAccessToken();
	}

	private static void getAuthToken(final Activity context, final String code, final DoubanAuthLsn listener) {
		new Thread() {
			public void run() {
				try {
					HttpPost httpPost = new HttpPost(OpenId.DOUBAN_TOKEN_URL);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("client_id", OpenId.DOUBAN_CLIENT_ID));
					nameValuePairs.add(new BasicNameValuePair("client_secret", OpenId.DOUBAN_APP_SECRET));
					nameValuePairs.add(new BasicNameValuePair("redirect_uri", OpenId.DOUBAN_REDIRECT_URI));
					nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
					nameValuePairs.add(new BasicNameValuePair("code", code));
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
					HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
					final String result = EntityUtils.toString(httpResponse.getEntity());
//					Log.e("douban", "getAuthToken:  " + result);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						context.runOnUiThread(new Runnable() {
							public void run() {
								JSONObject object;
								try {
									object = new JSONObject(result);
									String access_token = object.optString("access_token");
									if (access_token != null) {
										String expires_in = object.optString("expires_in");
										String refresh_token = object.optString("refresh_token");
										String douban_user_id = object.optString("douban_user_id");
										Oauth2AccessToken accessToken = new Oauth2AccessToken();
										accessToken.setExpiresIn(expires_in);
										accessToken.setRefreshToken(refresh_token);
										accessToken.setUid(douban_user_id);
										accessToken.setToken(access_token);
										AccessTokenKeeper.writeDoubanAccessToken(context.getApplicationContext(),
												accessToken);
										listener.onGetAccessToken();
									}
								} catch (JSONException e) {
									//LogUtils.e(e);
									listener.onError(e);
								}
							}
						});
					} else {
						listener.onError(new Exception(result));
					}
				} catch (Exception e) {
					//LogUtils.e(e);
					listener.onError(e);
				}
			}
		}.start();
	}

	private static void shareToDouban(final Activity activity, final String accessToken, final String text,
			final String rec_title, final String rec_url, final String rec_desc, final String rec_image,
			final IDoubanListener listener) {
		new Thread() {
			public void run() {
				HttpPost httpPost = new HttpPost("https://api.douban.com/shuo/v2/statuses/");
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("source", OpenId.DOUBAN_CLIENT_ID));
				params.add(new BasicNameValuePair("text", text));
				params.add(new BasicNameValuePair("rec_title", rec_title));
				params.add(new BasicNameValuePair("rec_url", rec_url));
				params.add(new BasicNameValuePair("rec_desc", rec_desc));
				params.add(new BasicNameValuePair("rec_image", rec_image));
				httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Trident/4.0)");
				httpPost.setHeader(new BasicHeader("Authorization", String.format("Bearer %1$s", accessToken)));
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
					final String result = EntityUtils.toString(httpResponse.getEntity());
//					Log.e("douban", "shareToDouban:  " + result);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						if (listener != null) {
							activity.runOnUiThread(new Runnable() {
								public void run() {
									listener.onComplete(result);
								}
							});
						}
					} else {
						if (listener != null) {
							activity.runOnUiThread(new Runnable() {
								public void run() {
									listener.onError(new Exception("网络错误: " + result));
								}
							});
						}
					}
				} catch (final IOException e) {
					//LogUtils.e(e);
					if (listener != null) {
						activity.runOnUiThread(new Runnable() {
							public void run() {
								listener.onError(e);
							}
						});
					}
				}
			};
		}.start();

	}
}
