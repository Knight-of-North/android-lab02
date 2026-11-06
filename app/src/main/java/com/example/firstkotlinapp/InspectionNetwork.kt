package com.example.firstkotlinapp

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Android官方模拟器中的10.0.2.2指向教师电脑的回环地址。
private const val BASE_URL = "http://10.0.2.2:8765"
private val client = OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .build()

suspend fun loadInspectionTasks(
    room: String, scenario: DemoScenario, requestKey: String
): List<InspectionTask> = withContext(Dispatchers.IO) {
    val url = "$BASE_URL/api/tasks".toHttpUrl().newBuilder()
        .addQueryParameter("room", room)
        .addQueryParameter("scenario", scenario.value)
        .addQueryParameter("key", requestKey).build()
    val body = awaitBody(Request.Builder().url(url).build())
    val root = JSONObject(body)
    check(root.getString("room") == room) { "响应实验室与请求不一致" }
    val array = root.getJSONArray("tasks")
    // TODO 1：遍历array，将四个字段映射为InspectionTask，返回List。
    // 起点返回空列表，仅保证骨架可运行，不代表解析已完成。
    emptyList<InspectionTask>()
}

// 教师提供的桥接代码：取消协程时，同时取消OkHttp Call。
private suspend fun awaitBody(request: Request): String =
    suspendCancellableCoroutine { continuation ->
        val call = client.newCall(request)
        continuation.invokeOnCancellation {
            call.cancel()
            Log.i("InspectionLab", "HTTP_CANCEL ${request.url}")
        }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isActive) continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.use {
                        if (!it.isSuccessful) throw IOException("HTTP ${it.code}")
                        it.body?.string() ?: throw IOException("响应正文为空")
                    }
                    if (continuation.isActive) continuation.resume(body)
                } catch (e: Exception) {
                    if (continuation.isActive) continuation.resumeWithException(e)
                }
            }
        })
    }
