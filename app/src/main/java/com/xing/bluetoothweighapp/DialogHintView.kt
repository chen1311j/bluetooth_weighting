package com.xing.bluetoothweighapp

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.xing.library.helper.utils.UtilDialog
import kotlinx.android.synthetic.main.dialog_layout.view.*

class DialogHintView : View {


    var view: View? = null
    var dialog: Dialog? = null

    constructor(context: Activity?) : this(context, null)
    constructor(context: Activity?, onClickListener: OnClickListener?) : super(context, null) {
        view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)
        dialog = UtilDialog.showDialog(view!!)
        if (onClickListener != null) {
            view?.tv_confirm?.setOnClickListener(onClickListener)
        } else {
            view?.tv_confirm?.setOnClickListener {
                cancel()
            }
        }
    }

    fun show(hint: String) {
        view?.tv_hint?.text = hint
        if (dialog != null && dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    fun cancel() {
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    fun setConfirm(onClickListener: OnClickListener) {
        view?.tv_confirm?.setOnClickListener(onClickListener)
    }
}