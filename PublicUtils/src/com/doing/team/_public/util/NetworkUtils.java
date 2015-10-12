package com.doing.team._public.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;
import android.util.SparseArray;

import com.qihoo.haosou.msearchpublic.util.LogUtils;

public class NetworkUtils {

    private static ConnectivityManager connManager = null;

    private static ConnectivityManager getConnectivityManager(Context context) {
        if (connManager == null) {
            connManager = (ConnectivityManager) context.getApplicationContext().getSystemService(
                    Context.CONNECTIVITY_SERVICE);
        }
        return connManager;
    }

    public static NetworkInfo getAvailableNetworkInfo(Context context) {
        try {
            connManager = getConnectivityManager(context);
            NetworkInfo allInfos = connManager.getActiveNetworkInfo();
            if (allInfos != null) {
                return allInfos;
            }
        	NetworkInfo mobNetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobNetInfo != null) {
                return mobNetInfo;
            }
        }catch (Exception ex){
            LogUtils.e(ex);
        }
        return null;
    }

    public static boolean isNetworkOpen(Context context) {
        NetworkInfo available = getAvailableNetworkInfo(context);
        if (available != null) {
            return true;
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo available = getAvailableNetworkInfo(context);
        if (available != null && available.isConnected()) {
            return true;
        }
        return false;
    }
    
    public static boolean isNetworkAvailable(Context context) { 
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        if (connectivity == null) { 
            LogUtils.i("NetWorkState", "Unavailabel"); 
            return false; 
        } else { 
            NetworkInfo[] info = connectivity.getAllNetworkInfo(); 
            if (info != null) { 
                for (int i = 0; i < info.length; i++) { 
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) { 
                        LogUtils.i("NetWorkState", "Availabel"); 
                        return true; 
                    } 
                } 
            } 
        } 
            return false; 
    } 


    public static boolean isNetworkInfoCMWAP(Context context) {
        NetworkInfo networkInfo = getAvailableNetworkInfo(context);
        if (networkInfo != null
                // && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                && android.net.Proxy.getDefaultHost() != null
                && android.net.Proxy.getDefaultPort() > 0) {
            return true;
        }
        return false;
    }

    public static String currentNetType(Context context) {
        NetworkInfo networkInfo = getAvailableNetworkInfo(context);
        if (networkInfo == null) {
            return "unknown network type";
        }

        return networkInfo.getTypeName();
    }

    public static boolean isNetworkInfoCMWap2(Context context) {
        NetworkInfo networkInfo = getAvailableNetworkInfo(context);
        String currentAPN = "";
        if (networkInfo != null) {
            currentAPN = networkInfo.getExtraInfo();
        }
        if (currentAPN != null && currentAPN != "" && currentAPN.equals("cmwap")) {
            return true;
        }

        return false;
    }

    public static boolean isNetworkInWiFI(Context context) {
        NetworkInfo networkInfo = getAvailableNetworkInfo(context);
        if (networkInfo != null
        // && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
            // }else
            // if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) {
            // return false;
        } else {
            return false;
        }
    }

    public static int getConnectTimeout(Context context) {
        if (isNetworkInWiFI(context)) {
            return 60000;
        }
        return 180000;
    }

    public static int getReadTimeout(Context context) {
        if (isNetworkInWiFI(context)) {
            return 10000;
        }
        return 60000;
    }

    private static SparseArray<String> networkMap;
    public static String getNetWorkType(Context context) {
        // 先判断是否WIFI连接
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        State state=null;
        try {
        	 state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		} catch (Exception e) {
			// TODO: handle exception
		}
        if(state==null){
        	return "";
        }
        if (state == State.CONNECTED) {
            return "WIFI";
        }
        // 再判断当前移动网络
        if(networkMap==null){
            networkMap = new SparseArray<String>();
            networkMap.put(TelephonyManager.NETWORK_TYPE_UNKNOWN, "UNKNOWN");
            networkMap.put(TelephonyManager.NETWORK_TYPE_GPRS, "GPRS");
            networkMap.put(TelephonyManager.NETWORK_TYPE_EDGE, "EDGE");
            networkMap.put(TelephonyManager.NETWORK_TYPE_UMTS, "UMTS");
            networkMap.put(TelephonyManager.NETWORK_TYPE_CDMA, "CDMA");
            networkMap.put(TelephonyManager.NETWORK_TYPE_EVDO_0, "EVDO_0");
            networkMap.put(TelephonyManager.NETWORK_TYPE_EVDO_A, "EVDO_A");
            networkMap.put(TelephonyManager.NETWORK_TYPE_1xRTT, "1xRTT");
            networkMap.put(TelephonyManager.NETWORK_TYPE_HSDPA, "HSDPA");
            networkMap.put(TelephonyManager.NETWORK_TYPE_HSUPA, "HSUPA");
            networkMap.put(TelephonyManager.NETWORK_TYPE_HSPA, "HSPA");
            networkMap.put(TelephonyManager.NETWORK_TYPE_IDEN, "IDEN");
            networkMap.put(TelephonyManager.NETWORK_TYPE_EVDO_B, "EVDO_B");
            networkMap.put(TelephonyManager.NETWORK_TYPE_LTE, "LTE");
            networkMap.put(TelephonyManager.NETWORK_TYPE_EHRPD, "EHRPD");
            networkMap.put(TelephonyManager.NETWORK_TYPE_HSPAP, "HSPAP");
//            public static final int NETWORK_TYPE_TD_SCDMA = 17;
            networkMap.put(17,"TD_SCDMA");

        }
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String type = networkMap.get(telephonyManager.getNetworkType());
        if (type == null) {
            type = "Network type :" + telephonyManager.getNetworkType();
        }
        return type;
    }

}
