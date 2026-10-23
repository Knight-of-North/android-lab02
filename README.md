# Android 实验三：工程结构与巡检页面整理

在实验二已能运行的 Kotlin Compose 工程上，把网络测试页整理成校园巡检页面：定位工程配置、修改应用名称、拆分源码文件、接入 OkHttp，并在设备上完成真实请求验证。

## 实验目标

- 按任务定位负责的文件，区分 SDK 路径、编译 API 级别、Gradle 版本与依赖坐标
- 把单文件示例拆为入口、页面、网络三个文件，明确各自职责
- 新增 OkHttp 依赖，并证明该依赖真正参与了运行时请求
- 通过参数变化（A203 / A204）与受控排错验证页面代码与网络代码的边界

## 运行方式

1. 使用 Android Studio 打开本工程，等待 Gradle 同步完成
2. 启动 Android 模拟器（本项目在 Medium Phone / Android 17 / API 37 上验证通过）
3. 点击 Run 安装运行；页面显示巡检员、实验室、设备总数与已检查数量
4. 点击「请求 A203」「请求 A204」，查看请求地址、HTTP 状态码与响应正文

## 起点状态

本条提交对应实验三开始前的可运行工程：应用名称仍为 `FirstKotlinApp`，页面代码与网络代码同处 `MainActivity.kt`，未引入 OkHttp。

## 实验二回顾：网络访问与 HTTP 明文策略

在实验一 Kotlin Compose 项目基础上，完成一次真实网络请求，观察 HTTP 明文配置前后的差异，并使用 Git 完成两次本地提交与远程推送。

- 验证 Android 9.0+（API 28+）默认禁止 HTTP 明文通信的机制
- 在模拟器中发起 HTTPS / HTTP 请求，对比配置前后的结果差异
- 掌握 Git 本地提交（Commit）与远程推送（Push）的标准工程流程

网络安全配置位于 `app/src/main/res/xml/network_security_config.xml`：

- `<base-config cleartextTrafficPermitted="false" />`：全局不放行明文通信
- `<domain-config>`：仅针对测试域名 `httpbin.org` 调整，用于对比 HTTP 请求的拦截与放行两种状态

> **HTTP 仅用于教学对比，实际服务优先 HTTPS。**
