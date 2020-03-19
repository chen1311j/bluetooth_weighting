/**
 * generate by AAMVVM: https://github.com/HeadingMobile/AAMVVM
 */
package com.xing.bluetoothweighapp.view

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Process
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.xing.bluetoothweighapp.Application
import com.xing.bluetoothweighapp.DialogHintView
import com.xing.bluetoothweighapp.ProgressView
import com.xing.bluetoothweighapp.R
import com.xing.bluetoothweighapp.bluetooth.BlueToothController
import com.xing.bluetoothweighapp.bluetooth.ConnectBlueTask
import com.xing.bluetoothweighapp.dao.DaoMaster
import com.xing.bluetoothweighapp.dao.WeightOrderBeanDao
import com.xing.bluetoothweighapp.databinding.MainActivityBinding
import com.xing.bluetoothweighapp.db.DBMangaer
import com.xing.bluetoothweighapp.view.viewmodel.MainViewModel
import com.xing.library.aop.annotation.SingleClick
import com.xing.library.helper.Constants
import com.xing.library.helper.extens.bindLifeCycle
import com.xing.library.helper.extens.logD
import com.xing.library.helper.extens.navigateToActivity
import com.xing.library.helper.extens.toast
import com.xing.library.net.NetStateReceiver
import com.xing.library.view.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.mockito.internal.util.io.IOUtil.close
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService
import java.io.*
import java.nio.channels.FileChannel
import java.util.*

/**
 * description: MainActivity
 * @see MainViewModel
 *
 * @date 2020/02/21
 */

class MainActivity : BaseActivity<MainActivityBinding>(), TextToSpeech.OnInitListener {


    companion object {
        const val ACTION_BLUETOOTH_CODE = 1024
    }


    private var netStatusDialog: AlertDialog? = null
    private var mediaPlayer: MediaPlayer? = null
    private var connectBlueTask: ConnectBlueTask? = null
    private lateinit var blueToothController: BlueToothController
    private val mViewModel by viewModel<MainViewModel>()


    private var mTextToSpeech: TextToSpeech? = null
    private val REQUEST_READ_WRITE = 1212
    private val downloadPath = "${Environment.getExternalStorageDirectory().absolutePath}/Download"
    private val copyDBPath = "${downloadPath}/copy.db"
    private val apkFilePath = "$downloadPath/度秘语音引擎3.0-BaiduSpeechService.apk"

    private val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    override fun getLayoutId(): Int = R.layout.main_activity


    private val dialogHintView: DialogHintView by lazy {
        DialogHintView(this)
    }
    private val progress: ProgressView by lazy {
        ProgressView(this)
    }
    val handler = Handler()

    override fun initView() {
        mBinding.vm = mViewModel
        mToolbar?.setNavigationIcon(null)
        mTvTitle?.setText("首页")
        initTextToSpeech()
        blueToothController = BlueToothController()
        Application.application.blueToothController = blueToothController
        if (blueToothController.isBlueEnabled() == false || blueToothController.getBondedDeviceList().isNullOrEmpty()) {
            dialogHintView.show("未开启蓝牙，是否开启")
            dialogHintView.setConfirm(View.OnClickListener {
                dialogHintView.cancel()
                blueToothController.openBlueSync(this, ACTION_BLUETOOTH_CODE)
            })
        }

        NetStateReceiver.registerNetworkStateReceiver(this)
        mediaPlayer = MediaPlayer.create(this, R.raw.netwoker_status)
        copyDBFile()
        SQLiteStudioService.instance().start(this)
    }

    private fun copyDBFile() {
        try {
            val dataBasePath = "data/" + Environment.getDataDirectory().absolutePath + "/$packageName/databases/${DBMangaer.getDBName()}"
            val dbFile = File(dataBasePath)
            if (File(dataBasePath).exists()) {
                val downloadFile = File(downloadPath)
                if (!downloadFile.exists()) {
                    downloadFile.mkdir()
                }
                val copyFile = File(copyDBPath)
                if (!copyFile.exists()) {
                    copyFile.createNewFile()
                }
                nioTransferCopy(dbFile,copyFile)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun nioTransferCopy(source: File, target: File) {
        var inc: FileChannel? = null
        var out: FileChannel? = null
        var inStream: FileInputStream? = null
        var outStream: FileOutputStream? = null
        try {
            inStream = FileInputStream(source)
            outStream = FileOutputStream(target)
            inc = inStream.getChannel()
            out = outStream.getChannel()
            inc.transferTo(0, inc.size(), out)
        } catch (e: IOException) {
            e.printStackTrace();
        } finally {
            close(inStream)
            close(inc)
            close(outStream)
            close(out)
        }
    }

    override fun loadData(isRefresh: Boolean) {}

    @SingleClick
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_scanning -> {
                scanningWeight(false)
            }
            R.id.btn_bluetooth -> {
                if (blueToothController.isBlueEnabled() == true) {
                    startActivityForResult(Intent(Settings.ACTION_BLUETOOTH_SETTINGS), ACTION_BLUETOOTH_CODE)
                } else {
                    blueToothController.openBlueSync(this, ACTION_BLUETOOTH_CODE)
                }
            }
            R.id.btn_upload -> {
                mViewModel.uploadWeight().doOnDispose {
                    progress.show()
                }.bindLifeCycle(this)
                    .subscribe({
                        progress.cancel()
                        if (it is String) {
                            dialogHintView.show("没有可上传的数据")
                        } else {
                            dialogHintView.show("上传成功")
                        }
                    }, {
                        progress.cancel()
                        dialogHintView.show("上传失败，请重新上传")
                    })
            }
            R.id.today_data -> {
                navigateToActivity(TodayActivity::class.java)
            }
            R.id.reset_scanning -> {
                scanningWeight(true)
            }
        }
    }


    private fun scanningWeight(isResetScanning: Boolean) {
        val bondedDeviceList = blueToothController.getBondedDeviceList()
        if (!bondedDeviceList.isNullOrEmpty()) {
            val filter = bondedDeviceList.filter { it.bluetoothClass.majorDeviceClass == Constants.EQUIPMENT_IDENTITY }
            if (!filter.isNullOrEmpty()) {

                navigateToActivity(SelectCustomerActivity::class.java, isResetScanning)
            } else {
                dialogHintView.show("请连接蓝牙")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_BLUETOOTH_CODE) {
            val bondedDeviceList = blueToothController.getBondedDeviceList()?.filter { it.bluetoothClass.majorDeviceClass != Constants.EQUIPMENT_IDENTITY }
            if (bondedDeviceList.isNullOrEmpty()) {
                dialogHintView.show("请连接蓝牙")
            }
        } else if (requestCode == 120) {
            saveInstall()
        } else if (requestCode == 130) {
            var result = mTextToSpeech?.setLanguage(Locale.CHINA)
            if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                System.exit(0)

            }
        }
    }

    private fun checkPermissionAllGranted(permissions: Array<String>): Boolean {
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    private fun initTextToSpeech() { // 参数Context,TextToSpeech.OnInitListener
        mTextToSpeech = TextToSpeech(this, this)
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        mTextToSpeech?.setPitch(1.0f)
        // 设置语速
        mTextToSpeech?.setSpeechRate(1f)
        Application.application.mTextToSpeech = mTextToSpeech
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            /*
                使用的是小米手机进行测试，打开设置，在系统和设备列表项中找到更多设置，
            点击进入更多设置，在点击进入语言和输入法，见语言项列表，点击文字转语音（TTS）输出，
            首选引擎项有三项为Pico TTs，科大讯飞语音引擎3.0，度秘语音引擎3.0。其中Pico TTS不支持
            中文语言状态。其他两项支持中文。选择科大讯飞语音引擎3.0。进行测试。

                如果自己的测试机里面没有可以读取中文的引擎，
            那么不要紧，我在该Module包中放了一个科大讯飞语音引擎3.0.apk，将该引擎进行安装后，进入到
            系统设置中，找到文字转语音（TTS）输出，将引擎修改为科大讯飞语音引擎3.0即可。重新启动测试
            Demo即可体验到文字转中文语言。
             */
// setLanguage设置语言
            var result = mTextToSpeech?.setLanguage(Locale.CHINA)
            // TextToSpeech.LANG_MISSING_DATA：表示语言的数据丢失
// TextToSpeech.LANG_NOT_SUPPORTED：不支持
            if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                /* Toast.makeText(this, "数据丢失或不支持", Toast.LENGTH_SHORT)
                     .show()*/
                saveInstall()

            }
        } else {
            saveInstall()
        }
    }

    override fun onNetworkDisConnected() {
        if (netStatusDialog == null) {
            netStatusDialog = AlertDialog.Builder(mContext).setTitle("提示")
                .setMessage("网络断开确认需要设置？")
                .setPositiveButton("确认") { dialog, _ ->
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                    dialog.dismiss()
                }.setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()

                }.create()
        } else {
            netStatusDialog?.show()
        }


        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }


    private fun saveInstall() {
        if (!hasApplication()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermissionAllGranted(permissions)) {
                    if (saveFile(apkFilePath)) {

                        install(apkFilePath)
                    }
                } else {
                    requestPermissions(permissions, REQUEST_READ_WRITE)
                }

            } else {
                if (saveFile(apkFilePath)) {
                    install(apkFilePath)
                }
            }
        } else {
            resetApp()
        }
    }

    private fun resetApp() {
        startActivityForResult(Intent("com.android.settings.TTS_SETTINGS"), 130)
        toast("请选择度秘语音引擎", Toast.LENGTH_LONG)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_WRITE) {
            val count = grantResults.count { it == PackageManager.PERMISSION_GRANTED }
            if (count == permissions.size) {
                if (saveFile(apkFilePath)) {
                    install(apkFilePath)
                }
            } else {
                toast("允许读写权限")
            }
        }
    }

    private fun saveFile(path: String): Boolean {
        try {
            val open = assets.open("度秘语音引擎3.0-BaiduSpeechService.apk")
            val apkFile = File(path)
            if (apkFile.exists()) {
                return true
            }
            apkFile.createNewFile()
            val fileOutputStream = FileOutputStream(apkFile)
            val byteArray = ByteArray(1024)
            var index: Int
            open.use {
                while (open.read(byteArray).also { index = it } != -1) {
                    fileOutputStream.write(byteArray, 0, index)
                }
            }
            fileOutputStream.close()
            open.close()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }

    fun install(filePath: String) {
        handler.postDelayed({
            //Log.i(FragmentActivity.TAG, "开始执行安装: $filePath")
            val apkFile = File(filePath)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //参数1 上下文, 参数2 在AndroidManifest中的android:authorities值, 参数3  共享的文件
                //参数1 上下文, 参数2 在AndroidManifest中的android:authorities值, 参数3  共享的文件
                val apkUri = FileProvider.getUriForFile(this, "$packageName.provider", File(filePath))
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            } else {
                //Log.w(FragmentActivity.TAG, "正常进行安装")
                intent.setDataAndType(
                    Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive"
                )
            }
            startActivityForResult(intent, 120)
        }, 300)
    }

    private fun hasApplication(): Boolean {
        val packageManager = getPackageManager()
        val installedPackages = packageManager.getInstalledPackages(0)
        installedPackages.forEach {
            if (it.packageName.equals("com.baidu.duersdk.opensdk")) {
                return true
            }
        }
        return false
    }

    /*override fun onStop() {
        super.onStop()
        // 不管是否正在朗读TTS都被打断
        mTextToSpeech?.stop()
        // 关闭，释放资源
        mTextToSpeech?.shutdown()
    }*/

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        NetStateReceiver.unRegisterNetworkStateReceiver(this)
        if (mTextToSpeech != null) {
            mTextToSpeech?.stop()
            mTextToSpeech?.shutdown()
            mTextToSpeech = null
        }
    }
}
