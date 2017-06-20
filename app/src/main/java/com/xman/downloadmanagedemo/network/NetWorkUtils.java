package com.xman.downloadmanagedemo.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @version V1.0
 * @类名称: NetWorkUtils
 * @类描述: 网络油条
 * @创建人：NieYunLong
 * @创建时间：2016-11-1
 * @备注：
 */
public class NetWorkUtils {
    public enum NetWorkStatus {
        NETWORK_WIFI, NETWORK_TWO_G, NETWORK_THREE_G, NETWORK_FOUR_G, NETWORK_ERROR
    }

    public static boolean isNetWorkConnect(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (!networkInfo.isAvailable()) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 得到网络状态
     *
     * @return
     */
    public static NetWorkStatus getNetWorkStatus(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager
                .getActiveNetworkInfo();

        if (netInfo != null && netInfo.isAvailable()) {
            //网络连接
            String name = netInfo.getTypeName();
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                //WiFi网络
                return NetWorkStatus.NETWORK_WIFI;
            } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = netInfo.getSubtypeName();
                // TD-SCDMA   networkType is 17
                int networkType = netInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        return NetWorkStatus.NETWORK_TWO_G;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        return NetWorkStatus.NETWORK_THREE_G;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        return NetWorkStatus.NETWORK_FOUR_G;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            return NetWorkStatus.NETWORK_THREE_G;
                        } else {
                            return NetWorkStatus.NETWORK_TWO_G; //这个其实是不知道网络类型 （暂时定义为2g ）
                        }

                }
            }
        } else {
            //网络断开
            return NetWorkStatus.NETWORK_ERROR;
        }
        return NetWorkStatus.NETWORK_ERROR;
    }

}
