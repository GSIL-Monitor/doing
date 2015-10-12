/**
 * @author zhoushengtao
 * @since 2014-2-10 下午4:10:39
 */

package com.doing.team._public.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

/**
 * 截屏工具类
 */
public class CaptureUtil
{
    /**
     * 截屏 整个 activity 屏幕
     * 
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
	public static Bitmap captureScreen(Activity context)
    {
        // View是你需要截图的View
        View view = context.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        //System.out.println(statusBarHeight);

        // 获取屏幕长和高
        Point srcsize=new Point();
        context.getWindowManager().getDefaultDisplay().getSize(srcsize);
        //int width = context.getWindowManager().getDefaultDisplay().getWidth();
        //int height = context.getWindowManager().getDefaultDisplay().getHeight();
        
        //
        int contentTop = context.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        // statusBarHeight是上面所求的状态栏的高度
        int titleBarHeight = contentTop - statusBarHeight;
        int cutHeight = titleBarHeight + statusBarHeight;

        int bHeight = b1.getHeight();
        int nHeight = srcsize.y - cutHeight;
        if ((cutHeight + nHeight) > bHeight)
        {
            nHeight = bHeight - cutHeight;
        }
        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(b1, 0, cutHeight, srcsize.x, nHeight);
        view.destroyDrawingCache();

        return b;

    }

    /**
     * 截取webView快照(webView加载的整个内容的大小)
     * 
     * @param webView
     * @return
     */
    public static Bitmap captureWebView(WebView webView)
    {
        Bitmap bmp = Bitmap.createBitmap(webView.getWidth(), webView.getContentHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        webView.draw(canvas);
        return bmp;
    }

    /**
     * 截取webView 显示内容
     * 
     * @param webView
     * @return
     */
    public static Bitmap captureWebViewShow(WebView webView)
    {
        Bitmap bmp = Bitmap.createBitmap(webView.getWidth(), webView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        webView.draw(canvas);
        return bmp;
    }
    
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }
}
