# Echo iOS Visual Refresh Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace Echo's default Material visual treatment with a white-and-lake-blue iOS Journal-inspired interface without changing memory behavior.

**Architecture:** Keep screen state and domain code unchanged. Centralize the new semantic color system and typography in the Compose theme, then introduce focused presentation components for floating navigation, writing paper, section headers, and list rows.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Compose UI tests, Gradle.

---

### Task 1: Establish the semantic iOS visual system

**Files:**
- Modify: `app/src/main/java/com/echo/app/presentation/theme/Color.kt`
- Modify: `app/src/main/java/com/echo/app/presentation/theme/EchoTheme.kt`

- [ ] **Step 1: Replace warm neutrals with the specified white/lake-blue palette**

```kotlin
val AppCanvas = Color(0xFFF7FAFC)
val CardSurface = Color(0xFFFFFFFF)
val LakeBlue = Color(0xFF2F9FD8)
val LakeBlueSoft = Color(0xFFE9F6FC)
val Ink = Color(0xFF17212B)
val MutedInk = Color(0xFF718096)
val Hairline = Color(0xFFE8EEF2)
```

- [ ] **Step 2: Map the palette into Material semantic roles**

```kotlin
private val LightScheme = lightColorScheme(
  primary = LakeBlue, secondaryContainer = LakeBlueSoft,
  background = AppCanvas, surface = CardSurface,
  onBackground = Ink, onSurface = Ink, onSurfaceVariant = MutedInk,
  outlineVariant = Hairline,
)
```

- [ ] **Step 3: Compile the module**

Run: `./gradlew.bat :app:compileDebugKotlin`
Expected: `BUILD SUCCESSFUL`.

### Task 2: Replace default navigation and writing controls

**Files:**
- Modify: `app/src/main/java/com/echo/app/presentation/components/EchoScaffold.kt`
- Create: `app/src/main/java/com/echo/app/presentation/components/WritingPaper.kt`
- Modify: `app/src/main/java/com/echo/app/presentation/today/TodayScreen.kt`

- [ ] **Step 1: Create a borderless white writing surface**

```kotlin
@Composable
fun WritingPaper(value: String, onValueChange: (String) -> Unit) {
  Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface) {
    BasicTextField(value = value, onValueChange = onValueChange, modifier = Modifier.padding(22.dp))
  }
}
```

- [ ] **Step 2: Place Save in the top-right and tags in a quiet capsule**

Keep `TodayViewModel.save` unchanged. Use `TextButton` with primary lake-blue text for Save and `AssistChip`-like custom surface for the optional tag.

- [ ] **Step 3: Implement a floating capsule navigation bar**

```kotlin
Surface(shape = RoundedCornerShape(28.dp), shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
  Row { destinations.forEach { FloatingNavItem(...) } }
}
```

- [ ] **Step 4: Build Debug APK**

Run: `./gradlew.bat :app:assembleDebug`
Expected: `BUILD SUCCESSFUL`.

### Task 3: Refresh reading surfaces and verify behavior

**Files:**
- Modify: `app/src/main/java/com/echo/app/presentation/components/MemoryCard.kt`
- Modify: `app/src/main/java/com/echo/app/presentation/timeline/TimelineScreen.kt`
- Modify: `app/src/main/java/com/echo/app/presentation/archive/ArchiveScreen.kt`
- Modify: `app/src/main/java/com/echo/app/presentation/recall/RecallScreen.kt`

- [ ] **Step 1: Use white row surfaces with hairline dividers instead of generic elevated cards**

Use 20dp corners, zero card elevation, and `HorizontalDivider(color = outlineVariant)` after list content where separation improves scanning.

- [ ] **Step 2: Strengthen recall reading hierarchy**

Use a 30sp-equivalent headline for note text. Keep only `仍然重要` in `primary`; render other responses with `onSurfaceVariant`.

- [ ] **Step 3: Run regression and packaging checks**

Run: `./gradlew.bat :app:testDebugUnitTest :app:assembleDebug`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Manually verify on the emulator**

Open Echo, create a memory, inspect Today/Timeline/Archive in light and dark mode, then submit each recall response. Confirm saved content remains visible and the new visual treatment does not affect navigation or feedback.
