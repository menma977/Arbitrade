package biz.arbitrade.controller

import java.math.BigDecimal
import kotlin.math.pow

object Helper {
    fun toDogeString(satoshi: Long): String {
        return BigDecimal(satoshi).divide(BigDecimal(10.0.pow(8))).toPlainString()
    }
}