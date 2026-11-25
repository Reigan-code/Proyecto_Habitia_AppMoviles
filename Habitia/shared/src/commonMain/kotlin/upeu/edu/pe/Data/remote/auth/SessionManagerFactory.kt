package upeu.edu.pe.Data.remote.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
expect fun rememberSessionManager(): SessionManager
