package com.xing.library.helper.utils

import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.xing.library.R

object UtilDialog {

     fun  showDialog(contentView : View):Dialog{
        val context = contentView.context
        val centerDialog = Dialog(context, R.style.ActionSheetDialogStyle)
        if (contentView.parent != null) {
            val viewGroup = contentView.parent as ViewGroup
            viewGroup.removeAllViews()
        }
        centerDialog.setContentView(contentView)
        val layoutParams = contentView.layoutParams
        layoutParams.width = context.resources.displayMetrics.widthPixels
        contentView.layoutParams = layoutParams

        if (centerDialog.window != null) {
            centerDialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            centerDialog.window!!.setGravity(Gravity.CENTER)
            //centerDialog.window!!.setWindowAnimations(R.style.BottomDialog_Animation)
        }
        if (!centerDialog.isShowing) {
            centerDialog.show()
        }
        return centerDialog
    }

    fun  showProgressDialog(contentView : View):Dialog{
        val context = contentView.context
        val centerDialog = Dialog(context, R.style.ActionSheetDialogStyle)
        if (contentView.parent != null) {
            val viewGroup = contentView.parent as ViewGroup
            viewGroup.removeAllViews()
        }
        centerDialog.setContentView(contentView)
        val layoutParams = contentView.layoutParams
        layoutParams.width = context.resources.displayMetrics.widthPixels
        contentView.layoutParams = layoutParams

        if (centerDialog.window != null) {
            centerDialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            centerDialog.window!!.setGravity(Gravity.CENTER)
            //centerDialog.window!!.setWindowAnimations(R.style.BottomDialog_Animation)
        }
        if (!centerDialog.isShowing) {
            centerDialog.show()
        }
        return centerDialog
    }
}