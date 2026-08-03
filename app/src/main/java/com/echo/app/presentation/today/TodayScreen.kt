package com.echo.app.presentation.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.echo.app.EchoApplication
import com.echo.app.presentation.components.MemoryCard
import com.echo.app.presentation.components.WritingPaper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayScreen(onRecall: (String) -> Unit, onFirstMemorySaved: () -> Unit) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as EchoApplication
    val model: TodayViewModel = viewModel(factory = viewModelFactory { initializer { TodayViewModel(app.container.memoryRepository) } })
    val due by model.due.collectAsState()
    var content by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var showTag by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    val date = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年M月d日")) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("今天 · $date", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = {
                    model.save(content, tag) {
                        content = ""
                        tag = ""
                        showTag = false
                        saved = true
                        onFirstMemorySaved()
                    }
                },
                enabled = content.isNotBlank(),
            ) {
                Text("完成", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(44.dp))
        Text(
            "留给未来的一句话",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, lineHeight = 30.sp),
            fontWeight = FontWeight.Medium,
        )
        Text(
            "不必写得完整，真实就好。",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        Spacer(Modifier.height(38.dp))
        WritingPaper(value = content, onValueChange = { content = it })
        if (content.isNotBlank()) {
            TextButton(onClick = { showTag = !showTag }, modifier = Modifier.padding(top = 8.dp)) {
                Text(if (tag.isBlank()) "添加标签" else tag, color = MaterialTheme.colorScheme.primary)
            }
        }
        if (showTag) {
            OutlinedTextField(
                value = tag,
                onValueChange = { tag = it },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                placeholder = { Text("例如：学习") },
                singleLine = true,
            )
        }
        AnimatedVisibility(visible = saved, enter = fadeIn(), exit = fadeOut()) {
            Text("已替你留在这里。", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 12.dp))
        }
        if (due.isNotEmpty()) {
            Spacer(Modifier.height(18.dp))
            Text("过去的自己", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            MemoryCard(due.first(), onClick = { onRecall(due.first().id) }, modifier = Modifier.padding(top = 10.dp))
        }
    }
}
