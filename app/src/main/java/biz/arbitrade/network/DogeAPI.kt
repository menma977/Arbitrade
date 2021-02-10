package biz.arbitrade.network

import android.util.Log
import java.util.concurrent.Callable
import okhttp3.FormBody
import okhttp3.Response
import org.json.JSONObject

class DogeAPI(private val body: FormBody.Builder) : Callable<JSONObject> {
  private val key = "f99ec64d5e534ab4a7fb863a67121c72a"

  override fun call(): JSONObject {
    body.add("Key", key)
    return http.makePost(Url.doge(), body, null)
  }

  private val http =
      object : Base() {
        override fun responseHandler(response: Response, json: JSONObject): JSONObject {
          return when {
            json.toString().contains("ChanceTooHigh") -> {
              JSONObject().put("code", 500).put("data", "Chance Too High")
            }
            json.toString().contains("ChanceTooLow") -> {
              JSONObject().put("code", 500).put("data", "Chance Too Low")
            }
            json.toString().contains("InsufficientFunds") -> {
              JSONObject().put("code", 500).put("data", "Insufficient Funds")
            }
            json.toString().contains("NoPossibleProfit") -> {
              JSONObject().put("code", 500).put("data", "No Possible Profit")
            }
            json.toString().contains("MaxPayoutExceeded") -> {
              JSONObject().put("code", 500).put("data", "Max Payout Exceeded")
            }
            json.toString().contains("999doge") -> {
              JSONObject()
                  .put("code", 500)
                  .put("data", "Invalid request On Server Wait 5 minute to try again")
            }
            json.toString().contains("error") -> {
              JSONObject().put("code", 500).put("data", "Invalid request")
            }
            json.toString().contains("TooFast") -> {
              JSONObject().put("code", 500).put("data", "Too Fast")
            }
            json.toString().contains("TooSmall") -> {
              JSONObject().put("code", 500).put("data", "Too Small")
            }
            json.toString().contains("LoginRequired") -> {
              JSONObject().put("code", 500).put("data", "Login Required")
            }
            !response.isSuccessful -> {
              Log.e("Error", json.toString())
              JSONObject().put("code", 500).put("data", "Unstable connection / Response Not found")
            }
            else -> JSONObject().put("code", 200).put("data", json)
          }
        }
      }
}
