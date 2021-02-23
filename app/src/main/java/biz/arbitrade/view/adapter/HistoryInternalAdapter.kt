package biz.arbitrade.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import biz.arbitrade.R
import biz.arbitrade.model.HistoryInternal
import java.util.ArrayList

class HistoryInternalAdapter(private val context: Context) : RecyclerView.Adapter<HistoryInternalAdapter.ViewHolder>() {
  private val data = ArrayList<HistoryInternal>()

  init {
    data.add(HistoryInternal(R.color.textPrimary, "Address", "Balance", "AT"))
  }

  class ViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
    val address: TextView = layout.findViewById(R.id.address)
    val balance: TextView = layout.findViewById(R.id.balance)
    val date: TextView = layout.findViewById(R.id.date)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val layout = LayoutInflater.from(parent.context).inflate(R.layout.row_history_internal, parent, false)
    return ViewHolder(layout)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.address.text = data[position].address
    holder.balance.text = data[position].balance
    holder.balance.setTextColor(ContextCompat.getColor(context, data[position].color))
    holder.date.text = data[position].date
  }

  override fun getItemCount() = data.size

  fun addItem(item: HistoryInternal) {
    data.add(1, item)
    this.notifyDataSetChanged()
    this.notifyItemRangeChanged(0, data.size)
  }

  fun clear() {
    data.clear()
    data.add(HistoryInternal(R.color.textPrimary, "Address", "Balance", "AT"))
    this.notifyDataSetChanged()
    this.notifyItemRangeChanged(0, data.size)
  }
}