package com.xing.bluetoothweighapp.bluetooth

import android.bluetooth.BluetoothDevice

interface PinBlueCallBack {


    fun onBondRequest()

    fun onBondFail(device: BluetoothDevice?)

    fun onBonding(device: BluetoothDevice?)

    fun onBondSuccess(device: BluetoothDevice?)

}
