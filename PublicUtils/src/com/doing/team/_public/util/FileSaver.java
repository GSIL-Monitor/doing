package com.doing.team._public.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.qihoo.haosou.msearchpublic.util.LogUtils;

public class FileSaver {
    private Context context;

    public FileSaver(Context context) {
        this.context = context;

    }

    public boolean SaveJsonToFile(String filename, String strjson) {
        boolean bRet = false;
        try {
            FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outStream.write(strjson.getBytes());
            outStream.flush();
            outStream.close();
            bRet = true;
        } catch (FileNotFoundException e) {
            LogUtils.e(e);
        } catch (IOException e) {
            LogUtils.e(e);
        } catch (Exception e) {
			// TODO: handle exception
        	LogUtils.e(e);
		}
        return bRet;
    }

    public String LoadJsonFromFile(String filename) {
        String strJson = "";
        File file = context.getFileStreamPath(filename);
        if (file == null || !file.exists()) {
            return strJson;
        }
        try {
            FileInputStream inStream = context.openFileInput(filename);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
            stream.close();
            inStream.close();
            strJson = stream.toString();
        } catch (FileNotFoundException e) {
            LogUtils.e(e);
        } catch (IOException e) {
            return "";
        }
        return strJson;
    }

    public static String getMD5Code(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
            byte b[] = md.digest();

            int i;
            StringBuffer buf = new StringBuffer("");

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append('0');
                buf.append(Integer.toHexString(i));
            }

            return buf.toString();
            // return buf.toString().substring(8, 24);

        } catch (Exception e) {
            LogUtils.e(e);
        }
        return "";
    }

    /**
     * 从Assets文件夹里读文件
     * 注释掉按行读取的方法 因为效率非常低
     * @param fileName
     * @return String
     */
    public String loadStringFromAssets(String fileName) {
        if (context == null || TextUtils.isEmpty(fileName))
            return "";
        Resources res = context.getResources();
        if (res == null)
            return "";
        AssetManager assetManager = res.getAssets();

        if (assetManager != null) {
            try {
                String result = "";
                InputStream in = assetManager.open(fileName);
                int length = in.available();
                byte[] buffer = new byte[length];
                in.read(buffer);
                result = EncodingUtils.getString(buffer, "UTF-8");
                return result;
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }

        return "";

                /*if (assetManager != null) {
            InputStreamReader inputReader = null;
            BufferedReader bufReader = null;
            try {
                inputReader = new InputStreamReader(assetManager.open(fileName));
                bufReader = new BufferedReader(inputReader);
                String line = "";
                String result = "";
                while ((line = bufReader.readLine()) != null) {
                    result += line;
                }
                return result;
            } catch (Exception e) {
                return "";
            }finally {
                try {
                    if (inputReader != null) {
                        inputReader.close();
                    }
                    if (bufReader != null) {
                        bufReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

}
