package com.xing.bluetoothweighapp.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.util.Log
import com.xing.library.helper.Constants
import java.util.*
import kotlin.concurrent.thread


class ConnectBlueTask : AsyncTask<BluetoothDevice, Int, BluetoothSocket> {
    private var readBluetoothTask: ReadBluetoothTask? = null
    val TAG = this::class.java.simpleName
    private var bluetoothDevice: BluetoothDevice? = null
    var uiHandler: Handler? = null
    var start = true

    constructor(uiHandler: Handler?) : super() {
        this.uiHandler = uiHandler
    }


    override fun doInBackground(vararg params: BluetoothDevice?): BluetoothSocket? {
        bluetoothDevice = params[0]
        var bluetoothSocket: BluetoothSocket? = null

        try {
            val parcelUuid = bluetoothDevice?.uuids?.get(0)
            bluetoothSocket = parcelUuid?.uuid?.let {
                bluetoothDevice?.createRfcommSocketToServiceRecord(it)
            }
            if (bluetoothSocket != null && !bluetoothSocket.isConnected) {
                bluetoothSocket.connect()
            }
        } catch (e: Exception) {
            Log.e(TAG, "socket连接失败")
            uiHandler?.sendEmptyMessage(Constants.MSG_CONNECT_ERROR)
            try {
                bluetoothSocket?.close()
            } catch (e1: Exception) {
                e1.printStackTrace()
                Log.e(TAG, "socket关闭失败")
            }
        }
        return bluetoothSocket
    }

    override fun onPreExecute() {
        Log.d(TAG, "开始连接")

    }

    override fun onPostExecute(bluetoothSocket: BluetoothSocket?) {
        start = false
        if (bluetoothSocket != null && bluetoothSocket.isConnected) {
            if (readBluetoothTask == null) {
                readBluetoothTask = ReadBluetoothTask(uiHandler, bluetoothSocket)
                readBluetoothTask?.start()
                uiHandler?.sendEmptyMessage(Constants.MSG_CONNECT_SUCCESS)
            }
        } else {
            uiHandler?.sendEmptyMessage(Constants.MSG_CONNECT_ERROR)
        }
    }


    fun sendCommand(msg: String) {
        readBluetoothTask?.writeCommand(msg)
    }


    fun cancel() {
        if (readBluetoothTask != null) {
            readBluetoothTask?.close()
        }
        this.cancel(true)
    }

}