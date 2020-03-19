package com.xing.bluetoothweighapp.gunutils.strategy;


import com.xing.bluetoothweighapp.gunutils.CompileType;
import com.xing.bluetoothweighapp.gunutils.listener.OnResultListener;

import java.util.List;

public interface IGun {
    void register();

    void unRegister();


    void setOnResultListener(OnResultListener l);


    OnResultListener getResultListener();

    CompileType getType();


    List<String> getFilterList();


}
