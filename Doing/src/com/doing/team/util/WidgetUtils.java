package com.doing.team.util;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.doing.team.DoingApplication;
import com.doing.team.R;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

/**
 * 工具类
 */
public class WidgetUtils {

	/**
	 * 隐藏软键盘
	 * 
	 * @param context
	 */
	public static void hideSoftKeyboard(Context context) {
		if (context == null)
			return;
		try {
			((InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(((Activity) context)
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			LogUtils.e(e);
		}

	}

	/**
	 * 全屏切换
	 */
	public void fullScreenChange(Activity activity, boolean isfull) {
		WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
		if (!isfull) {
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			activity.getWindow().setAttributes(attrs);
			// 取消全屏设置
			activity.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		} else {
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			activity.getWindow().setAttributes(attrs);
			activity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}

	/**
	 * 显示软键盘
	 * 
	 * @param context
	 */
	public static void showSoftKeyboard(final View view) {

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				InputMethodManager m = (InputMethodManager) view.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}

		}, 100);
	}
	public static void showSoftKeyboard(final Context context) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }, 100);
    }

	public static boolean isSoftKeyboardShowing(Context context) {
		InputMethodManager m = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return m.isActive();
	}

	/**
	 * 设置亮度
	 * 
	 * @param activity
	 * @param value
	 */
	public static void setBrightness(Activity activity, int value) {
		// 停止自动亮度
		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS, value);

		Uri uri = android.provider.Settings.System
				.getUriFor("screen_brightness");
		android.provider.Settings.System.putInt(activity.getContentResolver(),
				"screen_brightness", value);
		// resolver.registerContentObserver(uri, true, myContentObserver);
		activity.getContentResolver().notifyChange(uri, null);

	}

	/**
	 * 判断桌面是否已添加快捷方式
	 * 
	 * @param cx
	 * @param titleName
	 *            快捷方式名称
	 * @return
	 */
	public static boolean hasShortcut(Context cx, String title) {
		boolean result = false;
		// // 获取当前应用名称
		// try {
		// final PackageManager pm = cx.getPackageManager();
		// title = pm.getApplicationLabel(
		// pm.getApplicationInfo(cx.getPackageName(),
		// PackageManager.GET_META_DATA))
		// .toString();
		// } catch (Exception e) {
		// }

		String uriStr;
		if (android.os.Build.VERSION.SDK_INT < 8) {
			uriStr = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		Uri CONTENT_URI = Uri.parse(uriStr);
		Cursor c = cx.getContentResolver().query(CONTENT_URI, null, "title=?",
				new String[] { title }, null);
		if (c != null && c.getCount() > 0) {
			result = true;
			c.close();
		}
		return result;
	}

	/**
	 * 创建快捷方式
	 */
	public static void createShortCut(String showName, int iconId,
			Class<?> tagetClass) {

		if (hasShortcut(DoingApplication.getInstance(), showName)) {
			return;
		}
		// delShortcut(showName,tagetClass);
		// 创建快捷方式的Intent
		Intent shortcutIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		// 不允许重复创建
		shortcutIntent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, showName);
		// 快捷图片
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				DoingApplication.getInstance(), iconId);

		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

		Intent intent = new Intent(DoingApplication.getInstance(), tagetClass);
		// // 下面两个属性是为了当应用程序卸载时桌面 上的快捷方式会删除
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		// 点击快捷图片，运行的程序主入口
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 发送广播。OK
		DoingApplication.getInstance().sendBroadcast(shortcutIntent);
	}

	/**
	 * MesureMent
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10); // 可删除
		listView.setLayoutParams(params);
	}

	/**
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int convertDpToPixel(Context context, float dp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	/**
	 * 高亮关键字
	 * 
	 * @param titleTextView
	 * @param keyWords
	 *            关键字
	 * @param colorId
	 *            颜色的值，int
	 */
	@SuppressLint("DefaultLocale")
	public static void HighLightWords(TextView titleTextView, String keyWords,
			int color) {
		String text = titleTextView.getText().toString();

		if (TextUtils.isEmpty(keyWords) || TextUtils.isEmpty(text))
			return;
		// 创建一个 SpannableString对象
		SpannableString sp = new SpannableString(text);

		int start = text.toUpperCase().indexOf(keyWords.toUpperCase());
		if (start < 0 || (start + keyWords.length()) > sp.length())
			return;

		sp.setSpan(new ForegroundColorSpan(color), start,
				start + keyWords.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		titleTextView.setText(sp);
	}

	/**
	 * 高亮URL关键字 只匹配网址的中间部分 不匹配www等前缀
	 */
	@SuppressLint("DefaultLocale")
	public static void HighLightUrl(TextView titleTextView, String keyWords,
			String hilightText, int color) {
		String text = titleTextView.getText().toString();

		if (TextUtils.isEmpty(keyWords) || TextUtils.isEmpty(text)
				|| TextUtils.isEmpty(hilightText))
			return;

		// 创建一个 SpannableString对象
		SpannableString sp = new SpannableString(text);
		int index = text.toUpperCase().indexOf(hilightText.toUpperCase());
		int start = index >= 0 ? text.toUpperCase().indexOf(
				keyWords.toUpperCase(), index) : text.toUpperCase().indexOf(
				keyWords.toUpperCase());
		if (start < 0 || (start + keyWords.length()) > sp.length())
			return;

		sp.setSpan(new ForegroundColorSpan(color), start,
				start + keyWords.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		titleTextView.setText(sp);
	}

//	// 等待框
//	private static LoadingDialog s_loadingDialog;
//
//	/**
//	 * 显示等待框
//	 * 
//	 * @param context
//	 */
//	public static void showDialogLoading(Context context) {
//		try {
//			showDialogLoading(context,
//					context.getResources().getString(R.string.loading));
//		} catch (Exception e) {
//			LogUtils.e(e);
//		}
//	}
//
//	public static void showDialogLoading(Context context, int textId) {
//		try {
//			showDialogLoading(context, context.getResources().getString(textId));
//		} catch (Exception e) {
//			LogUtils.e(e);
//		}
//	}

//	public static void showDialogLoading(Context context, String text) {
//		if (s_loadingDialog == null)
//			s_loadingDialog = new LoadingDialog(context);
//		s_loadingDialog.setTipsText(text);
//		s_loadingDialog.show();
//	}
//
//	/**
//	 * 关闭等待框
//	 */
//	public static void unShowDialogLoading() {
//		if (s_loadingDialog == null) {
//			return;
//		} else {
//			s_loadingDialog.dismiss();
//			s_loadingDialog = null;
//		}
//
//	}

	/**
	 * 收起通知栏（部分手机常驻通知栏中RemoteView中的控件点击跳转的时候，通知栏没有默认的收起）
	 * 
	 * @param context
	 */
	public static void collapseStatusBar(Context context) {
		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		try {
			Object service = context.getSystemService("statusbar");
			Class<?> statusbarManager = Class
					.forName("android.app.StatusBarManager");
			Method collapse = null;
			if (service != null) {
				if (currentApiVersion <= 16) {
					collapse = statusbarManager.getMethod("collapse");
				} else {
					collapse = statusbarManager.getMethod("collapsePanels");
				}
				collapse.setAccessible(true);
				collapse.invoke(service);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
