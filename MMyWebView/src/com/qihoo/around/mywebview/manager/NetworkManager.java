package com.qihoo.around.mywebview.manager;

import java.util.ArrayList;

import android.net.NetworkInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

public class NetworkManager {
    private static volatile NetworkManager sInstance;
    // using event bus is more flexible
    private ArrayList<_INetworkChange> listeners;
    private final static int TYPE_DEFAULT = -2;
    public final static int TYPE_MOBILE = 0;
    public final static int TYPE_WIFI = 1;
    //such as bluetooth
    public final static int TYPE_OTHERS = 2;
    public final static int TYPE_NONE = -1;
    //ConnectivityManager constants
    private int type = TYPE_DEFAULT;
    // TelephonyManager constants
    private int subType = TelephonyManager.NETWORK_TYPE_UNKNOWN;

    // 字符串型网络状态
    /**
     * 手机网络,没有区分2/3G
     */
    public static final String NETWORK_MOBILE = "NETWORK_MOBILE";
    /**
     * wifi网络
     */
    public static final String NETWORK_WIFI = "NETWORK_WIFI";
    /**
     * 网络无连接
     */
    public static final String NETWORK_DISABLE = "NETWORK_DISABLE";
    /**
     * 未知网络
     */
    public static final String NETWORK_UNKNOWN = "NETWORK_UNKNOWN";
    private Context mContext;

    public void addNetworkChangeListener(_INetworkChange listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeNightModeListener(_INetworkChange listener) {
        listeners.remove(listener);
    }

    private void notifyNetworkChanged(int type) {
        if (listeners == null) {
            return;
        }

        for (int i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).onNetworkChanged(type);
            } catch (Exception e) {
                listeners.remove(i);
            }
        }
    }

    private NetworkManager(Context context) {
        mContext = context;
        listeners = new ArrayList<_INetworkChange>();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mNetworkStateReceiver, filter);
    }

    public static final NetworkManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (NetworkManager.class) {
                if (sInstance == null) {
                    sInstance = new NetworkManager(context);
                }
            }
        }

        return sInstance;
    }

    /**
     * @param type ConnectivityManager.TYPE_MOBILE or
     *             ConnectivityManager.TYPE_WIFI or ConnectivityManager.TYPE_NONE
     */
    private void onNetworkChanged(int type) {
        switch (type) {
            case TYPE_MOBILE:
                notifyNetworkChanged(TYPE_MOBILE);
                break;
            case TYPE_WIFI:
                notifyNetworkChanged(TYPE_WIFI);
                break;
            case TYPE_NONE:
                notifyNetworkChanged(TYPE_NONE);
                break;
            default:
                notifyNetworkChanged(TYPE_OTHERS);
        }
    }

    private BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                type = -1;
                subType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
                onNetworkChanged(TYPE_NONE);
            }else {
                type = activeNetworkInfo.getType();
                subType = activeNetworkInfo.getSubtype();
                switch (type) {
                    case ConnectivityManager.TYPE_WIFI:
                        subType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
                        onNetworkChanged(TYPE_WIFI);
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        subType = activeNetworkInfo.getSubtype();
                        onNetworkChanged(TYPE_MOBILE);
                        break;
                    default:
                        subType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
                        onNetworkChanged(TYPE_OTHERS);
                }
            }
        }
    };

    public boolean isMobileNetwork() {
        return type == ConnectivityManager.TYPE_MOBILE;
    }

    public boolean isMobileNetwork2G() {
        return subType == TelephonyManager.NETWORK_TYPE_CDMA
                        || subType == TelephonyManager.NETWORK_TYPE_EDGE
                        || subType == TelephonyManager.NETWORK_TYPE_GPRS ;
    }

    /**
     * 返回字符串型网络状态
     *
     * @return
     */
    public String getNetworkType() {
        if (isMobileNetwork()) {
            return NETWORK_MOBILE;
        } else if (isWifiNetwork()) {
            return NETWORK_WIFI;
        } else if (isNoNetwork()) {
            return NETWORK_DISABLE;
        }
        return NETWORK_UNKNOWN;
    }

    public boolean isWifiNetwork() {
        return type == ConnectivityManager.TYPE_WIFI;
    }

    public boolean isNoNetwork() {
        return type == -1;
    }

    public boolean HasNetWork() {
        return type != -1;
    }
    
    public void unRegisterReceiver(){
        if(null != mNetworkStateReceiver){
            mContext.unregisterReceiver(mNetworkStateReceiver);
            mNetworkStateReceiver = null;
            sInstance=null;
        }
    }
}
