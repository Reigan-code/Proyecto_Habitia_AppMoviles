package upeu.edu.pe.Data.notification

// iOS: las notificaciones locales se dejan como no-op por ahora (sin UNUserNotificationCenter).
class IosLocalNotificationPlatform : LocalNotificationPlatform {
    override suspend fun showTestNotification(): Result<Unit> = Result.success(Unit)
    override suspend fun showAlert(title: String, message: String): Result<Unit> = Result.success(Unit)
}

actual fun createLocalNotificationPlatform(): LocalNotificationPlatform = IosLocalNotificationPlatform()
