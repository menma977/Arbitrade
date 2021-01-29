package biz.arbitrade.network

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

abstract class Base {
    abstract fun responseHandler(response: Response, json: JSONObject): JSONObject

    fun makePost(
        url: String,
        requestBody: FormBody.Builder?,
        headers: Map<String, String>?
    ): JSONObject {
        return makeRequest(url, "POST", requestBody?.build(), headers)
    }

    fun makeGet(
        url: String,
        queries: Map<String, String>?,
        headers: Map<String, String>?
    ): JSONObject {
        var queryString = ""
        if (!queries.isNullOrEmpty())
            queries.forEach { request ->
                queryString += "${request.key}=${request.value}&"
            }
        return makeRequest("$url?$queryString", "GET", null, headers)
    }

    private fun makeRequest(
        url: String,
        method: String,
        body: RequestBody?,
        headers: Map<String, String>?
    ): JSONObject {
        try {
            val client = OkHttpClient.Builder()
            client.connectTimeout(30, TimeUnit.SECONDS)
            client.writeTimeout(30, TimeUnit.SECONDS)
            client.readTimeout(30, TimeUnit.SECONDS)
            val request = Request.Builder()
            request.url(url)
            request.method(method, body)
            if (!headers.isNullOrEmpty())
                headers.forEach { header ->
                    request.addHeader(header.key, header.value)
                }
            request.addHeader("charset", "utf-8")
            request.addHeader("Content-Type", "application/json")
            request.addHeader("Accept", "application/json")
            request.addHeader("X-Requested-With", "XMLHttpRequest")
            val response = client.build().newCall(request.build()).execute()
            val json = JSONObject(response.body!!.string())
            return responseHandler(response, json)
        } catch (e: Exception) {
            Log.e("json", e.stackTraceToString())
            return JSONObject().put("code", 500).put("data", e.message)
        }
    }
}