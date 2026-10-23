package com.example.firstkotlinapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun remainingDevices(total: Int, checked: Int): Int = total - checked

@Composable
fun InspectionScreen(
    inspector: String, room: String, total: Int, checked: Int,
    result: String, busy: Boolean, onRequest: (String) -> Unit
) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding()
            .navigationBarsPadding().padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("校园巡检")
        Text("巡检员：$inspector")
        Text("实验室：$room")
        Text("设备总数：$total")
        Text("已检查：$checked")
        Text("未检查：${remainingDevices(total, checked)}")
        Button(enabled = !busy, onClick = { onRequest(room) }) {
            Text("请求 $room")
        }
        Button(enabled = !busy, onClick = { onRequest("A204") }) {
            Text("请求 A204")
        }
        Text(result)
    }
}
