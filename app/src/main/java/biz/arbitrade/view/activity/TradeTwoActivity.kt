package biz.arbitrade.view.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import biz.arbitrade.R
import biz.arbitrade.controller.TradeTwoController
import biz.arbitrade.model.User
import org.eazegraph.lib.charts.ValueLineChart
import org.eazegraph.lib.models.ValueLinePoint
import org.eazegraph.lib.models.ValueLineSeries
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.random.Random


class TradeTwoActivity : AppCompatActivity() {
    private lateinit var user: User
    private lateinit var chart: ValueLineChart
    private lateinit var controller: TradeTwoController

    private var counter = 1
    private val betDelay: Long = 2500
    private val betPeriod: Long = 250
    private val chartFreq: Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade_two)

        user = User(this)

        chart = findViewById(R.id.cubiclinechart)

        val series = ValueLineSeries()
        series.color = -0x1212ff
        series.addPoint(ValueLinePoint(" ", user.getInteger("balance") * 1f))
        findViewById<Button>(R.id.continueBtn).setOnClickListener {}

        chart.addSeries(series);
        chart.startAnimation();

        var betCounter = 0;
        Timer().scheduleAtFixedRate(betDelay, betPeriod) {
            // controller.bet
            series.addPoint(ValueLinePoint(counter++.toString(), Random(counter).nextFloat()))
            if(series.series.size > chartFreq)
                series.series.removeAt(0)
            chart.addSeries(series)
            if (++betCounter >= 30) {
                this.cancel()
            }
        }
    }
}