package com.echo.app.presentation.recall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.app.domain.model.Memory
import com.echo.app.domain.model.RecallFeedback
import com.echo.app.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecallViewModel(private val repository: MemoryRepository) : ViewModel() {
    private val _memory = MutableStateFlow<Memory?>(null)
    val memory: StateFlow<Memory?> = _memory.asStateFlow()

    fun load(id: String) = viewModelScope.launch { _memory.value = repository.findById(id) }

    fun answer(id: String, feedback: RecallFeedback, onFinished: () -> Unit) = viewModelScope.launch {
        repository.applyFeedback(id, feedback, System.currentTimeMillis())
        onFinished()
    }
}
