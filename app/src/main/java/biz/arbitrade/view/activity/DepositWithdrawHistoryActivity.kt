package biz.arbitrade.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import biz.arbitrade.controller.Helper
import biz.arbitrade.model.Withdraw
import biz.arbitrade.network.DogeAPI
import biz.arbitrade.view.adapter.DepositHistoryAdapter
import biz.arbitrade.view.dialog.Loading
import okhttp3.FormBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class DepositWithdrawHistoryActivity {
//
//  private lateinit var loading: Loading
//
//  private var currentPage = 0
//  private val perPage = 10
//  private val dataHolder = ArrayList<Withdraw>()
//  private var dataList = ArrayList<Withdraw>()
//  override val listAdapter: DepositHistoryAdapter = DepositHistoryAdapter(dataList)
//
//  override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    loading = Loading(this)
//    loading.openDialog()
//    Timer().schedule(100) {
//      fetchAll()
//      val toIndex = currentPage * perPage + perPage
//      dataList = Helper.subList(
//        dataHolder,
//        currentPage * perPage,
//        if (toIndex > dataHolder.size) dataHolder.size else toIndex
//      )
//      runOnUiThread {
//        refreshAdapter()
//        loading.closeDialog()
//      }
//    }
//  }
//
//  private fun fetchAll(){
//    fetchAllDeposit()
//    fetchAllWithdraw()
//    dataList.sortByDescending { d -> d.datetime }
//  }
//
//  private fun refreshAdapter(){
//    listAdapter.clear()
//    for (i in dataList) {
//      listAdapter.add(i)
//    }
//    btnNext((currentPage + 1) * perPage <= dataHolder.size)
//    btnPrev(currentPage > 0)
//  }
//
//  private fun fetchAllDeposit() {
//    var token = ""
//    do {
//      var res = fetch(token, "GetDeposits")
//      var deposits = JSONArray("[]")
//      var transfers = JSONArray("[]")
//      if (res.getInt("code") >= 400) {
//        Log.e("Error", res.getString("message"))
//      } else {
//        res = res.getJSONObject("data")
//        deposits = res.getJSONArray("Deposits")
//        transfers = res.getJSONArray("Transfers")
//        token = res.getString("Token")
//        for (i in 0 until deposits.length()) {
//          val deposit = deposits.getJSONObject(i)
//          if (deposit.getString("Currency") == "doge") {
//            dataHolder.add(
//              Withdraw(
//                deposit.getLong("Value"),
//                "Deposit",
//                Helper.getMillis(deposit.getString("Date").trim())
//              )
//            )
//          }
//        }
//        for (i in 0 until transfers.length()) {
//          val transfer = transfers.getJSONObject(i)
//          if (transfer.getString("Currency") == "doge") {
//            dataHolder.add(
//              Withdraw(
//                transfer.getLong("Value"),
//                "Transfer In",
//                Helper.getMillis(transfer.getString("Date").trim())
//              )
//            )
//          }
//        }
//      }
//
//    } while (token.isNotBlank() &&
//      (deposits.length() > 0 ||
//        transfers.length() > 0)
//    )
//  }
//
//  private fun fetchAllWithdraw() {
//    var token = ""
//    do {
//      var res = fetch(token, "GetWithdrawals")
//      var withdrawals = JSONArray("[]")
//      var transfers = JSONArray("[]")
//      if (res.getInt("code") >= 400) {
//        Log.e("Error", res.getString("message"))
//      } else {
//        Log.e("MINE", res.toString())
//        res = res.getJSONObject("data")
//        withdrawals = res.getJSONArray("Withdrawals")
//        transfers = res.getJSONArray("Transfers")
//        token = res.getString("Token")
//        for (i in 0 until withdrawals.length()) {
//          val withdrawal = withdrawals.getJSONObject(i)
//          if (withdrawal.getString("Currency") == "doge" && withdrawal.getString("Completed") != "null") {
//            dataHolder.add(
//              Withdraw(
//                withdrawal.getLong("Value"),
//                "Withdraw",
//                Helper.getMillis(withdrawal.getString("Completed").trim())
//              )
//            )
//          }
//        }
//        for (i in 0 until transfers.length()) {
//          val transfer = transfers.getJSONObject(i)
//          if (transfer.getString("Currency") == "doge") {
//            dataHolder.add(
//              Withdraw(
//                transfer.getLong("Value"),
//                "Transfer Out",
//                Helper.getMillis(transfer.getString("Completed").trim())
//              )
//            )
//          }
//        }
//      }
//
//    } while (token.isNotBlank() &&
//      (withdrawals.length() > 0 ||
//        transfers.length() > 0)
//    )
//  }
//
//  private fun fetch(token: String?, a: String): JSONObject {
//    Log.e("FETCH", token ?: "A")
//    val body = FormBody.Builder()
//    body.add("a", a)
//    body.add("s", "ee40fd575c5d452884e330cc4196ec4a")
//    if (!token.isNullOrBlank())
//      body.add("Token", token)
//    return DogeAPI(body).call()
//  }
//
//  override fun nextAction() {
//    if ((currentPage + 1) * perPage < dataHolder.size) {
//      currentPage++
//      val toIndex = currentPage * perPage + perPage
//      dataList = Helper.subList(
//        dataHolder,
//        currentPage * perPage,
//        if (toIndex > dataHolder.size) dataHolder.size else toIndex
//      )
//      refreshAdapter()
//    } else {
//      Toast.makeText(this, "Last page!", Toast.LENGTH_SHORT).show()
//    }
//  }
//
//  override fun prevAction() {
//    if (currentPage > 0) {
//      currentPage--
//      val toIndex = currentPage * perPage + perPage
//      dataList = Helper.subList(
//        dataHolder,
//        currentPage * perPage,
//        if (toIndex > dataHolder.size) dataHolder.size else toIndex
//      )
//      refreshAdapter()
//    } else {
//      Toast.makeText(this, "First page!", Toast.LENGTH_SHORT).show()
//    }
//  }
}