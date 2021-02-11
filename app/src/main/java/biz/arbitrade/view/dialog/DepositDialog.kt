package biz.arbitrade.view.dialog;

import android.R.style.Theme_Translucent_NoTitleBar
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import biz.arbitrade.R
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder


class DepositDialog(activity: Activity, wallet: String) {
  private val dialog = Dialog(activity, Theme_Translucent_NoTitleBar)

  init {
    val view = activity.layoutInflater.inflate(R.layout.dialog_deposit, null)
    val imgQr = view.findViewById<ImageView>(R.id.imgQr)
    val txtWallet = view.findViewById<TextView>(R.id.txtWallet)

    txtWallet.text = wallet
    val barcodeEncoder = BarcodeEncoder()
    val bitmap = barcodeEncoder.encodeBitmap(wallet, BarcodeFormat.QR_CODE, 500, 500)
    imgQr.setImageBitmap(bitmap)

    imgQr.setOnClickListener { copy(wallet) }
    txtWallet.setOnClickListener { copy(wallet) }

    dialog.window?.setWindowAnimations(android.R.style.Animation_Dialog)
    view.findViewById<ImageView>(R.id.close).setOnClickListener { dialog.dismiss() }
    dialog.setContentView(view)
    dialog.show()
  }

  private fun copy(text: String){
    val clipboard = dialog.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(text, text)
    clipboard.setPrimaryClip(clip)
  }
}