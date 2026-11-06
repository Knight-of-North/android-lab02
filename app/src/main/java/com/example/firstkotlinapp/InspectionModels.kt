package com.example.firstkotlinapp

data class InspectionTask(
    val id: String,
    val deviceName: String,
    val location: String,
    val status: String
)

enum class LoadPhase { INITIAL, LOADING, SUCCESS, EMPTY, ERROR }

data class InspectionUiState(
    val phase: LoadPhase = LoadPhase.INITIAL,
    val tasks: List<InspectionTask> = emptyList(),
    val message: String = ""
)

enum class DemoScenario(val value: String, val label: String) {
    NORMAL("normal", "正常"), EMPTY("empty", "空数据"),
    ERROR("error", "500错误"), RETRY("retry", "失败后重试"),
    SLOW("slow", "延迟"), MALFORMED("malformed", "格式错误")
}
