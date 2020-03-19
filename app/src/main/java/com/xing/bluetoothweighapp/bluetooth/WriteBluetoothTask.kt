package com.xing.bluetoothweighapp.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.AsyncTask
import android.util.Log
import java.lang.Exception


class WriteBluetoothTask : AsyncTask<String, Int, String> {
    private var callBack: WriteBluetoothCallBack? = null
    private var socket: BluetoothSocket? = null

    constructor(callBack: WriteBluetoothCallBack?, socket: BluetoothSocket?) : super() {
        this.callBack = callBack
        this.socket = socket
    }

    override fun doInBackground(vararg params: String?): String {
        val string = params[0]
        try {
            socket?.outputStream?.use {
                string?.apply {
                    it.write(this.toByteArray())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("error", "ON RESUME: Exception during write.", e)
            return "发送失败"
        }
        return "发送成功"
    }


    override fun onPreExecute() {
        callBack?.onStarted()
    }

    override fun onPostExecute(result: String?) {
        if ("发送成功".equals(result)) {
            callBack?.onFinished(true, result)
        } else {
            callBack?.onFinished(false, result)
        }
    }
}