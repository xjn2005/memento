package com.echo.app.presentation.archive

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.echo.app.EchoApplication
import com.echo.app.domain.model.Memory
import com.echo.app.presentation.formatMemoryDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private class ArchiveViewModel(private val app: EchoApplication) : ViewModel() {
    val memories = app.container.memoryRepository.observeArchived().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun reactivate(id: String) = viewModelScope.launch { app.container.memoryRepository.reactivate(id, System.currentTimeMillis()) }
}

@Composable
fun ArchiveScreen() {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as EchoApplication
    val model: ArchiveViewModel = viewModel(factory = viewModelFactory { initializer { ArchiveViewModel(app) } })
    val memories by model.memories.collectAsState()
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp)) {
        Text("归档", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text("不再打扰，但也不必遗失。", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 7.dp, bottom = 22.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(memories, key = Memory::id) { memory ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {
                        Text(formatMemoryDate(memory.createdAt), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(memory.content, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
                        TextButton(onClick = { model.reactivate(memory.id) }, modifier = Modifier.padding(top = 6.dp)) {
                            Text("重新留在时光里", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
