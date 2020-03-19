package com.xing.bluetoothweighapp

import android.bluetooth.BluetoothDevice
import android.speech.tts.TextToSpeech
import androidx.multidex.MultiDexApplication
import com.xing.bluetoothweighapp.bluetooth.BlueToothController
import com.xing.bluetoothweighapp.di.appModule
import com.xing.library.helper.network.NetMgr
import com.xing.library.model.remote.BaseNetProvider
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger
import org.koin.standalone.StandAloneContext.startKoin

class Application : MultiDexApplication() {

     var blueToothController: BlueToothController? = null
     var  mTextToSpeech: TextToSpeech? = null

    override fun onCreate() {
        super.onCreate()
        application = this
        NetMgr.registerProvider(BaseNetProvider(this))
        startKoin(this, appModule, logger = AndroidLogger(showDebug = BuildConfig.DEBUG))
    }

    companion object {
        lateinit var application: Application
    }
}
