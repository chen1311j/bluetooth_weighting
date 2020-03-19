package com.xing.bluetoothweighapp.bluetooth

interface WriteBluetoothCallBack {


    fun onStarted()

    fun onFinished(isReadSuccess:Boolean,msg:String?)
}