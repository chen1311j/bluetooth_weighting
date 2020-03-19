package com.xing.bluetoothweighapp.bluetooth

interface ReadBluetoothCallBack {


    fun onStarted()

    fun onFinished(isReadSuccess:Boolean,msg:String?)
}