package com.xing.library.net;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.collection.SimpleArrayMap;

/**
 * 作者：王鹏飞
 * 创建时间：2015/12/30 14:36
 * 邮箱：15291967179@163.com
 * 描述：
 */
public class NetStateReceiver extends BroadcastReceiver {

    private static boolean isNetAvailable = false;
    private static boolean isRegister = false;
    private static NetType mNetType;
    private final static String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private final static String CUSTOM_ANDROID_NET_CHANGE_ACTION = "com.nice.choice.library.net.conn.CONNECTIVITY_CHANGE";

    private static SimpleArrayMap<Activity, NetChangeObserver> mNetChangeObservers;

    private static BroadcastReceiver mBroadcastReceiver;

    private static BroadcastReceiver getReceiver() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new NetStateReceiver();
        }
        return mBroadcastReceiver;
    }

    public static boolean isIsRegister() {
        return isRegister;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        mBroadcastReceiver = NetStateReceiver.this;
        if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION) ||
                intent.getAction().equalsIgnoreCase(CUSTOM_ANDROID_NET_CHANGE_ACTION)) {
            if (!NetUtils.isNetworkAvailable(context)) {
                isNetAvailable = false;
            } else {
                isNetAvailable = true;
                mNetType = NetUtils.getAPNType(context);
            }
            notifyObserver((Activity) context);
        }
    }

    public static void registerNetworkStateReceiver(Activity activity) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CUSTOM_ANDROID_NET_CHANGE_ACTION);
        filter.addAction(ANDROID_NET_CHANGE_ACTION);
        activity.registerReceiver(getReceiver(), filter);
        isRegister = true;
    }

    public static void checkNetworkState(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(CUSTOM_ANDROID_NET_CHANGE_ACTION);
        activity.sendBroadcast(intent);
    }

    public static void unRegisterNetworkStateReceiver(Activity activity) {
        isRegister = false;
        if (mBroadcastReceiver != null) {
            try {
                activity.unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e) {
            }
        }

    }

    private void notifyObserver(Activity activity) {
        if (!mNetChangeObservers.isEmpty()) {
            NetChangeObserver observer = mNetChangeObservers.get(activity);
            if (observer != null) {
                if (isNetworkAvailable()) {
                    observer.onNetConnected(mNetType);
                } else {
                    observer.onNetDisConnect();
                }
            }
        }
    }

    public boolean isNetworkAvailable() {
        return isNetAvailable;
    }

    public static void registerObserver(Activity activity, NetChangeObserver observer) {
        if (mNetChangeObservers == null) {
            mNetChangeObservers = new SimpleArrayMap<>();
        }
        mNetChangeObservers.put(activity, observer);
    }

    public static void removeRegisterObserver(Activity activity, NetChangeObserver observer) {
        if (mNetChangeObservers != null) {
            if (mNetChangeObservers.containsValue(observer)) {
                mNetChangeObservers.remove(activity);
            }
        }
    }


}
