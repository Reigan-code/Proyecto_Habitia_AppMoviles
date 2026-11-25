package upeu.edu.pe.Data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import upeu.edu.pe.di.SettingProvider

// Implementación ANDROID de LocalNotificationPlatform.
// Muestra notificaciones en la bandeja; respeta permiso de Android 13+ y
// canal de Android 8+. El PendingIntent abre MainActivity al tocar.
class AndroidLocalNotificationPlatform(private val context: Context) : LocalNotificationPlatform {

    override suspend fun showTestNotification(): Result<Unit> = runCatching {
        checkPermissionOrThrow()
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) {
            throw SecurityException("Las notificaciones están bloqueadas en Ajustes de Android")
        }
        createNotificationChannel()
        manager.notify(
            SettingProvider.TEST_NOTIFICATION_ID,
            buildNotification("Habitia", "¡Las notificaciones locales están funcionando!")
        )
    }

    override suspend fun showAlert(title: String, message: String): Result<Unit> = runCatching {
        checkPermissionOrThrow()
        createNotificationChannel()
        NotificationManagerCompat.from(context).notify(title.hashCode(), buildNotification(title, message))
    }

    private fun checkPermissionOrThrow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException("Concede el permiso de notificaciones")
        }
    }

    private fun buildNotification(title: String, message: String): android.app.Notification {
        // No referenciamos MainActivity desde shared (dependencia inversa):
        // usamos el intent de lanzado del propio paquete.
        val openApp = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        } ?: Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val pending = PendingIntent.getActivity(
            context, 0, openApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, SettingProvider.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SettingProvider.NOTIFICATION_CHANNEL_ID,
                "Notificaciones de Habitia",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Alertas de hábitos, tareas y recordatorios" }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}

actual fun createLocalNotificationPlatform(): LocalNotificationPlatform =
    AndroidLocalNotificationPlatform(AndroidAppContext.context)
