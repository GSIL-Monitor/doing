/**
 * @author zhoushengtao
 * @since 2013-10-11 上午10:07:39
 */

package com.doing.team._public.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.doing.team._public.context.MDoingContext;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

/**
 * 手机信息的util，更新控制
 */
public class DeviceUtils {
    private static final String DEFAULT_IMEI = "DOING_DEFAULT_IMEI";
    
    public static final String MIUI_PROP="ro.miui.ui.version.name";
    public static final String emotion_ui = "ro.build.version.emui";
    public static final String smartisan_ui = "ro.smartisan.version";
    private static String verifyId = "";
    private final static String VERIFYID_KEY = "verifyId";
    private final static String TOKEN_KEY = "b69ed2538337afa80dbb10e71814b152";

    /*-------------------------------version and update-----------------------------------------*/
    		
    public static int getVersionCode() {
        return MDoingContext.getVersionCode();
    }

    public static String getVersionName() {
        return MDoingContext.getVersionName();
    }

    public static String getVerifyId(Context context) {
        if (!TextUtils.isEmpty(verifyId))
            return verifyId;
        if (context == null)
            return "";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keepVerifyId = sharedPreferences.getString( VERIFYID_KEY, "");
        if (!TextUtils.isEmpty(keepVerifyId)) {
            verifyId = keepVerifyId;
            return verifyId;
        }
        String imei = getImei(context);
        String android_id = android.provider.Settings.System.getString(context.getContentResolver(), "android_id");
        String serialNumber = getSerialNumber();
        String deviceID = md5(imei + android_id + serialNumber);
        verifyId = deviceID;
        sharedPreferences.edit().putString(VERIFYID_KEY,verifyId ).apply();
        return deviceID;
    }

    public final static String getImei(Context context) {
        if (context != null) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            try {
                if (tm != null && !TextUtils.isEmpty(tm.getDeviceId())) {
                    return tm.getDeviceId();
                }
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
        return DEFAULT_IMEI;
    }

    private static String getSerialNumber() {
        String serialNumber = null;
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            if (clazz != null) {
                Method method_get = clazz.getMethod("get", String.class, String.class);
                if (method_get != null) {
                    serialNumber = (String) (method_get.invoke(clazz, "ro.serialno", ""));
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }

        if (serialNumber == null) {
            return "";
        }
        return serialNumber;
    }

    public static String md5(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getUrlToken(String value){
        String temp = value + "&sk=" + TOKEN_KEY;
        try {
            temp = URLDecoder.decode(temp, "utf-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "&sn=" + md5(temp);
    }

    public static String getMimeType(final String fileName) {
        String result = "application/octet-stream";
        int extPos = fileName.lastIndexOf(".");
        if (extPos != -1) {
            String ext = fileName.substring(extPos + 1);
            result = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }
        return result;
    }

    public static String getPhoneNumber(Context paramContext) {
        return ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE))
                .getLine1Number();
    }

    /**
     * 获取设备ID IMET + IMSI
     * 
     * @param context
     * @return
     */
    public static String getDeciceId(Context context) {
        if (context == null)
            return "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
//        return tm.getDeviceId() + tm.getSubscriberId(); 直接相加会出现例如 12345null 的现象
        String devId = null;
        try {
            devId = tm.getDeviceId();
        } catch (Exception e) {
            LogUtils.e(e);
            return "";
        }
        String subId = tm.getSubscriberId();
        if (TextUtils.isEmpty(devId))
            devId = "";
        if (TextUtils.isEmpty(subId))
            subId = "";
        subId = devId + subId;
        
        return TextUtils.isEmpty(subId) ? "unknown" : subId;
    }

    /**
     * 指纹
     * 
     * @demo Xiaomi/aries/aries:4.1.1/JRO03L/3.12.6:user/release-keys
     * @param context
     * @return
     */
    public static String getFingerPrint() {
        String finger = Build.FINGERPRINT;
        if (TextUtils.isEmpty(finger))
            finger = "";
        return finger.replaceAll(" ", "");
    }

    public static String getModel() {
        String model = Build.MODEL;
        if (TextUtils.isEmpty(model))
            model = "";
        return model.replaceAll(" ", "");
    }
    
    public static String getProduct() {
        String product = Build.PRODUCT;
        if (TextUtils.isEmpty(product))
            product = "";
        return product.replaceAll(" ", "");
    }

    public static String getManufacturer() {
        String manufac = Build.MANUFACTURER;
        if (TextUtils.isEmpty(manufac))
            manufac = "";
        return manufac.replaceAll(" ", "");
    }
    
    public static String getSDKVersion(){
    	 String uiver = Build.VERSION.RELEASE;
         if (TextUtils.isEmpty(uiver))
        	 uiver = "";
         return uiver.replaceAll(" ", "");
    }

    public static String getNetworkType(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        return tm.getNetworkType() + "";
    }
    
    /**
     * 针对非小米手机刷机MIUI后的判断方法
     * @param propName
     * @return
     */
    public static String getSystemProperty(String propName) {
		String value="";
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(
					new InputStreamReader(p.getInputStream()), 1024);
			value = input.readLine();
			input.close();
		} catch (IOException ex) {
			LogUtils.d("Unable to read sysprop " + propName, ex);
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					LogUtils.d("Exception while closing InputStream", e);
				}
			}
		}
		return value;
	}
    
    public static boolean isMeizuM9() {
        return getProduct().toLowerCase().contains("meizu_m9") && getModel().toLowerCase().contains("m9");
    }

    public static boolean isMeizuMX() {
        return getProduct().toLowerCase().contains("meizu_mx");
    }
    
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
            return 100;
        }
        return statusBarHeight;//BitmapUtils.dip2px(context, statusBarHeight);
    }
    
    /**
     * 获取手机IP地址
     * @return
     */
    public static String getLocalIpAddress() {  
        try {  
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {  
                NetworkInterface intf = en.nextElement();  
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                    InetAddress inetAddress = enumIpAddr.nextElement();  
                    if (!inetAddress.isLoopbackAddress()) {  
                        return inetAddress.getHostAddress().toString();  
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtils.e(ex);
        }
        return null;  
    } 

    public static boolean checkApp(Context context,String packageName) {  
        if (packageName == null || "".equals(packageName))  
            return false;  
        try {  
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(  
                    packageName, PackageManager.GET_UNINSTALLED_PACKAGES);  
            return true;
        } catch (NameNotFoundException e) {  
            return false;  
        }
    }
    
    public static boolean checkVersion(Context context){
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        for(int i=0;i<packages.size();i++) { 
             PackageInfo packageInfo = packages.get(i); 

             if(packageInfo.packageName.equals("cn.qihoo.msearch")){
                 if(packageInfo.versionName.startsWith("1.8.1") || packageInfo.versionName.startsWith("1.8.2"))
                     return false;
             }
//             else if(packageInfo.packageName.equals("com.qihoo.haosou")){
//                 if(packageInfo.versionName.startsWith("2.0.0") || packageInfo.versionName.startsWith("2.0.1") || packageInfo.versionName.startsWith("2.0.2")
//                         || packageInfo.versionName.startsWith("2.0.3") || packageInfo.versionName.startsWith("2.0.4"))
//                     return false;
//             }
         }
        return true;
    }
    
    public static boolean hasInstallSearchApp(Context context){
        return checkApp(context,"com.qihoo.around");
    }
    public static boolean hasInstallBrowserApp(Context context){
        return checkApp(context,"com.qihoo.browser");
    }
    
    public static boolean canMozi(){
        int version = Build.VERSION.SDK_INT;
        return version >= 14;
    }
    
    public static boolean canRunning(){
        int version = Build.VERSION.SDK_INT;
        return version >= 9;
    }
    
    public static int MAKE_VER(String ver){
        String[] vs = ver.split("\\.");
        int v = 0;
        v += Integer.valueOf(vs[0])*10000;
        v += Integer.valueOf(vs[1])*100;
        v += Integer.valueOf(vs[2]);
        return v;
    }
    
    public static long getInternalFreeSize(Context context) {
        // 取SD卡文件路径
        File path = new File(context.getFilesDir(),"plugin");
        if(!path.exists()){
        	path.mkdirs();
        }
        StatFs sf = new StatFs(path.getPath());
        // 获取单数据块(Byte)
        long blockSize = sf.getBlockSize(); // 空闲数据块数量
        long freeBlocks = sf.getAvailableBlocks();
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }
}
