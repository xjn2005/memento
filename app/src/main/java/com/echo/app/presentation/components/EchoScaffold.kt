package com.echo.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.echo.app.presentation.navigation.Destination

@Composable
fun EchoScaffold(navController: NavHostController, content: @Composable (PaddingValues) -> Unit) {
    val route = navController.currentBackStackEntryAsState().value?.destination?.route
    androidx.compose.material3.Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (route?.startsWith(Destination.Recall) != true) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 10.dp,
                    ) {
                        Row(
                            modifier = Modifier.padding(5.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            listOf(
                                Destination.Today to "今日",
                                Destination.Timeline to "时光",
                                Destination.Archive to "归档",
                            ).forEach { (destination, label) ->
                                val selected = route == destination
                                TextButton(
                                    onClick = {
                                        navController.navigate(destination) {
                                            launchSingleTop = true
                                            restoreState = true
                                            popUpTo(Destination.Today) { saveState = true }
                                        }
                                    },
                                    modifier = Modifier.background(
                                        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(22.dp),
                                    ),
                                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 11.dp),
                                ) {
                                    Text(
                                        text = label,
                                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
    ) { padding -> content(padding) }
}
