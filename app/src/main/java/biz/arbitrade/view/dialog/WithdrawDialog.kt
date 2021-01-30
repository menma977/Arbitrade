package biz.arbitrade.view.dialog;

import android.R.style.Theme_Translucent_NoTitleBar
import android.app.Activity
import android.app.Dialog
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import biz.arbitrade.R
import biz.arbitrade.controller.Helper
import biz.arbitrade.network.ArbizAPI
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class WithdrawDialog(private val activity: Activity, private val token: String) {
  private val dialog = Dialog(activity, Theme_Translucent_NoTitleBar)

  init {
    val view = activity.layoutInflater.inflate(R.layout.dialog_withdraw, null)
    val amount = view.findViewById<EditText>(R.id.txtWithdrawAmount)
    dialog.window?.setWindowAnimations(android.R.style.Animation_Dialog)
    view.findViewById<ImageView>(R.id.close).setOnClickListener { dialog.dismiss() }
    view.findViewById<Button>(R.id.btnWithdrawAll).setOnClickListener { withdraw(0) }
    view.findViewById<Button>(R.id.btnWithdraw).setOnClickListener {
      if (amount.text.toString().isNotBlank()) {
        try {
          val s = Helper.fromDogeString(amount.text.toString())
          if (s > 0) withdraw(s)
        } catch (e: Exception) {
          Toast.makeText(activity, "Invalid Doge amount", Toast.LENGTH_SHORT).show()
        }
      }
    }
    dialog.setContentView(view)
    dialog.show()
  }

  private fun withdraw(satoshi: Long) {
    val body = FormBody.Builder()
    if (satoshi > 0) {
      body.add("amount", satoshi.toString())
    }
    Timer().schedule(100) {
      val response =
        ArbizAPI(if (satoshi > 0) "withdraw" else "withdraw.all", "post", token, body).call()
      if(response.getInt("code") < 400){
        Toast.makeText(activity, "Successfully processing Withdrawal to queue", Toast.LENGTH_SHORT).show()
      }else
        Toast.makeText(activity, response.optString("data") ?: "Failed to withdraw", Toast.LENGTH_SHORT).show()
    }
  }
}