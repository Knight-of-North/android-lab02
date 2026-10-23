package com.example.firstkotlinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var result by remember { mutableStateOf("等待请求") }
                var busy by remember { mutableStateOf(false) }

                InspectionScreen("舒飞", "A203", 10, 3, result, busy) { room ->
                    busy = true
                    result = "请求中：$room"
                    Thread {
                        val text = fetch("https://httpbin.org/get?room=$room")
                        runOnUiThread {
                            result = text
                            busy = false
                        }
                    }.start()
                }
            }
        }
    }
}
