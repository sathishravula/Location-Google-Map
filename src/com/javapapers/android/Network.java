package com.javapapers.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 24/8/14
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Network {
  public static NetworkInfo getNetworkInfo(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    return connectivityManager.getActiveNetworkInfo();
  }

  public static boolean isConnected(Context context) {
    NetworkInfo networkInfo = Network.getNetworkInfo(context);
    return (networkInfo != null && networkInfo.isConnected());
  }

  public static boolean isConnectedWifi(Context context) {
    NetworkInfo networkInfo = Network.getNetworkInfo(context);
    return (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
  }

  public static boolean isConnectedMobile(Context context) {
    NetworkInfo info = Network.getNetworkInfo(context);
    return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
  }

  public static boolean isConnectedFast(Context context) {
    NetworkInfo info = Network.getNetworkInfo(context);
    return (info != null && info.isConnected() && Network.isConnectionFast(info.getType(), info.getSubtype()));
  }

  public static boolean isConnectionFast(int type, int subType) {
    if (type == ConnectivityManager.TYPE_WIFI) {
      return true;
    } else if (type == ConnectivityManager.TYPE_MOBILE) {
      switch (subType) {
        case TelephonyManager.NETWORK_TYPE_1xRTT:
          return false; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_CDMA:
          return false; // ~ 14-64 kbps
        case TelephonyManager.NETWORK_TYPE_EDGE:
          return false; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
          return true; // ~ 400-1000 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
          return true; // ~ 600-1400 kbps
        case TelephonyManager.NETWORK_TYPE_GPRS:
          return false; // ~ 100 kbps
        case TelephonyManager.NETWORK_TYPE_HSDPA:
          return true; // ~ 2-14 Mbps
        case TelephonyManager.NETWORK_TYPE_HSPA:
          return true; // ~ 700-1700 kbps
        case TelephonyManager.NETWORK_TYPE_HSUPA:
          return true; // ~ 1-23 Mbps
        case TelephonyManager.NETWORK_TYPE_UMTS:
          return true; // ~ 400-7000 kbps
//        case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
//          return true; // ~ 1-2 Mbps
//        case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
//          return true; // ~ 5 Mbps
//        case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
//          return true; // ~ 10-20 Mbps
//        case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
//          return false; // ~25 kbps
//        case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
//          return true; // ~ 10+ Mbps
        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
        default:
          return false;
      }
    } else {
      return false;
    }
  }
}
