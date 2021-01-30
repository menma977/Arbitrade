package biz.arbitrade.view.activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import biz.arbitrade.R

abstract class HistoryActivity<ViewHolder : RecyclerView.ViewHolder, Adapter : RecyclerView.Adapter<ViewHolder>>(
  private val title: String
) : AppCompatActivity() {
  abstract val listAdapter: Adapter
  private lateinit var txtTitle: TextView
  private lateinit var listHistory: RecyclerView
  private lateinit var btnNext: Button
  private lateinit var btnPrev: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history)

    txtTitle = findViewById(R.id.txtTitle)
    listHistory = findViewById(R.id.listHistory)
    btnNext = findViewById(R.id.btnNext)
    btnPrev = findViewById(R.id.btnPrev)

    txtTitle.text = title
    listHistory.layoutManager = LinearLayoutManager(this)
    listHistory.adapter = listAdapter

    btnNext.setOnClickListener { nextAction() }
    btnPrev.setOnClickListener { prevAction() }
  }

  fun btnNext(enable: Boolean) {
    btnNext.isEnabled = enable
  }

  fun btnPrev(enable: Boolean) {
    btnPrev.isEnabled = enable
  }

  abstract fun nextAction()
  abstract fun prevAction()
}