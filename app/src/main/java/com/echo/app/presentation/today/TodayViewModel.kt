package com.echo.app.presentation.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.app.domain.model.Memory
import com.echo.app.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodayViewModel(private val repository: MemoryRepository) : ViewModel() {
    val active: StateFlow<List<Memory>> = repository.observeActive().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val due: StateFlow<List<Memory>> = active.map { memories ->
        val now = System.currentTimeMillis()
        memories.filter { it.nextReviewAt <= now }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun save(content: String, tag: String?, onSuccess: () -> Unit) = viewModelScope.launch {
        if (content.isNotBlank()) {
            repository.create(content, tag, System.currentTimeMillis())
            onSuccess()
        }
    }
}
