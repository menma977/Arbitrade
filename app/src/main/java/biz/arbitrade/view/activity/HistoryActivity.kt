package biz.arbitrade.view.activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import biz.arbitrade.R
import biz.arbitrade.controller.Helper
import biz.arbitrade.model.HistoryExternal
import biz.arbitrade.model.HistoryInternal
import biz.arbitrade.model.User
import biz.arbitrade.network.DogeAPI
import biz.arbitrade.view.adapter.HistoryExternalAdapter
import biz.arbitrade.view.adapter.HistoryInternalAdapter
import biz.arbitrade.view.dialog.Loading
import okhttp3.FormBody
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class HistoryActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var buttonIn: Button
  private lateinit var buttonOut: Button
  private lateinit var buttonInternal: Button
  private lateinit var buttonExternal: Button
  private lateinit var buttonNext: Button
  private lateinit var listView: RecyclerView
  private lateinit var title: TextView
  private lateinit var result: JSONObject
  private lateinit var listAdapterInternal: HistoryInternalAdapter
  private lateinit var listAdapterExternal: HistoryExternalAdapter
  private var targetUrl = ""
  private var newToken = ""
  private var typeList = R.color.White
  private var typeExternal = ""
  private var isIn = false
  private var isInternal = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history)

    user = User(this)
    loading = Loading(this)

    buttonIn = findViewById(R.id.buttonIn)
    buttonOut = findViewById(R.id.buttonOut)
    buttonInternal = findViewById(R.id.buttonInternal)
    buttonExternal = findViewById(R.id.buttonExternal)
    title = findViewById(R.id.txtTitle)
    listView = findViewById(R.id.listHistory)
    buttonNext = findViewById(R.id.btnNext)

    title.text = "-"
    listAdapterExternal = HistoryExternalAdapter(this)
    listAdapterInternal = HistoryInternalAdapter(this)

    buttonIn.setOnClickListener {
      targetUrl = "GetDeposits"
      buttonInternal.isEnabled = true
      buttonExternal.isEnabled = true
      typeExternal = "Deposits"
      newToken = ""
      isIn = true
      title.text = "IN"
      typeList = R.color.Success
    }

    buttonOut.setOnClickListener {
      targetUrl = "GetWithdrawals"
      buttonInternal.isEnabled = true
      buttonExternal.isEnabled = true
      typeExternal = "Withdrawals"
      newToken = ""
      isIn = false
      title.text = "OUT"
      typeList = R.color.Danger
    }

    buttonInternal.setOnClickListener {
      loading.openDialog()
      isInternal = true
      listView = findViewById<RecyclerView>(R.id.listHistory).apply {
        layoutManager = LinearLayoutManager(applicationContext)
        adapter = listAdapterInternal
      }
      getDataInternal()
    }

    buttonExternal.setOnClickListener {
      loading.openDialog()
      isInternal = false
      listView = findViewById<RecyclerView>(R.id.listHistory).apply {
        layoutManager = LinearLayoutManager(applicationContext)
        adapter = listAdapterExternal
      }
      getDataExternal(typeExternal)
    }

    buttonNext.setOnClickListener {
      loading.openDialog()
      if (isInternal) {
        getDataInternal()
      } else {
        getDataExternal(typeExternal)
      }
    }

    listAdapterInternal.clear()
    listAdapterExternal.clear()
  }

  private fun getDataInternal() {
    listAdapterInternal.clear()
    if (isIn) {
      title.text = "IN - Internal"
    } else {
      title.text = "OUT - Internal"
    }
    Timer().schedule(100) {
      val body = FormBody.Builder()
      body.addEncoded("a", targetUrl)
      body.addEncoded("s", user.getString("cookie"))
      body.addEncoded("Token", newToken)
      result = DogeAPI(body).call()

      if (result.getInt("code") == 200) {
        val list = result.getJSONObject("data")
        runOnUiThread {
          if (list.getString("Token").toString().isNotEmpty()) {
            newToken = list.getString("Token")
            buttonNext.isEnabled = true
          } else {
            buttonNext.isEnabled = false
          }
        }
        val listArray = list.getJSONArray("Transfers")

        for (i in 0 until listArray.length()) {
          val read = listArray.getJSONObject(i)
          val balanceFormat = if (isIn) {
            "+" + Helper.toDogeString(read.getString("Value").toLong())
          } else {
            "-" + Helper.toDogeString(read.getString("Value").toLong())
          }
          val date = try {
            read.getString("Date")
          } catch (e: Exception) {
            read.getString("Completed")
          }
          runOnUiThread {
            listAdapterInternal.addItem(
              HistoryInternal(
                typeList,
                read.getString("Address").replace("XFER", "ARB"),
                balanceFormat,
                date,
              )
            )
          }
        }

        runOnUiThread {
          loading.closeDialog()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_LONG).show()
          loading.closeDialog()
        }
      }
    }
  }

  private fun getDataExternal(nameList: String) {
    listAdapterExternal.clear()
    if (isIn) {
      title.text = "IN - External"
    } else {
      title.text = "OUT - External"
    }
    Timer().schedule(100) {
      val body = FormBody.Builder()
      body.addEncoded("a", targetUrl)
      body.addEncoded("s", user.getString("cookie"))
      body.addEncoded("Token", newToken)
      result = DogeAPI(body).call()

      if (result.getInt("code") == 200) {
        val list = result.getJSONObject("data")
        runOnUiThread {
          if (list.getString("Token").toString().isNotEmpty()) {
            newToken = list.getString("Token")
            buttonNext.isEnabled = true
          } else {
            buttonNext.isEnabled = false
          }
        }
        val listArray = list.getJSONArray(nameList)

        for (i in 0 until listArray.length()) {
          val read = listArray.getJSONObject(i)
          val balanceFormat = if (isIn) {
            "+" + Helper.toDogeString(read.getString("Value").toLong())
          } else {
            "-" + Helper.toDogeString(read.getString("Value").toLong())
          }
          val date = try {
            read.getString("Date")
          } catch (e: Exception) {
            read.getString("Completed")
          }
          runOnUiThread {
            listAdapterExternal.addItem(
              HistoryExternal(
                typeList,
                read.getString("Address").replace("XFER", "WALL"),
                read.getString("TransactionHash"),
                balanceFormat,
                date,
              )
            )
          }
        }

        runOnUiThread {
          loading.closeDialog()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_LONG).show()
          loading.closeDialog()
        }
      }
    }
  }
}