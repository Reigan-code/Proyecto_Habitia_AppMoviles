package upeu.edu.pe.Data.notification

import android.content.Context

// Contexto de la app, se inicializa en MainActivity.onCreate().
// Lo usa createLocalNotificationPlatform() porque el expect no puede recibir Context.
object AndroidAppContext {
    lateinit var context: Context
        private set

    fun init(context: Context) {
        this.context = context.applicationContext
    }
}
