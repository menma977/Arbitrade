package biz.arbitrade.view.activity

import android.os.Bundle
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
  private var isStart = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_send_ticket)

    user = User(this)

    btnScan = findViewById(R.id.btnScan)
    btnSend = findViewById(R.id.btnSend)
    frameScanner = findViewById(R.id.frameLayoutScanner)
    txtWallet = findViewById(R.id.editTextWallet)
    txtAmount = findViewById(R.id.editTextTicket)

    initScannerView()
    btnScan.setOnClickListener {
      if (isStart) {
        scannerEngine.startCamera()
        isStart = false
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
          if (response.getInt("code") < 400) {
            Toast.makeText(this@SendTicketActivity, response.getString("message"), Toast.LENGTH_SHORT).show()
          }else{
            if(response.optString("data") == "Unauthenticated."){
              Helper.logoutAll(this@SendTicketActivity)
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
      }
    }
    frameScanner.addView(scannerEngine)
  }
}