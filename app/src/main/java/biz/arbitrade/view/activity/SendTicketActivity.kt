package biz.arbitrade.view.activity

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import biz.arbitrade.R
import biz.arbitrade.controller.Helper
import biz.arbitrade.model.User
import biz.arbitrade.network.ArbizAPI
import me.dm7.barcodescanner.zxing.ZXingScannerView
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class SendTicketActivity : AppCompatActivity() {
  private lateinit var frameScanner: FrameLayout
  private lateinit var user: User
  private lateinit var btnScan: ImageView
  private lateinit var btnSend: Button
  private lateinit var txtWallet: EditText
  private lateinit var txtAmount: EditText
  private lateinit var scannerEngine: ZXingScannerView
  private lateinit var scroll: ScrollView
  private var isStart = true

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_send_ticket)

    user = User(this)

    btnScan = findViewById(R.id.btnScan)
    btnSend = findViewById(R.id.btnSend)
    frameScanner = findViewById(R.id.frameLayoutScanner)
    txtWallet = findViewById(R.id.editTextWallet)
    txtAmount = findViewById(R.id.editTextTicket)
    scroll = findViewById(R.id.scrollview)

    initScannerView()
    btnScan.setOnClickListener {
      isStart = if (isStart) {
        scannerEngine.startCamera()
        frameScanner.visibility = View.VISIBLE
        scroll.post {
          scroll.fullScroll(View.FOCUS_DOWN)
          frameScanner.requestFocus()
        }
        false
      }else{
        frameScanner.visibility = View.GONE
        scannerEngine.stopCamera()
        scroll.post {
          scroll.fullScroll(View.FOCUS_UP)
          btnSend.requestFocus()
        }
        true
      }
    }
    btnSend.setOnClickListener { send() }
  }

  private fun send() {
    when {
      !txtAmount.text.toString().matches(Regex("^\\d+(\\.\\d+)?$")) ->
        Toast.makeText(this@SendTicketActivity, "Invalid Amount", Toast.LENGTH_SHORT).show()
      txtWallet.text.toString().isBlank() ->
          Toast.makeText(this@SendTicketActivity, "Wallet cannot be empty", Toast.LENGTH_SHORT).show()
      else ->
        Timer().schedule(100) {
          val body = FormBody.Builder()
          body.add("total", txtAmount.text.toString())
          body.add("wallet", txtWallet.text.toString())
          val response = ArbizAPI("pin.store", "post", user.getString("token"), body).call()
          runOnUiThread {
            val message = when {
              response.optString("data").isNotBlank() -> response.getString("data")
              response.optString("message").isNotBlank() -> response.getString("message")
              response.getInt("code") >= 400 -> "Cannot complete transaction"
              else -> "Success"
            }
            Toast.makeText(this@SendTicketActivity, message, Toast.LENGTH_SHORT).show()
            if (response.getInt("code") >= 400) {
              if(response.optString("data") == "Unauthenticated."){
                Helper.logoutAll(this@SendTicketActivity)
              }
            }
          }
        }
    }
  }

  private fun initScannerView() {
    scannerEngine = ZXingScannerView(this)
    scannerEngine.setAutoFocus(true)
    scannerEngine.setResultHandler {
      if (it?.text?.isNotEmpty()!!) {
        val wallet = it.text.toString()
        txtWallet.setText(wallet)
        frameScanner.visibility = View.GONE
        scannerEngine.stopCamera()
        scroll.post {
          scroll.fullScroll(View.FOCUS_UP)
          btnSend.requestFocus()
        }
        isStart = true
      }
    }
    frameScanner.visibility = View.GONE
    frameScanner.addView(scannerEngine)
  }

  override fun onPause() {
    scannerEngine.stopCamera()
    super.onPause()
  }
}