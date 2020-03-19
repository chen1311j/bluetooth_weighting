package com.xing.library.helper.extens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.xing.library.helper.Constants
import com.xing.library.model.data.BaseResponse
import com.xing.library.model.remote.exception.EmptyException
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.FlowableSubscribeProxy
import com.uber.autodispose.SingleSubscribeProxy
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.xing.library.BuildConfig
import com.xing.library.helper.annotation.ToastType
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.MainThreadDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.Serializable
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit


/**
 * 页面描述：一些扩展
 *
 * Created by ditclear on 2017/9/29.
 */

fun Activity.getCompactColor(@ColorRes colorRes: Int): Int = ContextCompat.getColor(this, colorRes)

fun Activity.toast(
    msg: CharSequence,
    duration: Int = Toast.LENGTH_SHORT, @ToastType type: Int = ToastType.NORMAL
) {
    when (type) {
        ToastType.WARNING -> Toast.makeText(this, msg, duration).show()
        ToastType.ERROR -> Toast.makeText(this, msg, duration).show()
        ToastType.NORMAL -> Toast.makeText(this, msg, duration).show()
        ToastType.SUCCESS -> Toast.makeText(this, msg, duration).show()
    }
}


fun Activity.dispatchFailure(error: Throwable?) {
    error?.apply {
        if (BuildConfig.DEBUG) {
            printStackTrace()
        }
        if (this is EmptyException) {

        } else if (error is SocketTimeoutException) {
            message?.let { toast("网络连接超时", ToastType.ERROR) }

        } else if (this is UnknownHostException || this is ConnectException) {
            //网络未连接
            message?.let { toast("网络未连接", ToastType.ERROR) }

        } else {
            message?.let { toast(it, ToastType.ERROR) }
        }
    }
}

fun <T : Any> androidx.fragment.app.FragmentActivity.argument(key: String) =
    lazy { intent.extras?.get(key) as? T ?: error("Intent Argument $key is missing") }

fun AppCompatActivity.switchFragment(@IdRes layoutId:Int,
    current: Fragment?,
    targetFg: Fragment,
    tag: String? = null
) {
    val ft = supportFragmentManager.beginTransaction()
    current?.run { ft.hide(this) }
    if (!targetFg.isAdded) {
        ft.add(layoutId, targetFg, tag)
    }
    ft.show(targetFg)
    ft.commitAllowingStateLoss()
}

fun Activity.dpToPx(@DimenRes resID: Int): Int = this.resources.getDimensionPixelOffset(resID)

fun Activity.navigateToActivity(c: Class<*>, serializable: Serializable? = null) {
    val intent = Intent()
    serializable?.let {
        val bundle = Bundle()
        bundle.putSerializable(Constants.KEY_SERIALIZABLE, it)
        intent.putExtras(bundle)
    }
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()

    intent.setClass(this, c)
    startActivity(intent, options)
}

fun Activity.navigateToActivity(
    c: Class<*>,
    serializable: Serializable? = null,
    requestCode: Int = 0
) {
    val intent = Intent()
    serializable?.let {
        val bundle = Bundle()
        bundle.putSerializable(Constants.KEY_SERIALIZABLE, it)
        intent.putExtras(bundle)
    }
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()

    intent.setClass(this, c)
    startActivityForResult(intent, requestCode, options)
}
fun Activity.navigateToActivity(
    c: Class<*>,
    parcelable: Parcelable? = null,
    requestCode: Int = 0
) {
    val intent = Intent()
    parcelable?.let {
        val bundle = Bundle()
        bundle.putParcelable(Constants.KEY_PARCELABLE, it)
        intent.putExtras(bundle)
    }
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()

    intent.setClass(this, c)
    startActivityForResult(intent, requestCode, options)
}
fun Activity.navigateToActivityForResult(c: Class<*>, requestCode: Int = 0) {
    val intent = Intent()
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()

    intent.setClass(this, c)
    startActivityForResult(intent, requestCode, options)
}

fun Fragment.navigateToActivity(c: Class<*>, serializable: Serializable? = null) {
    val intent = Intent()
    serializable?.let {
        val bundle = Bundle()
        bundle.putSerializable(Constants.KEY_SERIALIZABLE, it)
        intent.putExtras(bundle)
    }
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle()

    intent.setClass(requireActivity(), c)
    startActivity(intent, options)
}

fun Fragment.navigateToActivity(
    c: Class<*>,
    serializable: Serializable? = null,
    requestCode: Int = 0
) {
    val intent = Intent()
    serializable?.let {
        val bundle = Bundle()
        bundle.putSerializable(Constants.KEY_SERIALIZABLE, it)
        intent.putExtras(bundle)
    }
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle()

    intent.setClass(requireActivity(), c)
    startActivityForResult(intent, requestCode, options)
}
fun Fragment.navigateToActivityForResult(c: Class<*>, requestCode: Int = 0) {
    val intent = Intent()
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()).toBundle()

    intent.setClass(requireActivity(), c)
    startActivityForResult(intent, requestCode, options)
}

fun Any.logD(msg: String?) {
    if (BuildConfig.DEBUG) {
        Log.d(javaClass.simpleName, msg)
    }
}

fun <T> Flowable<T>.async(withDelay: Long = 0): Flowable<T> =
    this.subscribeOn(Schedulers.io()).delay(withDelay, TimeUnit.MILLISECONDS).observeOn(
        AndroidSchedulers.mainThread()
    )

fun <T> Single<T>.async(withDelay: Long = 0): Single<T> =
    this.subscribeOn(Schedulers.io()).delay(withDelay, TimeUnit.MILLISECONDS).observeOn(
        AndroidSchedulers.mainThread()
    )

fun  <R : BaseResponse<Any>> Single<R>.getOriginData(): Single<R> {
    return this.compose { upstream ->
        upstream.flatMap { t: R ->
            if (t.error_code == 0) {
                return@flatMap Single.just(t)
            } else {
                return@flatMap Single.error<R>(Throwable((t.error)))
            }
        }
    }
}
fun <T> Single<T>.bindLifeCycle(owner: LifecycleOwner): SingleSubscribeProxy<T> =
    this.`as`(
        AutoDispose.autoDisposable(
            AndroidLifecycleScopeProvider.from(
                owner,
                Lifecycle.Event.ON_DESTROY
            )
        )
    )

fun <T> Flowable<T>.bindLifeCycle(owner: LifecycleOwner): FlowableSubscribeProxy<T> =
    this.`as`(
        AutoDispose.autoDisposable(
            AndroidLifecycleScopeProvider.from(
                owner,
                Lifecycle.Event.ON_DESTROY
            )
        )
    )


//////////////////////////LiveData///////////////////////////////////

fun <T> MutableLiveData<T>.set(t: T?) = this.postValue(t)
fun <T> MutableLiveData<T>.get() = this.value

fun <T> MutableLiveData<T>.get(t: T): T = get() ?: t

fun <T> MutableLiveData<T>.init(t: T) = MutableLiveData<T>().apply {
    postValue(t)
}

fun <T> LiveData<T>.toFlowable(): Flowable<T> = Flowable.create({ emitter ->
    val observer = Observer<T> { data ->
        data?.let {
            emitter.onNext(it)
        }
    }
    observeForever(observer)
    emitter.setCancellable {
        object : MainThreadDisposable() {
            override fun onDispose() = removeObserver(observer)
        }
    }
}, BackpressureStrategy.LATEST)

//////////////////////////DataBinding///////////////////////////////////
fun <T> ObservableField<T>.toFlowable(): Flowable<T> = Flowable.create({ emitter ->
    val observer = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            get()?.let {
                emitter.onNext(it)
            }
        }
    }
    addOnPropertyChangedCallback(observer)

    emitter.setCancellable {
        object : MainThreadDisposable() {
            override fun onDispose() = removeOnPropertyChangedCallback(observer)
        }
    }
}, BackpressureStrategy.LATEST)

fun ObservableBoolean.toFlowable(): Flowable<Boolean> = Flowable.create({ emitter ->
    val observer = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            emitter.onNext(get())
        }
    }
    addOnPropertyChangedCallback(observer)

    emitter.setCancellable {
        object : MainThreadDisposable() {
            override fun onDispose() = removeOnPropertyChangedCallback(observer)
        }
    }
}, BackpressureStrategy.LATEST)