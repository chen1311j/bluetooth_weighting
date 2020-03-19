package com.xing.bluetoothweighapp.bluetooth

import android.bluetooth.BluetoothDevice

interface ScanBlueCallBack {

    fun onScanStarted()

    fun onScanFinished()

    fun onScanning(device: BluetoothDevice?)
}