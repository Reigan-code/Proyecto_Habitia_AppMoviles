package upeu.edu.pe.Data.remote.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberSessionManager(): SessionManager {
    return remember { SessionManager(NoopSessionStorage()) }
}
