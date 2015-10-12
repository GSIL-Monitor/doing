package com.doing.team._public.cache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.os.StatFs;
import android.support.v4.util.LruCache;



import com.android.volley.Cache.Entry;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader.ImageCache;

public class DiskBasedLruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {

    DiskBasedCache diskBasedCache = null;
    HandlerThread thread = null;
    Handler handler = null;

    public static final int WHAT_DISK_CACHE_INIT = 0x01;
    public static final int WHAT_DISK_CACHE_PUT = 0x02;

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        return cacheSize;
    }

    public DiskBasedLruBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    public DiskBasedLruBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    @SuppressWarnings("deprecation")
    public static long getSDFreeSize() {
        // 取SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单数据块(Byte)
        long blockSize = sf.getBlockSize(); // 空闲数据块数量
        long freeBlocks = sf.getAvailableBlocks();
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public DiskBasedLruBitmapCache(File rootFile) {
        this(getDefaultLruCacheSize());
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        diskBasedCache = new DiskBasedCache(rootFile);
        thread = new HandlerThread("disk_cache_thread");
        thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        handler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case WHAT_DISK_CACHE_INIT:
                    if (diskBasedCache == null) {
                        return;
                    }
                    diskBasedCache.initialize();
                    break;
                case WHAT_DISK_CACHE_PUT:
                    if (diskBasedCache == null || msg.obj == null
                            || !(msg.obj instanceof DiskCacheTask)) {
                        return;
                    }
                    DiskCacheTask task = (DiskCacheTask) msg.obj;
                    if (task.url == null || task.bitmap == null || task.bitmap.isRecycled()) {
                        return;
                    }
                    Entry entry = new Entry();
                    entry.data = convertBitmapToBytes((Bitmap) task.bitmap);
                    diskBasedCache.put(task.url, entry);
                    break;
                default:
                    break;
                }
            }
        };
        handler.sendEmptyMessage(WHAT_DISK_CACHE_INIT);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = get(url);
        if (bitmap != null) {
            return bitmap;
        }
        Entry entry = null;
        if (diskBasedCache != null && (entry = diskBasedCache.get(url)) != null
                && entry.data != null) {
            return BitmapFactory.decodeByteArray(entry.data, 0, entry.data.length);
        }
        return null;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
        if (handler != null && diskBasedCache != null && bitmap != null
                && !diskBasedCache.getFileForKey(url).exists()) {
            DiskCacheTask task = new DiskCacheTask();
            task.url = url;
            task.bitmap = bitmap;
            Message msg = Message.obtain();
            msg.what = WHAT_DISK_CACHE_PUT;
            msg.obj = task;
            handler.sendMessage(msg);
        }
    }

    public static byte[] convertBitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

    private class DiskCacheTask implements Serializable {
        private static final long serialVersionUID = -6846705728537106548L;
        public String url;
        public Bitmap bitmap;
    }

}
