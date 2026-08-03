# Echo Journal Home Redesign Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the current card-based home screen with a single-focus Apple Journal-inspired writing page.

**Architecture:** Keep `TodayViewModel` and save behavior unchanged. Replace only `TodayScreen` and `WritingPaper`, using the existing semantic Compose theme and a keyboard-aware layout.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Gradle, Android Emulator.

---

### Task 1: Rebuild the journal writing surface

**Files:**
- Modify: `app/src/main/java/com/echo/app/presentation/components/WritingPaper.kt`
- Modify: `app/src/main/java/com/echo/app/presentation/today/TodayScreen.kt`

- [ ] **Step 1: Make the editor a lined page instead of a card**

```kotlin
Column(Modifier.fillMaxWidth()) {
  HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
  BasicTextField(modifier = Modifier.heightIn(min = 250.dp), ...)
  HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}
```

- [ ] **Step 2: Use the specified compact journal hierarchy**

```kotlin
Text("今天 · $date", style = MaterialTheme.typography.labelLarge)
Text("留给未来的一句话", style = MaterialTheme.typography.titleLarge)
Text("不必写得完整，真实就好。", color = MaterialTheme.colorScheme.onSurfaceVariant)
```

- [ ] **Step 3: Hide tag controls until text exists**

```kotlin
if (content.isNotBlank()) {
  TextButton(onClick = { showTag = !showTag }) { Text("添加标签") }
}
```

- [ ] **Step 4: Verify compilation**

Run: `./gradlew.bat :app:compileDebugKotlin`
Expected: `BUILD SUCCESSFUL`.

### Task 2: Package and inspect the redesign

**Files:**
- Modify: `app/src/main/java/com/echo/app/presentation/today/TodayScreen.kt`

- [ ] **Step 1: Keep disabled and enabled completion states accessible**

Use `TextButton(enabled = content.isNotBlank())` and retain the existing save callback.

- [ ] **Step 2: Run regression and packaging checks**

Run: `./gradlew.bat :app:testDebugUnitTest :app:assembleDebug`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Install the APK on emulator-5554**

Run: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
Expected: `Success`.

- [ ] **Step 4: Manually inspect the empty and typing states**

Confirm there is no large title, white card, visible tag control in empty state, or loss of content after tapping `完成`.
