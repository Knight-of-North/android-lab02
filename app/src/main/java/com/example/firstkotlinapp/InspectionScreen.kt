package com.example.firstkotlinapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

// 个人标识：姓名、学号后四位、当堂验收码。三个值只在本机显示，不提交到接口。
private const val INSPECTOR_NAME = "舒飞"
private const val STUDENT_SUFFIX = "5060"
private const val CHECK_CODE = "1106"

@Composable
fun InspectionHome(onEnter: () -> Unit) {
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.safeDrawingPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("校园巡检", style = MaterialTheme.typography.headlineMedium)
            Text("实验四 · Compose与网络进阶")
            Text("从网络JSON到任务列表，观察加载、空数据、错误与取消。")
            Button(onClick = onEnter, modifier = Modifier.testTag("enter")) { Text("进入任务页") }
        }
    }
}

@Composable
fun InspectionScreen(controller: InspectionController, onBack: () -> Unit) {
    val state = controller.state
    val loading = state.phase == LoadPhase.LOADING
    var pendingOnly by remember { mutableStateOf(false) }
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.safeDrawingPadding().padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("巡检任务", style = MaterialTheme.typography.headlineSmall)
                TextButton(onClick = onBack, modifier = Modifier.testTag("back")) { Text("返回首页") }
            }
            Text("巡检员：$INSPECTOR_NAME · 学号尾号：$STUDENT_SUFFIX", style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("identity"))
            Text("课堂验收码：$CHECK_CODE", style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("checkCode"))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("A203", "A204").forEach { room ->
                    FilterChip(selected = controller.room == room, enabled = !loading,
                        onClick = { controller.selectRoom(room) }, label = { Text(room) },
                        modifier = Modifier.testTag("room_$room"))
                }
            }
            Text("教学测试场景", style = MaterialTheme.typography.labelMedium)
            DemoScenario.entries.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { scenario ->
                        FilterChip(selected = controller.scenario == scenario, enabled = !loading,
                            onClick = { controller.selectScenario(scenario) },
                            label = { Text(scenario.label) },
                            modifier = Modifier.testTag("scenario_${scenario.value}"))
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(enabled = !loading, onClick = { controller.load() }, modifier = Modifier.testTag("load")) {
                    Text("加载 ${controller.room}")
                }
                if (loading) OutlinedButton(onClick = { controller.cancel() }, modifier = Modifier.testTag("cancel")) {
                    Text("取消请求")
                }
            }
            HorizontalDivider()
            when (state.phase) {
                LoadPhase.INITIAL -> Text(state.message.ifEmpty { "请选择实验室并加载任务" }, Modifier.testTag("initial"))
                LoadPhase.LOADING -> {
                    LinearProgressIndicator(Modifier.fillMaxWidth().testTag("loading"))
                    Text("正在加载 ${controller.room}，请稍候…")
                }
                LoadPhase.EMPTY -> Text("${controller.room} 暂无巡检任务", Modifier.testTag("empty"))
                LoadPhase.ERROR -> {
                    Text("加载失败：${state.message}", color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("error"))
                    OutlinedButton(onClick = { controller.load(retry = true) }, modifier = Modifier.testTag("retry")) { Text("重试") }
                }
                LoadPhase.SUCCESS -> {
                    // TODO 3：从任务列表计算已完成数量，不能写死。
                    val done = state.tasks.count { it.status == "已完成" }
                    Text("${controller.room} · 共${state.tasks.size}项 · 待巡检${state.tasks.size - done}项 · 已完成${done}项",
                        modifier = Modifier.testTag("summary"), style = MaterialTheme.typography.titleSmall)
                    // TODO 6：增加“全部／仅看待巡检”按钮，更新pendingOnly。
                    // 根据pendingOnly筛选state.tasks，计算visibleTasks，并显示当前显示数量。
                    // 筛选只改变展示列表，顶部统计仍基于完整state.tasks；切换选项不重新请求网络。
                    val visibleTasks = if (pendingOnly) state.tasks.filter { it.status == "待巡检" } else state.tasks
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !pendingOnly,
                            onClick = { pendingOnly = false },
                            label = { Text("全部") },
                            modifier = Modifier.testTag("filter_all")
                        )
                        FilterChip(
                            selected = pendingOnly,
                            onClick = { pendingOnly = true },
                            label = { Text("仅看待巡检") },
                            modifier = Modifier.testTag("filter_pending")
                        )
                    }
                    Text(
                        "当前显示 ${visibleTasks.size} 项",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("visibleCount")
                    )
                    // TODO 4：使用LazyColumn、items和InspectionTaskCard展示visibleTasks。
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(visibleTasks, key = { it.id }) { task ->
                            InspectionTaskCard(task)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InspectionTaskCard(task: InspectionTask) {
    Card(Modifier.fillMaxWidth().testTag("task_${task.id}")) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // TODO 5：用Row组织名称和状态；名称用weight(1f)；下方显示编号和位置。
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // weight(1f) 让名称占用剩余宽度并在不足时换行，状态始终保留自己的宽度不被挤出屏幕。
                Text(
                    task.deviceName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    task.status,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (task.status == "已完成") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "${task.id} · ${task.location}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
