package com.echo.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.echo.app.presentation.archive.ArchiveScreen
import com.echo.app.presentation.recall.RecallScreen
import com.echo.app.presentation.timeline.TimelineScreen
import com.echo.app.presentation.today.TodayScreen

object Destination {
    const val Today = "today"
    const val Timeline = "timeline"
    const val Archive = "archive"
    const val Recall = "recall"
    fun recall(id: String) = "$Recall/$id"
}

@Composable
fun EchoNavHost(
    navController: NavHostController,
    onFirstMemorySaved: () -> Unit,
    initialRoute: String = Destination.Today,
    modifier: Modifier = Modifier,
) {
    NavHost(navController, startDestination = initialRoute, modifier = modifier) {
        composable(Destination.Today) {
            TodayScreen(onRecall = { navController.navigate(Destination.recall(it)) }, onFirstMemorySaved = onFirstMemorySaved)
        }
        composable(Destination.Timeline) { TimelineScreen(onMemoryClick = { navController.navigate(Destination.recall(it)) }) }
        composable(Destination.Archive) { ArchiveScreen() }
        composable("${Destination.Recall}/{memoryId}") { entry ->
            RecallScreen(memoryId = entry.arguments?.getString("memoryId").orEmpty(), onFinished = { navController.popBackStack() })
        }
    }
}
