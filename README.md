# Android 实验二：网络访问与 HTTP 明文策略

在实验一 Kotlin Compose 项目基础上，完成一次真实网络请求，观察 HTTP 明文配置前后的差异，并使用 Git 完成两次本地提交与远程推送。

## 实验目标

- 定位项目代码、权限文件与运行设备，区分网络权限与明文配置两个概念
- 验证 Android 9.0+（API 28+）默认禁止 HTTP 明文通信的机制
- 在模拟器中发起 HTTPS / HTTP 请求，对比配置前后的结果差异
- 掌握 Git 本地提交（Commit）与远程推送（Push）的标准工程流程

## 运行方式

1. 使用 Android Studio 打开本工程，等待 Gradle 同步完成
2. 启动 Android 模拟器（本项目在 Medium Phone / Android 17 / API 37 上验证通过）
3. 点击顶部 Run 按钮，将 App 安装到模拟器
4. 界面提供「HTTPS 请求」「HTTP 请求」两个按钮，点击后可查看请求地址、状态码与响应正文

## 测试接口地址

- HTTPS：`https://httpbin.org/get?room=A203`
- HTTP：`http://httpbin.org/get?room=A203`

## 配置说明

网络安全配置位于 `app/src/main/res/xml/network_security_config.xml`：

- `<base-config cleartextTrafficPermitted="false" />`：全局不放行明文通信
- `<domain-config>`：仅针对测试域名 `httpbin.org` 调整，用于对比 HTTP 请求的拦截与放行两种状态

> **HTTP 仅用于教学对比，实际服务优先 HTTPS。**
