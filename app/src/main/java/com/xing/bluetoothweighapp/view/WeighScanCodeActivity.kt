/**
 * generate by AAMVVM: https://github.com/HeadingMobile/AAMVVM
 */
package com.xing.bluetoothweighapp.view

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.xing.bluetoothweighapp.Application
import com.xing.bluetoothweighapp.bluetooth.BlueToothController
import com.xing.bluetoothweighapp.bluetooth.ConnectBlueTask
import com.xing.bluetoothweighapp.databinding.WeighScanCodeActivityBinding
import com.xing.bluetoothweighapp.gunutils.GunManager
import com.xing.bluetoothweighapp.view.viewmodel.WeighScanCodeViewModel
import com.xing.library.aop.annotation.SingleClick
import com.xing.library.helper.Constants
import com.xing.library.helper.extens.bindLifeCycle
import com.xing.library.helper.extens.toast
import com.xing.library.view.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.R.attr.name
import android.content.pm.PackageManager
import android.graphics.Paint
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.xing.bluetoothweighapp.DialogHintView
import com.xing.bluetoothweighapp.R
import com.xing.library.helper.extens.navigateToActivity
import com.xing.library.net.NetUtils
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.weigh_scan_code_activity.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.NumberFormatException
import java.lang.Process
import java.util.*


/**
 * description: WeighScancodeActivity
 * @see WeighScanCodeViewModel
 *
 * @date 2020/02/21
 */

class WeighScanCodeActivity : BaseActivity<WeighScanCodeActivityBinding>() {

    private var customerName: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isResetScanning: Boolean? = null
    private var connectBlueTask: ConnectBlueTask? = null
    private var blueToothController: BlueToothController? = null
    private var gunManager: GunManager? = null
    private var speechText: String? = null
    private var isDiBang = true//默认是黄色的【此单地磅件】，点了以后变为【此单外围件】


    private val mViewModel by viewModel<WeighScanCodeViewModel>()
    private val dialogHintView: DialogHintView by lazy {
        DialogHintView(this)
    }

    override fun getLayoutId(): Int = R.layout.weigh_scan_code_activity
    private val uiHandler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constants.MSG_READ_ERROR -> {
                    //toast("断开连接")
                }
                Constants.MSG_CONNECT_SUCCESS -> {
                    toast("蓝牙连接成功")
                    progress.visibility = View.GONE
                }
                Constants.MSG_CONNECT_ERROR -> {
                    toast("连接异常,正在重新连接")
                    if (blueToothController?.isBlueEnabled() == true) {
                        pairConnect()
                    }
                }
                Constants.MSG_GOT_DATA -> {
                    try {
                        val formatWeight = String.format("%.2f", msg.obj.toString().toFloat())
                        mViewModel.weight.set(formatWeight)
                        submit(formatWeight)
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                }
                Constants.MSG_CONNECTED_TO_SERVER -> {
                    blueToothController?.cancelScanBlue()
                    //toast("开始连接")
                }
            }
        }
    }

    override fun initView() {
        mBinding.vm = mViewModel
        blueToothController = Application.application.blueToothController
        gunManager = GunManager.getInstance()
        if (gunManager?.isFilterVersion == true) {
            gunManager?.filterGun?.register()
            gunManager?.filterGun?.setOnResultListener { result ->
                uiHandler.post {
                    mViewModel.orderCode.set(result)
                    connectBlueTask?.sendCommand("R")
                    if (mediaPlayer?.isPlaying == false && !NetUtils.isNetworkConnected(this)) {
                        mediaPlayer?.start()
                    }
                }
            }
        }
        getCount()
        refreshLatestView()
        mViewModel.checkData().bindLifeCycle(this).subscribe()
        mViewModel.checkSave().bindLifeCycle(this).subscribe {
            if (it == WeighScanCodeViewModel.SAVE_SUCCESS) {
                mViewModel.isSaveSuccess.set(WeighScanCodeViewModel.RESET)
                playSound(R.raw.success)
                toast("保存成功")
                getCount()
                refreshLatestView()
            } else if (it == WeighScanCodeViewModel.SAVE_FAILURE) {
                playSound(R.raw.failed)
                if (isResetScanning == true) {
                    toast("更新重量无效")
                } else {
                    toast("保存失败")
                }
            } else if (it == WeighScanCodeViewModel.REPEAT_FAILURE) {
                playSound(R.raw.failed)
                toast("重量无效")
            }

        }
        val bundle = intent.getParcelableExtra<Bundle>(Constants.KEY_PARCELABLE)
        bundle?.apply {
            customerName = bundle.getString(Constants.NAME)
            isResetScanning = bundle.getBoolean(Constants.IS_RESET_SCANNING, false)
            isResetScanning?.let {
                mViewModel.isResetScanning = it
            }
            mViewModel.customerName.set(customerName)
            mViewModel.orderType.set(if(isDiBang) "此单地磅单" else "此单外围单")
//            mViewModel.bgColor.set(if(isDiBang) R.mipmap.bg_yello else R.mipmap.bg_red)
            tv_title.setText(customerName)
            tv_confirm.setBackgroundResource(if(isDiBang) R.mipmap.bg_yello else R.mipmap.bg_red)
            tv_today_list.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        }
        blueToothController?.registerBlueToothReceiver(this, receiver)
        pairConnect()
        mediaPlayer = MediaPlayer.create(this, R.raw.network_none);
    }

    /**
     * 获取未上传数量
     */
    private fun getCount() {
        mViewModel.getCount().bindLifeCycle(this).subscribe({
            mViewModel.scanCount.set(it.size)
        }, {

        })
    }

    private fun refreshLatestView(){
        val latest  = mViewModel.getlatestOrder()
        mViewModel.latestOrderBean.set(latest)
    }

    /**
     * 连接服务端发送，发送命令
     */
    private fun pairConnect() {
        val bluetoothDevices = blueToothController?.getBondedDeviceList()?.filter { it.bluetoothClass.majorDeviceClass == Constants.EQUIPMENT_IDENTITY }
        if (!bluetoothDevices.isNullOrEmpty()) {
            toast("连接中。。。")
            progress.visibility = View.VISIBLE
            connectBlueTask?.cancel()
            blueToothController?.cancelScanBlue()
            connectBlueTask = ConnectBlueTask(uiHandler)
            connectBlueTask?.execute(bluetoothDevices[0])
            connectBlueTask?.sendCommand(Constants.P5_13_COMMAND)
        } else {
            toast("无配对蓝牙")
        }
    }


    override fun loadData(isRefresh: Boolean) {}


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_complete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_complete) {
            setResult(Activity.RESULT_OK)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        connectBlueTask?.cancel()
        uiHandler.removeCallbacksAndMessages(null)
        unregisterReceiver(receiver)
        gunManager?.filterGun?.unRegister()
    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_confirm -> {
//                if (mViewModel.isEnabled.get() == true) {
//                    if (isResetScanning == true) {
//                        mViewModel.resetSaveData()
//                    } else {
//                        mViewModel.saveData()
//                    }
//                }
                isDiBang = !isDiBang
                mViewModel.orderType.set(if(isDiBang) "此单地磅单" else "此单外围单")
//                mViewModel.bgColor.set(if(isDiBang) R.mipmap.bg_yello else R.mipmap.bg_red)
                tv_confirm.setBackgroundResource(if(isDiBang) R.mipmap.bg_yello else R.mipmap.bg_red)
            }
            R.id.tv_weight -> {
                connectBlueTask?.sendCommand("R")
            }
            R.id.tv_order_code -> {
                mViewModel.orderCode.set(System.currentTimeMillis().toString())
            }
            R.id.tv_previous_order -> {
                customerName?.let {
                    mViewModel.previousOrder(it).bindLifeCycle(this).subscribe({
                        toastSuccess("成功")
                    }, {
                        toastSuccess("失败")
                    })
                }
            }
            R.id.iv_latest_delete->{
                val latestItem = mViewModel.getlatestOrder()
                if(latestItem == null){
                    toast("没有可以删除的数据")
                    return
                }
                mViewModel.deleteOrderItem(latestItem)
                getCount()
                refreshLatestView()
            }
            R.id.tv_today_list ->{
                navigateToActivity(TodayActivity::class.java)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                    uiHandler.postDelayed({
                        val bluetoothDevices = blueToothController?.getBondedDeviceList()
                        if (blueToothController?.isBlueEnabled() == false && bluetoothDevices.isNullOrEmpty()) {
                            dialogHintView.show("蓝牙已断开")
                            connectBlueTask?.cancel()
                        } else {
                            pairConnect()
                        }
                    }, 1000)

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun submit(text: String) { // validate
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请您输入要朗读的文字", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.equals(speechText, text)) {
            return
        }
        speechText = text
        val mTextToSpeech = Application.application.mTextToSpeech
        if (mTextToSpeech != null && !mTextToSpeech.isSpeaking) { /*
                TextToSpeech的speak方法有两个重载。
                // 执行朗读的方法
                speak(CharSequence text,int queueMode,Bundle params,String utteranceId);
                // 将朗读的的声音记录成音频文件
                synthesizeToFile(CharSequence text,Bundle params,File file,String utteranceId);
                第二个参数queueMode用于指定发音队列模式，两种模式选择
                （1）TextToSpeech.QUEUE_FLUSH：该模式下在有新任务时候会清除当前语音任务，执行新的语音任务
                （2）TextToSpeech.QUEUE_ADD：该模式下会把新的语音任务放到语音任务之后，
                等前面的语音任务执行完了才会执行新的语音任务
             */
            val speak = mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "1001")
            if (speak == -1) {
                toast("语音功能，需要重启应用重新进入")
            }
        }
    }
}
