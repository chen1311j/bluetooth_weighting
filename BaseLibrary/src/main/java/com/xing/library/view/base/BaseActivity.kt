package com.xing.library.view.base

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.transition.Transition
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.transition.TransitionListenerAdapter
import com.xing.library.BR
import com.xing.library.helper.annotation.ToastType
import com.xing.library.helper.extens.dispatchFailure
import com.xing.library.helper.extens.toast
import com.xing.library.net.NetChangeObserver
import com.xing.library.net.NetStateReceiver
import com.xing.library.net.NetType
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.HashMap


/**
 * 页面描述：
 *
 * Created by ditclear on 2017/9/27.
 */
abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity(), Presenter {

    private lateinit var mNetChangeObserver: NetChangeObserver
    protected val mBinding: VB by lazy { DataBindingUtil.setContentView<VB>(this, getLayoutId()) }

    protected lateinit var mContext: Context

    protected var autoRefresh = true
    protected var delayToTransition = false
    protected var mToolbar: Toolbar? = null
    protected var mTvTitle: TextView? = null
    //声明一个SoundPool对象
    protected var soundpool: SoundPool? = null
    private var previousId: Int = 0
    private var handler: Handler? = null
    //使用HashMap管理各种音频
    protected var soundmap: HashMap<Int, Int>? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//竖屏
        super.onCreate(savedInstanceState)
        mContext = this
        mBinding.setVariable(BR.presenter, this)
        mBinding.executePendingBindings()
        mBinding.lifecycleOwner = this
        mToolbar = toolbar
        mTvTitle = tv_title
        toolbar?.let {
            setSupportActionBar(it)
            supportActionBar?.title = ""
            tv_title?.text = title
        }
        initView()
        if (delayToTransition) {
            afterEnterTransition()
        } else if (autoRefresh) {
            loadData(true)
        }
        mNetChangeObserver = object : NetChangeObserver() {
            override fun onNetConnected(type: NetType?) {
                super.onNetConnected(type)
                onNetworkConnected(type)
            }

            override fun onNetDisConnect() {
                super.onNetDisConnect()
                onNetworkDisConnected()
            }
        }
        NetStateReceiver.registerObserver(this, mNetChangeObserver)
    }

    private var enterTransitionListener =
        object : TransitionListenerAdapter(), Transition.TransitionListener {
            override fun onTransitionResume(transition: Transition) {

            }

            override fun onTransitionPause(transition: Transition) {
            }

            override fun onTransitionCancel(transition: Transition) {
            }

            override fun onTransitionStart(transition: Transition) {
            }

            override fun onTransitionEnd(transition: Transition) {
                loadData(true)
            }

        }


    private fun afterEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition.addListener(enterTransitionListener)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition.removeListener(enterTransitionListener)
        }
        NetStateReceiver.removeRegisterObserver(this, mNetChangeObserver)
    }

    abstract override fun loadData(isRefresh: Boolean)

    abstract fun initView()

    abstract fun getLayoutId(): Int
    /**
     * 当前没有网络连接
     */
    protected open fun onNetworkDisConnected() {}

    /**
     * 当前网络连接类型
     *
     * @param type
     */
    protected open fun onNetworkConnected(type: NetType?) {}


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initBackToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        val bar = supportActionBar
        if (bar != null) {
            bar.title = null
            bar.setDisplayHomeAsUpEnabled(true)
            bar.setDisplayShowHomeEnabled(true)
            bar.setDisplayShowTitleEnabled(true)
            bar.setHomeButtonEnabled(true)
        }
    }

    fun toastSuccess(msg: String?) {
        msg?.let { toast(it, ToastType.SUCCESS) }
    }

    fun toastFailure(error: Throwable?) {
        dispatchFailure(error)
    }

    override fun onClick(v: View?) {

    }

    protected fun <T> autoWired(key: String, default: T? = null): T? {
        return intent?.extras?.let { findWired(it, key, default) }
    }

    private fun <T> findWired(bundle: Bundle, key: String, default: T? = null): T? {
        return if (bundle.get(key) != null) {
            try {
                bundle.get(key) as T
            } catch (e: ClassCastException) {
                e.printStackTrace()
                null
            }
        } else default

    }

    fun initSound(resourceId: Int) {
        if (soundpool == null) {
            soundpool = SoundPool(5, AudioManager.STREAM_RING, 0)
        }
        if (soundmap == null) {
            soundmap = HashMap()
        }
        if (soundpool != null && soundmap != null && !soundmap!!.containsKey(resourceId)) {
            soundmap!![resourceId] = soundpool!!.load(this, resourceId, 1)
        }
    }

    fun playSound(resourceId: Int) {
        if (soundpool == null) {
            soundpool = SoundPool(5, AudioManager.STREAM_RING, 0)
        }
        if (soundmap == null) {
            soundmap = HashMap()
        }
        if (soundpool != null && soundmap != null && !soundmap!!.containsKey(resourceId)) {
            soundmap!![resourceId] = soundpool!!.load(this, resourceId, 1)
        }
        if (soundpool != null && soundmap != null && soundmap!!.get(resourceId) != null) {
            soundpool!!.play(soundmap!!.get(resourceId)!!, 1f, 1f, 0, 0, 1f)//播放指定的音频
        }
        soundpool!!.setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener { soundPool, i, i1 ->
            if (soundpool != null && soundmap != null && soundmap!!.get(resourceId) != null) {
                soundpool!!.play(soundmap!!.get(resourceId)!!, 1f, 1f, 0, 0, 1f)//播放指定的音频
            }
        })
    }
}