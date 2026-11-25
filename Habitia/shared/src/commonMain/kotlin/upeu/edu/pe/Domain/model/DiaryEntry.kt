package upeu.edu.pe.Domain.model

data class DiaryEntry(
    val id: String = "",
    val date: String = "",   // ISO "yyyy-MM-dd"
    val text: String = ""
)
