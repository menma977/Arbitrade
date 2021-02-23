package biz.arbitrade.view.dialog;

import android.R.style.Theme_Translucent_NoTitleBar
import android.app.Activity
import android.app.Dialog
import biz.arbitrade.R

class Loading(activity: Activity) {
  private val dialog = Dialog(activity, Theme_Translucent_NoTitleBar)

  init {
    val view = activity.layoutInflater.inflate(R.layout.activity_main, null)
    dialog.window?.setWindowAnimations(android.R.style.Animation_Dialog)
    dialog.setContentView(view)
    dialog.setCancelable(false)
  }

  fun openDialog() {
    dialog.show()
  }

  fun closeDialog() {
    dialog.dismiss()
  }
}