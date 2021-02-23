package biz.arbitrade.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import biz.arbitrade.R
import kotlinx.android.synthetic.main.activity_info_only.*

class InfoOnlyActivity : AppCompatActivity() {
  companion object TYPE {
    const val MAINTENANCE = "mt"
    const val MISMATCH_VERSION = "vc"
    const val CANNOT_CONNECT = "cc"
    const val SLOW_DOWN = "sd"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_info_only)
    val type = intent.getStringExtra("type")
    notes.text = when (type) {
      MAINTENANCE -> getString(R.string.under_maintenance)
      MISMATCH_VERSION -> getString(R.string.version_mismatch)
      CANNOT_CONNECT -> getString(R.string.check_connection)
      SLOW_DOWN -> getString(R.string.slow_down)
      else -> getString(R.string.unknown_error)
    }
  }
}