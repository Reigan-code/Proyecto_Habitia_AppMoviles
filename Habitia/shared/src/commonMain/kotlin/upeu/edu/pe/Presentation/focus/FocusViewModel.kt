package upeu.edu.pe.Presentation.focus


import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FocusViewModel {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    private val _timeLeftSeconds = MutableStateFlow(25 * 60)
    val timeLeftSeconds: StateFlow<Int> = _timeLeftSeconds

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _selectedPresetMinutes = MutableStateFlow(25)
    val selectedPresetMinutes: StateFlow<Int> = _selectedPresetMinutes

    fun setPreset(minutes: Int) {
        if (!_isRunning.value) {
            _selectedPresetMinutes.value = minutes
            _timeLeftSeconds.value = minutes * 60
        }
    }

    fun startTimer() {
        if (_isRunning.value) return
        _isRunning.value = true
        timerJob = scope.launch {
            while (_timeLeftSeconds.value > 0) {
                delay(1000)
                _timeLeftSeconds.value -= 1
            }
            _isRunning.value = false
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _isRunning.value = false
    }

    fun resetTimer() {
        pauseTimer()
        _timeLeftSeconds.value = _selectedPresetMinutes.value * 60
    }
}