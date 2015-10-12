package com.doing.team.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.doing.team._public.cache.DiskBasedLruBitmapCache;
import com.doing.team._public.cache.LruBitmapCache;
import com.doing.team._public.context.MDoingContext;

public class HttpManager {
    private final String TAG = HttpManager.class.getSimpleName();

    private static HttpManager httpmanager;

    private RequestQueue mRequestQueue = null;
    private ImageLoader mImageLoader = null;
    private ImageLoader firstPageImageLoader = null;

    public static HttpManager getInstance() {
        //LogUtils.d("volley", "HttpManager getInstance");
        if (httpmanager == null) {
            httpmanager = new HttpManager();
        }
        return httpmanager;
    }

    private RequestQueue getRequestQueue() {
        //LogUtils.d("volley", "HttpManager getRequestQueue");
        if (mRequestQueue == null) {
            if (MDoingContext.getApplicationContext() != null) {
                mRequestQueue = Volley
                        .newRequestQueue(MDoingContext.getApplicationContext());
            }
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        //LogUtils.d("volley", "HttpManager getImageLoader");
        RequestQueue queue = getRequestQueue();
        if (mImageLoader == null && queue != null) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && DiskBasedLruBitmapCache.getSDFreeSize() >= 10) { // sd卡正常装载,剩余空间大于10M
                /**
                 * @author renjihai 针对于360安全卫士等其他安全软件进行垃圾清理时,破坏了存储目录的读写权限问题;<br>
                 * <br>
                 *         问题描述: 在adb中使用ls命令可看见该目录存在,但ls -al进不去, 文件的目录结构被破坏<br>
                 *         根据安全卫士部门同事描述,他们清理目录一般都是cache目录,而不清理files目录,所以将缓存目录
                 */

                File cacheFile = new File(getCachePath());
                if (!cacheFile.exists()) {
                    cacheFile.mkdirs();
                }
                mImageLoader = new ImageLoader(this.mRequestQueue, new DiskBasedLruBitmapCache(
                        new File(getCachePath())));
            } else {
                mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
            }
        }
        return this.mImageLoader;
    }

    public ImageLoader getImageLoaderWithoutSd() {
        RequestQueue queue = getRequestQueue();
        if (mImageLoader == null && queue != null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        RequestQueue queue = getRequestQueue();
        if (queue != null) {
            queue.add(req);
        }
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        RequestQueue queue = getRequestQueue();
        if (queue != null) {
            queue.add(req);
        }
    }

    /**
     * 本地缓存路径
     */
    public String getCachePath() {
        String cachePath = "";
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard != null) {
            cachePath = sdcard + "/Android/data/com.doing.team/files/volley";
        } else {
            if (MDoingContext.getApplicationContext() != null) {
                File file = MDoingContext.getApplicationContext().getFilesDir();
                if (file == null)
                    cachePath = "/data/data/com.doing.team/files/volley";
                else
                    cachePath = file.getAbsolutePath() + "/volley";
            } else {
                cachePath = "/data/data/com.doing.team/files/volley";
            }
        }

        return cachePath;
    }

    /**
     * 首页图片本地缓存路径
     */
    public String getFirstPagePath() {
        String cachePath = "";

        if (MDoingContext.getApplicationContext() != null) {
            File file = MDoingContext.getApplicationContext().getFilesDir();
            if (file == null)
                cachePath = "/data/data/com.qihoo.around/files/firstpage";
            else
                cachePath = file.getAbsolutePath() + "/firstpage";
        } else {
            cachePath = "/data/data/com.qihoo.around/files/firstpage";
        }


        return cachePath;
    }
    //首页加载的大图为唯一的一张图片，每次拉去配置后加载替换本地图片，下次启动APP时使用本地图片
    public ImageLoader getFirstPageImageLoader() {
        final String firstPageUrl = "first_page_image_url";
        RequestQueue queue = getRequestQueue();
        if (firstPageImageLoader == null && queue != null) {

            firstPageImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
                @Override
                public Bitmap getBitmap(String s) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MDoingContext.getApplicationContext());
                    String imageUrl = sp.getString(firstPageUrl, null);
                    if (TextUtils.isEmpty(imageUrl)){
                        return null;
                    }else{
                        try {
                            FileInputStream in = new FileInputStream(getFirstPagePath());
                            Bitmap bitmap =BitmapFactory.decodeStream(in);
                            in.close();
                            return  bitmap;
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }

                @Override
                public void putBitmap(String s, Bitmap bitmap) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MDoingContext.getApplicationContext());
                    sp.edit().putString(firstPageUrl, s).commit();
                    try {
                        FileOutputStream out = new FileOutputStream(getFirstPagePath());
                        bitmap.compress(Bitmap.CompressFormat.PNG, 99, out);
                        out.flush();
                        out.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        return this.firstPageImageLoader;
    }
}
