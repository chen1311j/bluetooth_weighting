package com.xing.bluetoothweighapp.gunutils.strategy;

import android.content.Context;

import com.xing.bluetoothweighapp.gunutils.listener.OnResultListener;


public abstract class BaseGun implements IGun {
    protected OnResultListener onResultListener;
    public BaseGun(Context mContext) {
        this.mContext = mContext;
    }

    protected final Context mContext;

    @Override
    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    @Override
    public OnResultListener getResultListener() {
        return onResultListener;
    }
}
