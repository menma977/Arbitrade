package biz.arbitrade.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import biz.arbitrade.R
import biz.arbitrade.controller.TradeOneController
import biz.arbitrade.model.Bet
import biz.arbitrade.model.User
import kotlinx.android.synthetic.main.activity_trade_one.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.abs
import kotlin.random.Random

class TradeOneActivity : AppCompatActivity() {
    private lateinit var user: User
    private lateinit var bet: Bet
    private lateinit var controller: TradeOneController
    private lateinit var spinner: ProgressBar
    private lateinit var loading: ProgressBar
    private lateinit var status: TextView

    private var onTrading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade_one)

        user = User(this)
        bet = Bet(this)
        controller = TradeOneController()

        spinner = findViewById(R.id.spinner)
        loading = findViewById(R.id.progressBar)
        status = findViewById(R.id.txtStatus)
        txtWarning.isSelected = true

        user.setLong("balance", abs(Long.MAX_VALUE))

        val lastBet = Bet.getCalendar(bet.getLong("last_f_trade"))
        val now = Calendar.getInstance()
        if (bet.has("last_f_trade") &&
            lastBet.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
            lastBet.get(Calendar.MONTH) == now.get(Calendar.MONTH)
        ) {
            if (bet.has("last_f_trade_result")) {
                statusChange(
                    if (bet.getBoolean("last_f_trade_result")) R.string.win else R.string.lose,
                    if (bet.getBoolean("last_f_trade_result")) R.color.Info else R.color.Danger
                )
            } else statusChange(R.string.already_play, R.color.Danger)
            spinner.visibility = View.GONE
            status.visibility = View.VISIBLE
            progressBar.progress = progressBar.max
        } else {
            spinner.visibility = View.VISIBLE
            status.visibility = View.GONE
            progressBar.progress = 0
            Timer().schedule(100) {
                val response = controller.trade(user)
                if (response.getInt("code") < 400) {
                    onTrading = true
                    Timer().scheduleAtFixedRate(
                        Random.nextLong() % 750,
                        (Random.nextLong() % 50) + 25
                    ) {
                        runOnUiThread {
                            progressBar.progress++
                            if (progressBar.progress == progressBar.max) {
                                spinner.visibility = View.GONE
                                status.visibility = View.VISIBLE
                                statusChange(
                                    if (response.getString("message") == "WIN") R.string.win else R.string.lose,
                                    if (response.getString("message") == "WIN") R.color.Info else R.color.Danger
                                )
                                onTrading = false
                                cancel()
                            }
                        }
                    }
                } else {
                    Log.e(
                        "TradeOne.ArbiAPI",
                        response.optString("message") ?: response.optString("data")
                    )
                    runOnUiThread {
                        spinner.visibility = View.GONE
                        status.visibility = View.VISIBLE
                        statusChange(R.string.cannot_start_trading, R.color.Danger)
                        onTrading = false
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!onTrading)
            super.onBackPressed()
    }

    private fun statusChange(text: Int, color: Int) {
        status.setText(text)
        status.setTextColor(ContextCompat.getColor(this, color))
    }
}
