package biz.arbitrade.view.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import biz.arbitrade.R
import biz.arbitrade.controller.Helper
import biz.arbitrade.model.Withdraw
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class DepositHistoryAdapter(private val dataList: ArrayList<Withdraw>) :
  RecyclerView.Adapter<DepositHistoryAdapter.ViewHolder>() {

  private lateinit var context: Context

  fun add(value: Withdraw) {
    dataList.add(0, value)
    notifyItemRangeChanged(0, dataList.size + 1)
    notifyDataSetChanged()
  }

  fun clear(){
    dataList.removeAll { true }
    notifyItemRangeChanged(0, dataList.size + 1)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    context = parent.context
    val view =
      LayoutInflater.from(parent.context).inflate(R.layout.row_deposit_history, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    if (position == 0) {
      headerIfy(holder.amount)
      headerIfy(holder.desc)
      headerIfy(holder.dateTime)
      holder.amount.text = context.resources.getString(R.string.amount)
      holder.desc.text = context.resources.getString(R.string.description)
      holder.dateTime.text = context.resources.getString(R.string.datetime)
    } else {
      itemIfy(holder.amount)
      itemIfy(holder.desc)
      itemIfy(holder.dateTime)
      val data = dataList[position - 1]
      holder.amount.text = Helper.toDogeString(data.amount)
      holder.desc.text = data.description
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        holder.dateTime.text =
          LocalDateTime.ofEpochSecond(
            data.datetime / 1000,
            (data.datetime % 100).toInt(),
            ZoneOffset.UTC
          )
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
      } else {
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT)
        val cal = Calendar.getInstance()
        cal.timeInMillis = data.datetime
        holder.dateTime.text = formatter.format(cal.time)
      }
    }
  }

  override fun getItemCount(): Int {
    return dataList.size + 1
  }

  private fun headerIfy(view: TextView) {
    view.typeface = Typeface.create(view.typeface, Typeface.BOLD)
    view.textAlignment = View.TEXT_ALIGNMENT_CENTER
    view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
    view.setTextColor(ContextCompat.getColor(context, R.color.White))
  }

  private fun itemIfy(view: TextView) {
    view.typeface = Typeface.create(view.typeface, Typeface.NORMAL)
    view.textAlignment = View.TEXT_ALIGNMENT_CENTER
    view.setBackgroundColor(ContextCompat.getColor(context, R.color.Default))
    view.setTextColor(ContextCompat.getColor(context, R.color.textPrimary))
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val amount: TextView = itemView.findViewById(R.id.txtAmount)
    val desc: TextView = itemView.findViewById(R.id.txtDesc)
    val dateTime: TextView = itemView.findViewById(R.id.txtDateTime)
  }

}