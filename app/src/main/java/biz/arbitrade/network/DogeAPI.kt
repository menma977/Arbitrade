package biz.arbitrade.network

import android.util.Log
import java.util.concurrent.Callable
import okhttp3.FormBody
import okhttp3.Response
import org.json.JSONObject

class DogeAPI(private val body: FormBody.Builder) : Callable<JSONObject> {
  private val key = "12650d1e50194d789bf03d22f90ecebe"

  override fun call(): JSONObject {
    return try {
      body.add("Key", key)
      http.makePost(Url.doge(), body, null)
    }catch(e: Exception){
      JSONObject("{\"status\": 500, \"data\":\"Cannot connect to Doge\"}")
    }
  }

  private val http =
      object : Base() {
        override fun responseHandler(response: Response, json: JSONObject): JSONObject {
          Log.d("Mine", response.body.toString())
          Log.d("Mine", json.toString())
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
