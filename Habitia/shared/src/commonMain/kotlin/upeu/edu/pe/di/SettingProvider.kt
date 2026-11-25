package upeu.edu.pe.di

import com.russhwolf.settings.Settings
import upeu.edu.pe.Data.remote.api.ApiClient

// SettingProvider — estilo profesor: expect fun createSettings(): Settings.
// Cada plataforma entrega su Settings real (Android SharedPreferences, iOS NSUserDefaults).
expect fun createSettings(): Settings

// Configuración global derivada: la URL del backend la resuelve ApiClient en
// tiempo de ejecución (10.10.10.x / 192.168.x / emulador).
object SettingProvider {
    val backendBaseUrl: String get() = ApiClient.getBaseUrl()
    val usesDeviceNetwork: Boolean get() = backendBaseUrl.contains("10.10") || backendBaseUrl.contains("192.168")
    const val NOTIFICATION_CHANNEL_ID = "habitial_local_notifications"
    const val TEST_NOTIFICATION_ID = 1001
}
