package upeu.edu.pe.Data.remote.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import upeu.edu.pe.Data.repository.DataStoreSessionStorage

@Composable
actual fun rememberSessionManager(): SessionManager {
    val context = LocalContext.current
    return remember { SessionManager(DataStoreSessionStorage(context)) }
}
