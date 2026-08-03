package com.echo.app.presentation.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.echo.app.EchoApplication
import com.echo.app.domain.model.Memory
import com.echo.app.presentation.components.MemoryCard
import com.echo.app.presentation.monthOf
import com.echo.app.presentation.yearOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

private class TimelineViewModel(app: EchoApplication) : ViewModel() {
    val memories = app.container.memoryRepository.observeActive().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@Composable
fun TimelineScreen(onMemoryClick: (String) -> Unit) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as EchoApplication
    val model: TimelineViewModel = viewModel(factory = viewModelFactory { initializer { TimelineViewModel(app) } })
    val memories by model.memories.collectAsState()
    val groups = memories.groupBy { "${yearOf(it.createdAt)} ${monthOf(it.createdAt)}" }
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp)) {
        Text("时光", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text("留给未来的片段", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 7.dp, bottom = 18.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            groups.forEach { (heading, entries) ->
                item(key = heading) {
                    Text(
                        heading,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                    )
                }
                items(entries, key = Memory::id) { memory -> MemoryCard(memory, onClick = { onMemoryClick(memory.id) }) }
            }
        }
    }
}
