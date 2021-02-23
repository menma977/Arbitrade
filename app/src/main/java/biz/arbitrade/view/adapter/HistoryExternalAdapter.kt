package biz.arbitrade.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import biz.arbitrade.R
import biz.arbitrade.model.HistoryExternal
import java.util.*

class HistoryExternalAdapter(private val context: Context) : RecyclerView.Adapter<HistoryExternalAdapter.ViewHolder>() {
  private val data = ArrayList<HistoryExternal>()

  init {
    data.add(HistoryExternal(R.color.textPrimary, "Address", "HASH", "Balance", "AT"))
  }

  class ViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
    val address: TextView = layout.findViewById(R.id.address)
    val hash: TextView = layout.findViewById(R.id.hash)
    val balance: TextView = layout.findViewById(R.id.balance)
    val date: TextView = layout.findViewById(R.id.date)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val layout = LayoutInflater.from(parent.context).inflate(R.layout.row_history_external, parent, false)
    return ViewHolder(layout)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.address.text = data[position].address
    holder.hash.text = data[position].hash
    holder.hash.setOnClickListener {
      val uri = "https://dogechain.info/tx/${data[position].hash}"
      val move = Intent(Intent.ACTION_VIEW)
      move.data = Uri.parse(uri)
      context.startActivity(move)
    }
    holder.balance.text = data[position].balance
    holder.balance.setTextColor(ContextCompat.getColor(context, data[position].color))
    holder.date.text = data[position].date
  }

  override fun getItemCount() = data.size

  fun addItem(item: HistoryExternal) {
    data.add(1, item)
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, data.size)
  }

  fun clear() {
    data.clear()
    data.add(HistoryExternal(R.color.textPrimary, "Address", "HASH", "Balance", "AT"))
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, data.size)
  }
}