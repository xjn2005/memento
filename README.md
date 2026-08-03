# Echo

Echo 是一款本地优先的 Android 未来记忆工具。记录当下的一句话，Echo 会根据温和的遗忘曲线，在未来适当的时候把它带回眼前。

## 已实现的首版范围

- 今日内联记录与可选标签
- Room 本地存储、低密度时光轴与归档
- 每条记忆独立的 WorkManager 回顾调度
- 三种回顾反馈：仍然重要、已经改变、不再需要
- 浅色/深色暖调主题与克制的淡入反馈
- 纯 Kotlin 遗忘算法单元测试

## 本地运行

需要 Android Studio（或命令行 Android SDK）、JDK 21，以及一个 Android 35 平台：

```powershell
.\gradlew.bat :app:assembleDebug
```

安装到模拟器或实体设备后，从「今日」写入第一条内容。Android 13+ 会在首次保存后请求通知权限；拒绝权限不影响记录与回顾页的使用。

## 设计资料

- [产品与技术设计](docs/superpowers/specs/2026-07-30-echo-design.md)
- [实现计划](docs/superpowers/plans/2026-07-30-echo-android.md)
