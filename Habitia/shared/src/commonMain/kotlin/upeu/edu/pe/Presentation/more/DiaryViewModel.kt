package upeu.edu.pe.Presentation.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import upeu.edu.pe.Domain.model.DiaryEntry
import upeu.edu.pe.Domain.repository.DiaryRepository

class DiaryViewModel(private val repo: DiaryRepository) : ViewModel() {
    private val _entries = MutableStateFlow<List<DiaryEntry>>(emptyList())
    val entries: StateFlow<List<DiaryEntry>> = _entries.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            repo.refresh()
            repo.getEntries().collect { _entries.value = it }
        }
    }

    fun addToday(text: String = "") {
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
            repo.addEntry(today, text)
            refresh()
        }
    }

    fun update(id: String, text: String) {
        viewModelScope.launch { repo.updateEntry(id, text) }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repo.deleteEntry(id)
            refresh()
        }
    }
}
