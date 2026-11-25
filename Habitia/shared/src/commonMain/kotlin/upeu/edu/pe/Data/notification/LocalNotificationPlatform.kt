package upeu.edu.pe.Data.notification

// Contrato de notificaciones locales (patrón expect/actual de KMP).
// commonMain solo conoce ESTA interfaz; Android e iOS la implementan.
interface LocalNotificationPlatform {
    suspend fun showTestNotification(): Result<Unit>
    suspend fun showAlert(title: String, message: String): Result<Unit>
}

// Factory actual por plataforma (se implementa en androidMain / iosMain).
expect fun createLocalNotificationPlatform(): LocalNotificationPlatform
