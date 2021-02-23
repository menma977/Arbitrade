package biz.arbitrade.view.dialog

import android.R.style.Theme_Translucent_NoTitleBar
import android.app.Activity
import android.app.Dialog
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import biz.arbitrade.R
import biz.arbitrade.controller.DogeHelper
import biz.arbitrade.controller.Helper
import biz.arbitrade.model.User
import java.util.*
import kotlin.concurrent.schedule

class WithdrawDialog(private val activity: Activity, private val token: String) {
  private val dialog = Dialog(activity, Theme_Translucent_NoTitleBar)
  private val closeBtn: ImageView
  private val amount: EditText
  private val wallet: EditText
  private val wdBtn: Button
  private val wdAllBtn: Button

  init {
    val view = activity.layoutInflater.inflate(R.layout.dialog_withdraw, null)
    amount = view.findViewById(R.id.txtWithdrawAmount)
    wallet = view.findViewById(R.id.txtWallet)
    closeBtn = view.findViewById(R.id.close)
    wdAllBtn = view.findViewById(R.id.btnWithdrawAll)
    wdBtn = view.findViewById(R.id.btnWithdraw)
    dialog.window?.setWindowAnimations(android.R.style.Animation_Dialog)
    closeBtn.setOnClickListener { dialog.dismiss() }
    wdAllBtn.setOnClickListener { withdraw(0) }
    wdBtn.setOnClickListener {
      if (amount.text.toString().isNotBlank() && wallet.text.toString().isNotBlank()) {
        try {
          val s = Helper.fromDogeString(amount.text.toString())
          if (s > 0) withdraw(s)
        } catch (e: Exception) {
          Toast.makeText(
            activity, "Wallet and/or Doge Amount amount cannot be empty", Toast.LENGTH_SHORT
          ).show()
        }
      }
    }
    dialog.setContentView(view)
    dialog.show()
  }

  private fun toggleInput(v: Boolean) {
    closeBtn.isEnabled = v
    amount.isEnabled = v
    wallet.isEnabled = v
    wdBtn.isEnabled = v
    wdAllBtn.isEnabled = v
  }

  private fun withdraw(total: Long) {
    toggleInput(false)
    Timer().schedule(100) {
      Thread.sleep(3000)
      val response = DogeHelper.withdraw(total, wallet.text.toString(), token).call()
      activity.runOnUiThread {
        if (response.getInt("code") < 400) {
          Toast.makeText(
            activity, "Successfully processing Withdrawal to queue", Toast.LENGTH_SHORT
          ).show()
          toggleInput(true)
        } else {
          Toast.makeText(
            activity, if (response.optString("data").isNotBlank()) response.optString("data")
            else "Failed to withdraw", Toast.LENGTH_SHORT
          ).show()
          toggleInput(true)
          if (response.optString("data") == "Unauthenticated.") {
            dialog.ownerActivity?.runOnUiThread { Helper.logoutAll(dialog.ownerActivity) }
          }
        }
      }
    }
  }
}
