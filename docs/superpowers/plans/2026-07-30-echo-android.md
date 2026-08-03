# Echo Android Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build Echo, an offline-first Android app that helps users rediscover personal notes according to a gentle forgetting-curve schedule.

**Architecture:** A Kotlin single-activity Compose app uses Clean Architecture boundaries. Domain owns memory scheduling rules; data owns Room and WorkManager; presentation owns stateful Compose screens and navigation.

**Tech Stack:** Kotlin, Jetpack Compose Material 3, Room, Coroutines/Flow, WorkManager, Navigation Compose, JUnit, Compose UI Test.

---

## File structure

`app/` contains the Android module and manifest. Under `com.echo.app`, `domain` contains pure models/use cases; `data` contains Room and scheduling implementations; `presentation` contains theme, reusable components, screens and ViewModels. Tests mirror domain/data/presentation packages.

### Task 1: Create the Android build foundation

**Files:**
- Create: `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`
- Create: `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Define the project and Android application module**

```kotlin
// settings.gradle.kts
pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral() } }
rootProject.name = "Echo"
include(":app")
```

```kotlin
// app/build.gradle.kts — add Compose, Room KSP, WorkManager and Navigation dependencies
plugins { alias(libs.plugins.android.application); alias(libs.plugins.kotlin.android); alias(libs.plugins.kotlin.compose); alias(libs.plugins.ksp) }
android { namespace = "com.echo.app"; compileSdk = 36
  defaultConfig { applicationId = "com.echo.app"; minSdk = 26; targetSdk = 36; versionCode = 1; versionName = "1.0.0" }
  buildFeatures { compose = true }
}
```

- [ ] **Step 2: Add the app manifest with notification permission and launch activity**

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
  <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
  <application android:theme="@style/Theme.Echo" android:label="Echo">
    <activity android:name=".MainActivity" android:exported="true">
      <intent-filter><action android:name="android.intent.action.MAIN"/><category android:name="android.intent.category.LAUNCHER"/></intent-filter>
    </activity>
  </application>
</manifest>
```

- [ ] **Step 3: Build the empty project**

Run: `./gradlew.bat :app:assembleDebug`
Expected: `BUILD SUCCESSFUL`.

### Task 2: Implement and test the pure forgetting domain

**Files:**
- Create: `app/src/main/java/com/echo/app/domain/model/Memory.kt`
- Create: `app/src/main/java/com/echo/app/domain/algorithm/ForgettingCurve.kt`
- Create: `app/src/test/java/com/echo/app/domain/algorithm/ForgettingCurveTest.kt`

- [ ] **Step 1: Write the scheduling tests**

```kotlin
@Test fun initialReview_isScheduledTwoDaysLater() {
  assertEquals(2, curve.nextIntervalDays(3.0))
}
@Test fun importantFeedback_increasesStability() {
  assertEquals(7.2, curve.adjustStability(3.0, RecallFeedback.Important), 0.001)
}
@Test fun intervalIsNeverLessThanOneDay() {
  assertEquals(1, curve.nextIntervalDays(0.1))
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "*ForgettingCurveTest"`
Expected: FAIL because `ForgettingCurve` does not exist.

- [ ] **Step 3: Implement the immutable model and curve**

```kotlin
enum class MemoryStatus { Active, Archived }
enum class RecallFeedback { Important, Changed, NoLongerNeeded }

class ForgettingCurve(private val threshold: Double = 0.56) {
  fun nextIntervalDays(stabilityDays: Double): Int =
    ceil(-ln(threshold) * stabilityDays).toInt().coerceAtLeast(1)
  fun adjustStability(current: Double, feedback: RecallFeedback): Double = when (feedback) {
    RecallFeedback.Important -> (current * 2.4).coerceAtMost(180.0)
    RecallFeedback.Changed -> (current * 1.25).coerceAtMost(90.0)
    RecallFeedback.NoLongerNeeded -> current
  }
}
```

- [ ] **Step 4: Re-run the unit test**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "*ForgettingCurveTest"`
Expected: PASS.

### Task 3: Add Room persistence and repository operations

**Files:**
- Create: `app/src/main/java/com/echo/app/data/local/MemoryEntity.kt`
- Create: `app/src/main/java/com/echo/app/data/local/MemoryDao.kt`
- Create: `app/src/main/java/com/echo/app/data/local/EchoDatabase.kt`
- Create: `app/src/main/java/com/echo/app/domain/repository/MemoryRepository.kt`
- Create: `app/src/main/java/com/echo/app/data/repository/RoomMemoryRepository.kt`
- Create: `app/src/androidTest/java/com/echo/app/data/local/MemoryDaoTest.kt`

- [ ] **Step 1: Write the DAO ordering and archive filtering tests**

```kotlin
@Test fun activeMemories_areNewestFirst() = runTest {
  dao.insert(old); dao.insert(new)
  assertEquals(listOf(new.id, old.id), dao.observeActive().first().map { it.id })
}
@Test fun archivedMemory_doesNotAppearInActiveResults() = runTest {
  dao.insert(archived)
  assertTrue(dao.observeActive().first().isEmpty())
}
```

- [ ] **Step 2: Implement a focused Room schema and DAO**

```kotlin
@Entity(tableName = "memories")
data class MemoryEntity(
 @PrimaryKey val id: String, val content: String, val tag: String?, val createdAt: Long,
 val lastReviewedAt: Long?, val stabilityDays: Double, val nextReviewAt: Long,
 val status: String, val reviewCount: Int
)
@Dao interface MemoryDao {
 @Query("SELECT * FROM memories WHERE status = 'ACTIVE' ORDER BY createdAt DESC") fun observeActive(): Flow<List<MemoryEntity>>
 @Query("SELECT * FROM memories WHERE status = 'ARCHIVED' ORDER BY createdAt DESC") fun observeArchived(): Flow<List<MemoryEntity>>
 @Query("SELECT * FROM memories WHERE status = 'ACTIVE' AND nextReviewAt <= :now ORDER BY nextReviewAt") fun observeDue(now: Long): Flow<List<MemoryEntity>>
 @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(memory: MemoryEntity)
}
```

- [ ] **Step 3: Run instrumented DAO tests**

Run: `./gradlew.bat :app:connectedDebugAndroidTest`
Expected: PASS on an emulator/device.

### Task 4: Schedule independent recall notifications

**Files:**
- Create: `app/src/main/java/com/echo/app/data/notification/RecallWorker.kt`
- Create: `app/src/main/java/com/echo/app/data/notification/RecallScheduler.kt`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Implement unique one-time scheduling**

```kotlin
fun schedule(memory: Memory) {
 val delay = (memory.nextReviewAt - clock.millis()).coerceAtLeast(0)
 val request = OneTimeWorkRequestBuilder<RecallWorker>()
   .setInitialDelay(delay, TimeUnit.MILLISECONDS)
   .setInputData(workDataOf("memory_id" to memory.id))
   .build()
 workManager.enqueueUniqueWork("recall-${memory.id}", ExistingWorkPolicy.REPLACE, request)
}
fun cancel(id: String) = workManager.cancelUniqueWork("recall-$id")
```

- [ ] **Step 2: Make feedback and archive updates reschedule/cancel tasks**

Update repository use cases so active memories invoke `schedule`; archive invokes `cancel`; reactivation invokes `schedule`.

- [ ] **Step 3: Verify scheduling manually**

Run the app, create a short-delay debug memory, inspect Android Studio App Inspection → Background Task Inspector.
Expected: one unique WorkManager request per active memory.

### Task 5: Build the calm Compose application shell and theme

**Files:**
- Create: `app/src/main/java/com/echo/app/MainActivity.kt`
- Create: `app/src/main/java/com/echo/app/presentation/theme/Color.kt`
- Create: `app/src/main/java/com/echo/app/presentation/theme/EchoTheme.kt`
- Create: `app/src/main/java/com/echo/app/presentation/navigation/EchoNavHost.kt`
- Create: `app/src/main/java/com/echo/app/presentation/components/EchoScaffold.kt`

- [ ] **Step 1: Implement warm light/dark color schemes**

```kotlin
val WarmCanvas = Color(0xFFFAF8F5)
val Ink = Color(0xFF252422)
val MistTeal = Color(0xFF577B78)
val DarkCanvas = Color(0xFF171716)
```

- [ ] **Step 2: Implement three destination navigation**

```kotlin
sealed class Destination(val route: String, val label: String) {
 data object Today : Destination("today", "今日")
 data object Timeline : Destination("timeline", "时光")
 data object Archive : Destination("archive", "归档")
}
```

- [ ] **Step 3: Build and manually inspect both system themes**

Run: `./gradlew.bat :app:assembleDebug`
Expected: PASS; app opens on Today with no default purple Material styling.

### Task 6: Implement Today, Timeline and Archive screens

**Files:**
- Create: `app/src/main/java/com/echo/app/presentation/today/TodayViewModel.kt`
- Create: `app/src/main/java/com/echo/app/presentation/today/TodayScreen.kt`
- Create: `app/src/main/java/com/echo/app/presentation/timeline/TimelineScreen.kt`
- Create: `app/src/main/java/com/echo/app/presentation/archive/ArchiveScreen.kt`
- Create: `app/src/androidTest/java/com/echo/app/presentation/today/TodayScreenTest.kt`

- [ ] **Step 1: Write the save interaction test**

```kotlin
composeRule.onNodeWithText("你想留下什么？").assertExists()
composeRule.onNodeWithText("保存").assertIsNotEnabled()
composeRule.onNodeWithText("你想留下什么？").performTextInput("今天很安静。")
composeRule.onNodeWithText("保存").performClick()
verify { addMemory("今天很安静。", null) }
```

- [ ] **Step 2: Implement the inline recorder and low-density cards**

Use `OutlinedTextField` with a transparent container, a 48dp `TextButton` save target and `AnimatedVisibility` fade for confirmation. Group timeline content by local year/month. Keep each card to date, preview, tag and next-review copy.

- [ ] **Step 3: Run screen test**

Run: `./gradlew.bat :app:connectedDebugAndroidTest`
Expected: PASS.

### Task 7: Implement recall flow, permission handling and polish

**Files:**
- Create: `app/src/main/java/com/echo/app/presentation/recall/RecallViewModel.kt`
- Create: `app/src/main/java/com/echo/app/presentation/recall/RecallScreen.kt`
- Modify: `app/src/main/java/com/echo/app/MainActivity.kt`

- [ ] **Step 1: Add recall feedback actions**

```kotlin
fun answer(feedback: RecallFeedback) = viewModelScope.launch {
 repository.applyFeedback(memoryId, feedback, clock.millis())
 if (feedback == RecallFeedback.NoLongerNeeded) scheduler.cancel(memoryId)
 else scheduler.schedule(repository.requireMemory(memoryId))
}
```

- [ ] **Step 2: Request notification permission only after first save**

Use `rememberLauncherForActivityResult(RequestPermission())` on Android 13+, after a successful first entry. If denied, show a one-line system-settings hint, not a blocking dialog.

- [ ] **Step 3: Final verification**

Run: `./gradlew.bat :app:testDebugUnitTest :app:assembleDebug`
Expected: `BUILD SUCCESSFUL`.

Manually verify empty state, long Chinese content, dark mode, archive/reactivation, each feedback result, and a notification tap opening recall.
