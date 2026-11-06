package com.example.firstkotlinapp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID

class InspectionController(private val scope: CoroutineScope) {
    var state by mutableStateOf(InspectionUiState())
        private set
    var room by mutableStateOf("A203")
        private set
    var scenario by mutableStateOf(DemoScenario.NORMAL)
        private set
    private var job: Job? = null
    private var requestKey = ""

    fun selectRoom(value: String) {
        if (state.phase == LoadPhase.LOADING) return
        room = value
        state = InspectionUiState()
    }

    fun selectScenario(value: DemoScenario) {
        if (state.phase == LoadPhase.LOADING) return
        scenario = value
        state = InspectionUiState()
    }

    fun load(retry: Boolean = false) {
        if (state.phase == LoadPhase.LOADING) return
        if (!retry) requestKey = UUID.randomUUID().toString()
        state = InspectionUiState(LoadPhase.LOADING)
        job = scope.launch {
            try {
                val tasks = loadInspectionTasks(room, scenario, requestKey)
                // TODO 2：根据tasks是否为空设置EMPTY或SUCCESS，并将tasks放入state。
                state = InspectionUiState(message = "TODO 2：请完成请求成功后的状态更新")
                Log.i("InspectionLab", "APPLY_RESULT $room ${tasks.size}")
            } catch (e: CancellationException) {
                Log.i("InspectionLab", "REQUEST_CANCELLED $room")
                throw e
            } catch (e: Exception) {
                state = InspectionUiState(
                    LoadPhase.ERROR, message = e.message ?: e.javaClass.simpleName
                )
            }
        }
    }

    fun cancel() {
        job?.cancel()
        state = InspectionUiState(message = "请求已取消，可以重新加载")
    }
}
