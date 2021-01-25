package biz.arbitrade.network

import okhttp3.FormBody
import okhttp3.Response
import org.json.JSONObject

object DogeAPI {
    private val key = ""

    fun call(body: FormBody.Builder){
        http.makePost(Url.doge(), body, null)
    }

    private val http = object : Base(){
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
                    JSONObject().put("code", 500).put("data", "Invalid request On Server Wait 5 minute to try again")
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
                else -> {
                    JSONObject().put("code", 500).put("data", "Unstable connection / Response Not found")
                }
            }
        }
    }
}