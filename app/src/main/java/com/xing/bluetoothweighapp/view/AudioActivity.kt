package com.xing.bluetoothweighapp.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.xing.bluetoothweighapp.R
import com.xing.library.helper.extens.toast
import kotlinx.android.synthetic.main.activity_audio.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class AudioActivity : AppCompatActivity(), TextToSpeech.OnInitListener, View.OnClickListener {

    private var isSaveFile: Boolean = false
    private var result: Int? = 0
    private var mTextToSpeech: TextToSpeech? = null
    private val REQUEST_READ_WRITE = 1212;
    private val downloadPath = "${Environment.getExternalStorageDirectory().absolutePath}/Download"
    private val apkFilePath = "$downloadPath/度秘语音引擎3.0-BaiduSpeechService.apk"

    private val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)
        initTextToSpeech()
        main_btn_read.setOnClickListener(this);

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
            result = mTextToSpeech?.setLanguage(Locale.CHINA)
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
        startActivity(Intent("com.android.settings.TTS_SETTINGS"))
        System.exit(0)

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
        startActivity(intent)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.main_btn_read -> submit()
            else -> {
            }
        }
    }

    private fun submit() { // validate
        val text = main_edit_text.text.toString().trim()
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请您输入要朗读的文字", Toast.LENGTH_SHORT).show()
            return
        }
        // TODO validate success, do something
        if (mTextToSpeech != null && mTextToSpeech?.isSpeaking == false) { /*
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
            val speak = mTextToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "1001")
            if (speak == -1) {
                resetApp()
            }
        } else {
            if (mTextToSpeech?.isSpeaking == true) {
                toast("请连接网络")
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
        }
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

    override fun onStop() {
        super.onStop()
        // 不管是否正在朗读TTS都被打断
        mTextToSpeech?.stop()
        // 关闭，释放资源
        mTextToSpeech?.shutdown()
    }

    override fun onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech?.stop()
            mTextToSpeech?.shutdown()
            mTextToSpeech = null
        }
        super.onDestroy()
    }

}
