package biz.arbitrade.controller

import android.app.ActivityManager
import android.content.Context
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow

object Helper {
  fun toDogeString(satoshi: Long): String {
    return BigDecimal(satoshi).divide(BigDecimal(10.0.pow(8))).toPlainString()
  }

  fun fromDogeString(doge: String): Long {
    return BigDecimal(doge).times(10.0.pow(8).toBigDecimal()).longValueExact()
  }

  fun <T> subList(a: ArrayList<T>, from: Int, to: Int): ArrayList<T> {
    val ret = ArrayList<T>()
    for (i in from..to) {
      if (i < a.size) ret.add(a[i])
    }
    return ret
  }

  fun getMillis(date: String): Long {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")).toEpochSecond(
        ZoneOffset.UTC
      ) * 1000
    } else {
      try {
        val d = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.ROOT).parse(date)
        d?.time ?: 0
      } catch (e: Exception) {
        0
      }
    }
  }

  fun getDate(date: String): String {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
    } else {
      val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
      val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT)
      try {
        formatter.format(parser.parse(date) ?: "")
      } catch (e: Exception) {
        "-"
      }
    }
  }

  fun isAppIsInBackground(context: Context): Boolean {
    var isInBackground = true
    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningProcesses = am.runningAppProcesses
    for (processInfo in runningProcesses) {
      if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
        for (activeProcess in processInfo.pkgList) {
          if (activeProcess == context.packageName) {
            isInBackground = false
          }
        }
      }
    }
    return isInBackground
  }
}