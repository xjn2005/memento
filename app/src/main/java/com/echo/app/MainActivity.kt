package com.echo.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.echo.app.presentation.components.EchoScaffold
import com.echo.app.presentation.navigation.Destination
import com.echo.app.presentation.navigation.EchoNavHost
import com.echo.app.presentation.theme.EchoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialRecallId = intent.getStringExtra(RecallMemoryIdKey)
        setContent {
            val notificationPermission = rememberLauncherForActivityResultCompat()
            EchoTheme(darkTheme = isSystemInDarkTheme()) {
                val navController = rememberNavController()
                EchoScaffold(navController) { padding ->
                    EchoNavHost(
                        navController = navController,
                        initialRoute = initialRecallId?.let(Destination::recall) ?: Destination.Today,
                        onFirstMemorySaved = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }

    companion object { const val RecallMemoryIdKey = "recall_memory_id" }
}

@androidx.compose.runtime.Composable
private fun rememberLauncherForActivityResultCompat() = androidx.activity.compose.rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = {},
)
