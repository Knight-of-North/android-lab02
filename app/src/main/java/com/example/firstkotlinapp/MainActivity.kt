package com.example.firstkotlinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF245B84))) {
                InspectionApp()
            }
        }
    }
}

@Composable
fun InspectionApp() {
    var inTaskPage by remember { mutableStateOf(false) }
    if (inTaskPage) {
        val scope = rememberCoroutineScope()
        val controller = remember(scope) { InspectionController(scope) }
        BackHandler { inTaskPage = false }
        InspectionScreen(controller, onBack = { inTaskPage = false })
    } else {
        InspectionHome(onEnter = { inTaskPage = true })
    }
}
