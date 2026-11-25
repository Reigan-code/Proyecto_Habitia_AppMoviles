package upeu.edu.pe.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import upeu.edu.pe.Data.notification.AndroidAppContext

actual fun createSettings(): Settings {
    val ctx: Context = AndroidAppContext.context
    return SharedPreferencesSettings(ctx.getSharedPreferences("habitia_settings", Context.MODE_PRIVATE))
}
