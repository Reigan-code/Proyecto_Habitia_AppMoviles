package upeu.edu.pe.Domain.repository

import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Domain.model.DiaryEntry

interface DiaryRepository {
    fun getEntries(): Flow<List<DiaryEntry>>
    suspend fun addEntry(date: String, text: String): DiaryEntry?
    suspend fun updateEntry(id: String, text: String)
    suspend fun deleteEntry(id: String)
    suspend fun refresh()
}
