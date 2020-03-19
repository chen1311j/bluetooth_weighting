package com.xing.bluetoothweighapp.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import com.xing.library.helper.Constants
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.IOException
import java.lang.Exception


class ReadBluetoothTask : Thread {
    private var uIHandler: Handler? = null
    private var socket: BluetoothSocket? = null
    private val TAG = ReadBluetoothTask::class.java.simpleName
    var isStart = true
    var isSend = ""

    constructor(uIHandler: Handler?, socket: BluetoothSocket?) : super() {
        this.uIHandler = uIHandler
        this.socket = socket
    }

    override fun run() {
        val buffer = ByteArray(1024)  // 用于流的缓冲存储
        var bytes: Int?
        while (isStart) {
            try {
                bytes = socket?.inputStream?.read(buffer)
                if (bytes != null) {
                    uIHandler?.apply {
                        val value = String(buffer, 0, bytes)
                        if (value != isSend) {
                            val msg = obtainMessage()
                            msg.obj = value.replace("wn", "").replace("kg", "").trim()
                            msg.what = Constants.MSG_GOT_DATA
                            uIHandler?.sendMessage(msg)
                            isSend = value
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                uIHandler?.sendEmptyMessage(Constants.MSG_READ_ERROR)
            }
        }
    }


    fun writeCommand(msg: String) {
        try {
            socket?.outputStream?.write(msg.toByteArray(Charsets.UTF_8))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun close() {
        try {
            isStart = false
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}