package upeu.edu.pe.Presentation.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.dto.BalanceDto
import upeu.edu.pe.Data.remote.dto.PodiumEntryDto
import upeu.edu.pe.Data.remote.dto.RewardDto
import upeu.edu.pe.Data.remote.dto.WeeklySummaryDto

class AnalyticsViewModel(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : ViewModel() {
    private val _podium = MutableStateFlow<List<PodiumEntryDto>>(emptyList())
    val podium: StateFlow<List<PodiumEntryDto>> = _podium.asStateFlow()

    private val _weekly = MutableStateFlow(WeeklySummaryDto())
    val weekly: StateFlow<WeeklySummaryDto> = _weekly.asStateFlow()

    private val _balance = MutableStateFlow(BalanceDto())
    val balance: StateFlow<BalanceDto> = _balance.asStateFlow()

    private val _rewards = MutableStateFlow<List<RewardDto>>(emptyList())
    val rewards: StateFlow<List<RewardDto>> = _rewards.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun refresh() {
        val token = session.state.value.token ?: return
        viewModelScope.launch {
            _loading.value = true
            try {
                val base = ApiClient.getBaseUrl()
                val p = httpClient.get("$base/Analytics/podium") { bearerAuth(token) }
                if (p.status.isSuccess()) _podium.value = p.body()
                val w = httpClient.get("$base/Analytics/weekly") { bearerAuth(token) }
                if (w.status.isSuccess()) _weekly.value = w.body()
                val b = httpClient.get("$base/Analytics/balance") { bearerAuth(token) }
                if (b.status.isSuccess()) _balance.value = b.body()
                val r = httpClient.get("$base/Analytics/rewards") { bearerAuth(token) }
                if (r.status.isSuccess()) _rewards.value = r.body()
            } catch (e: Exception) {
                println("[Habitia-Analytics] ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
}
