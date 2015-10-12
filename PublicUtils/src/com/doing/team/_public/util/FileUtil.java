/**
 * @author zhoushengtao
 * @since 2014-1-8 上午10:19:01
 */

package com.doing.team._public.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.qihoo.aroundpublic.R;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

public class FileUtil {

    // {后缀名， MIME类型}
    private static final String[][] MIME_MapTable = {

    { ".3gp", "video/3gpp" }, { ".apk", "application/vnd.android.package-archive" },
            { ".asf", "video/x-ms-asf" }, { ".avi", "video/x-msvideo" },
            { ".bin", "application/octet-stream" }, { ".bmp", "image/bmp" },
            { ".c", "text/plain" }, { ".class", "application/octet-stream" },
            { ".conf", "text/plain" }, { ".cpp", "text/plain" }, { ".doc", "application/msword" },
            { ".exe", "application/octet-stream" }, { ".gif", "image/gif" },
            { ".gtar", "application/x-gtar" }, { ".gz", "application/x-gzip" },
            { ".h", "text/plain" }, { ".java", "text/plain" }, { ".jpeg", "image/jpeg" },
            { ".jpg", "image/jpeg" }, { ".js", "application/x-javascript" },
            { ".log", "text/plain" }, { ".m3u", "audio/x-mpegurl" }, { ".m4a", "audio/mp4a-latm" },
            { ".m4b", "audio/mp4a-latm" }, { ".m4p", "audio/mp4a-latm" },
            { ".m4u", "video/vnd.mpegurl" }, { ".m4v", "video/x-m4v" },
            { ".mov", "video/quicktime" }, { ".mp2", "audio/x-mpeg" }, { ".mp3", "audio/x-mpeg" },
            { ".mp4", "video/mp4" }, { ".mpc", "application/vnd.mpohun.certificate" },
            { ".mpe", "video/mpeg" }, { ".mpeg", "video/mpeg" }, { ".mpg", "video/mpeg" },
            { ".mpg4", "video/mp4" }, { ".mpga", "audio/mpeg" },
            { ".msg", "application/vnd.ms-outlook" }, { ".ogg", "audio/ogg" },
            { ".pdf", "application/pdf" }, { ".png", "image/png" },
            { ".pps", "application/vnd.ms-powerpoint" },
            { ".ppt", "application/vnd.ms-powerpoint" }, { ".prop", "text/plain" },
            { ".rar", "application/x-rar-compressed" }, { ".rc", "text/plain" },
            { ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
            { ".sh", "text/plain" }, { ".tar", "application/x-tar" },
            { ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
            { ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" }, { ".wmv", "audio/x-ms-wmv" },
            { ".wps", "application/vnd.ms-works" },
            // {".xml", "text/xml"},
            { ".xml", "text/plain" }, { ".z", "application/x-compress" },
            { ".zip", "application/zip" }, { "", "*/*" } };

    /**
     * 是否是已知的文件类型
     * 
     * @param path
     *            文件路劲
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static boolean isKnownFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        // 获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = path.lastIndexOf(".");

        if (dotIndex < 0) {
            return false;
        }

        /* 获取文件的后缀名 */
        String end = path.substring(dotIndex, path.length()).toLowerCase();
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                return true;
        }
        return false;
    }

    public static void insertImage(Context context, String filePath) {
        try {
            Uri localUri = Uri.fromFile(new File(filePath));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            context.sendBroadcast(localIntent);
        } catch (Exception ex) {
            LogUtils.e(ex);
        }
    }

    public static void deleteImage(Context context, String filePath) {
        try {
            String where = MediaStore.Images.Media.DATA + "='" + filePath + "'";
            context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    where, null);
        } catch (Exception ex) {
            LogUtils.e(ex);
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     * 
     * @param file
     */
    @SuppressLint("DefaultLocale")
    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        // 获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");

        if (dotIndex < 0) {
            return type;
        }

        /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "")
            return type;
        // 在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++)
            if (end.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
            }
        return type;
    }

    /**
     * 判断某个文件是否为图片
     * 
     * @param filename
     * @return
     */
    public static boolean isImageSuffix(String filename) {
        if (filename == null) {
            return false;
        }
        filename = filename.toLowerCase();
        return filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".bmp")
                || filename.endsWith(".gif") || filename.endsWith(".png")
                || filename.endsWith(".tiff") || filename.endsWith(".webp");
    }

    /**
     * 使用系统程序打开相应的文件
     * 
     * @param context
     * @param path
     */
    public static void openFile(Context context, String path) {
        try {
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.no_function_can_open_this_file, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @SuppressLint("InlinedApi")
    public static void openFileEx(Context context, String path) {
        try {
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.no_function_can_open_this_file, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public static boolean isExist(String filePath) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    public static boolean isExistInFilesPath(Context context, String filename) {
        if (context == null)
            return false;
        File file = context.getFileStreamPath(filename);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public static String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getHashString(MessageDigest digest) {

        StringBuilder builder = new StringBuilder();

        for (byte b : digest.digest()) {

            builder.append(Integer.toHexString((b >> 4) & 0xf));

            builder.append(Integer.toHexString(b & 0xf));

        }

        return builder.toString();

    }

    /**
     * @param fileName
     * @return
     */
    public static String getFileData(Context context, String fileName) {

        FileInputStream inStream = null;
        try {
            inStream = context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            LogUtils.e("getFileData:" + e);
            return null;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();// 输出到内存
        int len = 0;
        byte[] buffer = new byte[1024];
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);//
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        byte[] content_byte = outStream.toByteArray();
        String content = new String(content_byte);
        return content;
    }

    /**
     * 在data/data、files下读写文件
     * 
     * @param fileName
     * @param message
     */
    public static void writeFileData(Context context, String fileName, String message) {

        try {

            FileOutputStream fout = context.openFileOutput(fileName, Context.MODE_MULTI_PROCESS);

            byte[] bytes = message.getBytes();

            fout.write(bytes);

            fout.close();

        }

        catch (Exception e) {

            e.printStackTrace();

        }

    }

    /**
     * 读data/data、files下读写文件
     * 
     * @param fileName
     * @return
     */

    public static String readFileData(Context context, String fileName) {

        String res = "";

        try {

            FileInputStream fin = context.openFileInput(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];

            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();

        }

        catch (Exception e) {

            e.printStackTrace();

        }

        return res;

    }

    /**
     * 格式化APK大小
     * 
     * @param bytes
     * @return
     */
    public static String getReadableSize(long bytes) {
        NumberFormat formater = NumberFormat.getInstance();
        formater.setMaximumFractionDigits(2);
        if (bytes == 0) {
            return "0M";
        } else if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < 1048576) {
            return formater.format(bytes / 1024f) + "K";
        } else if (bytes < 1048576 * 1024) {
            return formater.format(bytes / 1024f / 1024f) + "M";
        } else {
            return formater.format(bytes / 1024f / 1024f / 1024f) + "G";
        }
    }
}
