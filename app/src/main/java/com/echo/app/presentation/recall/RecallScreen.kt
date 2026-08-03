package com.echo.app.presentation.recall

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.echo.app.EchoApplication
import com.echo.app.domain.model.RecallFeedback
import com.echo.app.presentation.formatMemoryDate

@Composable
fun RecallScreen(memoryId: String, onFinished: () -> Unit) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as EchoApplication
    val model: RecallViewModel = viewModel(factory = viewModelFactory { initializer { RecallViewModel(app.container.memoryRepository) } })
    val memory by model.memory.collectAsState()
    LaunchedEffect(memoryId) { model.load(memoryId) }
    val current = memory ?: return

    AnimatedVisibility(visible = true, enter = fadeIn()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onFinished) { Text("‹ 返回", color = MaterialTheme.colorScheme.primary) }
                Spacer(Modifier.weight(1f))
                Text("过去的自己", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(24.dp))
            Text(formatMemoryDate(current.createdAt), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(
                current.content,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp, lineHeight = 42.sp),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 12.dp),
            )
            Spacer(Modifier.weight(1f))
            Text("你还记得这句话吗？", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            RecallAction("仍然重要", isPrimary = true) { model.answer(current.id, RecallFeedback.Important, onFinished) }
            RecallAction("已经改变") { model.answer(current.id, RecallFeedback.Changed, onFinished) }
            RecallAction("不再需要") { model.answer(current.id, RecallFeedback.NoLongerNeeded, onFinished) }
        }
    }
}

@Composable
private fun RecallAction(label: String, isPrimary: Boolean = false, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isPrimary) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isPrimary) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.outlineVariant),
    ) {
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth().padding(vertical = 17.dp),
            color = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isPrimary) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
        )
    }
}
