package com.xing.bluetoothweighapp.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import java.lang.Exception

class BlueToothController {

    private var defaultAdapter: BluetoothAdapter? = null
    private val TAG = BlueToothController::class.java.name

    init {
        defaultAdapter = BluetoothAdapter.getDefaultAdapter()
    }


    fun getBlueAdapter(): BluetoothAdapter? {
        return  defaultAdapter
    }

    /**
     * 判断设备是否支持蓝牙
     */
    fun isSupportBlue(): Boolean {
        return defaultAdapter != null
    }

    /**
     * 自动打开蓝牙（同步）
     * 这个方法打开蓝牙会弹出提示
     * 需要在onActivityResult 方法中判断resultCode == RESULT_OK  true为成功
     */
    fun openBlueSync(activity: Activity, requestCode: Int) {
        activity.startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), requestCode)
    }

    /**
     * 判断当前设备是否开启蓝牙
     */
    fun isBlueEnabled(): Boolean? {
        return defaultAdapter?.isEnabled
    }

    /**
     * 注册扫描蓝牙广播
     */
    fun registerBlueToothReceiver(context: Context, broadcastReceiver: BroadcastReceiver) {
        val filter = IntentFilter()
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        context.registerReceiver(broadcastReceiver, filter)
    }

    /**
     * 获取已绑定设备
     */
    fun getBondedDeviceList(): Set<BluetoothDevice>? {
        return defaultAdapter?.bondedDevices
    }

    /**
     * 取消扫描蓝牙
     * @return  true 为取消成功
     */
    fun cancelScanBlue(): Boolean {
        if (isSupportBlue()) {
            return defaultAdapter?.cancelDiscovery() == true
        }
        return true
    }

    /**
     * 配对（配对成功与失败通过广播返回）
     * @param device
     */
    fun pin(device: BluetoothDevice?) {
        if (device == null) {
            return
        }
        if (isBlueEnabled() != true) {
            return
        }
        //配对之前把扫描关闭
        if (defaultAdapter?.isDiscovering == true) {
            cancelScanBlue()
        }
        //判断设备是否配对，没有配对在配，配对了就不需要配
        if (device.bondState == BluetoothDevice.BOND_NONE) {
            Log.d(TAG, "attemp to bond:" + device.name)
            try {
                //device.createBond()
                val createBondMethod = device::class.java.getMethod("createBond")
                createBondMethod.invoke(device) as Boolean
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "attemp to bond fail!");
            }
        }
    }

    /**
     * 取消配对（取消配对成功与失败通过广播返回 也就是配对失败）
     * @param device
     */
    fun cancelPinBule(device: BluetoothDevice?) {
        if (device == null) {
            return
        }

        if (isBlueEnabled() != true) {
            return
        }
        //配对之前把扫描关闭
        if (defaultAdapter?.isDiscovering == true) {
            cancelScanBlue()
        }
        //判断设备是否配对，没有配对就不用取消了
        if (device.bondState == BluetoothDevice.BOND_NONE) {
            Log.d(TAG, "attemp to bond:" + device.name)
            try {
                val removeBondMethod = device::class.java.getMethod("removeBond")
                removeBondMethod.invoke(device) as Boolean
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "attemp to bond fail!");
            }
        }
    }

}