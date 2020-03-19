package com.xing.bluetoothweighapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.xing.library.helper.utils.UtilDialog
import kotlinx.android.synthetic.main.dialog_layout.view.*

class ProgressView : View {


    private var dialog: Dialog? = null

    constructor(context: Activity) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        val view = LayoutInflater.from(context).inflate(R.layout.progress_layout, null)
        dialog = UtilDialog.showProgressDialog(view)
    }


    fun show() {
        if (dialog != null && dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    fun cancel() {
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }
}
