package com.example.firstkotlinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.net.HttpURLConnection
import java.net.URL

private const val HTTPS_URL = "https://httpbin.org/get?room=A204"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var result by remember { mutableStateOf("等待请求") }
                var busy by remember { mutableStateOf(false) }

                fun request(address: String) {
                    busy = true
                    result = "请求中：$address"
                    Thread {
                        val response = fetch(address)
                        runOnUiThread {
                            result = response
                            busy = false
                        }
                    }.start()
                }

                Column(
                    Modifier.fillMaxSize().statusBarsPadding()
                        .navigationBarsPadding().padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("姓名：舒飞")
                    Text("实验二 网络访问")
                    Button(enabled = !busy, onClick = {
                        request(HTTPS_URL)
                    }) { Text("HTTPS 请求") }
                    Button(enabled = !busy, onClick = {
                        request(HTTPS_URL.replace("https:", "http:"))
                    }) { Text("HTTP 请求") }
                    Text(result)
                }
            }
        }
    }
}

private fun fetch(address: String): String {
    var connection: HttpURLConnection? = null
    return try {
        val c = URL(address).openConnection() as HttpURLConnection
        connection = c
        c.requestMethod = "GET"
        c.connectTimeout = 8000
        c.readTimeout = 8000
        c.instanceFollowRedirects = false
        val status = c.responseCode
        val stream = if (status < 400) c.inputStream else c.errorStream
        val body = stream?.bufferedReader()?.use { it.readText() }
            ?: "无响应正文"
        "$address\nHTTP $status\n${body.take(800)}"
    } catch (e: Exception) {
        "$address\n请求失败：${e.javaClass.simpleName}\n${e.message}"
    } finally {
        connection?.disconnect()
    }
}
