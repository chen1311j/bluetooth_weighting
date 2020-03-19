package com.xing.bluetoothweighapp.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket

interface ConnectBlueCallBack {

    fun onStartConnect()

    fun onConnectSuccess(bluetoothDevice: BluetoothDevice?, bluetoothSocket: BluetoothSocket?)

    fun onConnectFail(bluetoothDevice: BluetoothDevice?, msg: String)


}