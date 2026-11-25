package upeu.edu.pe.Data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DiaryEntryCreateDto(
    val entryDate: String,
    val text: String
)

@Serializable
data class DiaryEntryResponseDto(
    val id: Long,
    val entryDate: String,
    val text: String
)
