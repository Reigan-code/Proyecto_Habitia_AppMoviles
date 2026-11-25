package upeu.edu.pe.Data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PodiumEntryDto(
    val position: Int = 0,
    val habitName: String = "",
    val completionCount: Long = 0,
    val reward: String = ""
)

@Serializable
data class WeeklySummaryDto(
    val tasksDone: Int = 0,
    val habitsDone: Int = 0,
    val mealsLogged: Int = 0,
    val remindersDone: Int = 0
)

@Serializable
data class BalanceDto(
    val productivity: Double = 0.0,
    val wellness: Double = 0.0,
    val balance: Double = 0.0
)

@Serializable
data class RewardDto(
    val code: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "",
    val unlocked: Boolean = false
)
