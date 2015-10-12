/**
 * @author wushuai
 * @since 2014-11-27 上午10:20:09
 * 
 */
package com.doing.team._public.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;

public class StorageUtil {

	/**
     * 
     * @return ROM存储路径
     */
    public static String getInternalMemoryPath() {
            return Environment.getDataDirectory().getPath();
    }

    public static String getImageDataMemoryPath(Context context){
    	return context.getFilesDir().getPath() + "/screencapture.data";
    }
    public static String getXMLDataMemoryPath(Context context){
    	return context.getFilesDir().getPath() + "/captureXML.xml";
    }
    /**
     * 
     * @return 内置sd卡路径
     */
    public static String getExternalMemoryPath() {
            // return Environment.getExternalStorageDirectory().getPath();
            return "/mnt/sdcard";
    }

    /**
     * 
     * @return 外置sd卡路径
     */
    public static String getSDCard2MemoryPath() {
    	String path = "/mnt/sdcard1";
    	StatFs stat = getStatFs(path);
        if(null == stat){
        	path = "/storage/sdcard1";
        	stat = getStatFs(path);
        }
        return path;
    }

    /**
     * 
     * @param path
     *            文件路径
     * @return 文件路径的StatFs对象
     * @throws Exception
     *             路径为空或非法异常抛出
     */
    public static StatFs getStatFs(String path) {
        try {
        	return new StatFs(path);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }

    /**
     * 
     * @param stat
     *            文件StatFs对象
     * @return 剩余存储空间的MB数
     * 
     */
    public static float calculateSizeInMB(StatFs stat) {
        if (stat != null)
            return stat.getAvailableBlocks()
                            * (stat.getBlockSize() / (1024f * 1024f));
        return 0.0f;
    }

    /**
     * 
     * @return ROM剩余存储空间的MB数
     */
    public static float getAvailableInternalMemorySize() {
        String path = getInternalMemoryPath();// 获取数据目录
        StatFs stat = getStatFs(path);
        return calculateSizeInMB(stat);
    }

    /**
     * 
     * @return 内置SDCard剩余存储空间MB数
     */
    public static float getAvailableExternalMemorySize() {
        String path = getExternalMemoryPath();// 获取数据目录
        StatFs stat = getStatFs(path);
        return calculateSizeInMB(stat);
    }

    /**
     * 
     * @return 外置SDCard剩余存储空间MB数
     */
    public static float getAvailableSDCard2MemorySize() {
        String path = getSDCard2MemoryPath(); // 获取数据目录
        StatFs stat = getStatFs(path);
        return calculateSizeInMB(stat);
    }
    //sdcard是否可读写 
    public static boolean IsCanUseSdCard() { 
        try {
        	String path = getExternalMemoryPath() + "/test.file";
        	File file = new File(path);
        	if(file.exists())
        		file.delete();
        	file.createNewFile();
        	boolean isSuccess = file.exists();
        	if(file.exists())
        		file.delete();
        	return isSuccess;
        } catch (Exception e) { 
            return false;
        }
    }
    public static boolean IsCanUseStorage() { 
        try {
        	String path = getInternalMemoryPath() + "/test.file";
        	File file = new File(path);
        	if(file.exists())
        		file.delete();
        	file.createNewFile();
        	boolean isSuccess = file.exists();
        	if(file.exists())
        		file.delete();
        	return isSuccess;
        } catch (Exception e) { 
            return false;
        }
    }
}
