package upeu.edu.pe.Data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.auth.SessionStorage

private val Context.sessionDataStore by preferencesDataStore(name = "session_prefs")

class DataStoreSessionStorage(private val context: Context) : SessionStorage {

    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val EMAIL = stringPreferencesKey("email")
        val USER_ID = longPreferencesKey("user_id")
        val NAME = stringPreferencesKey("name")
        val BIRTH_DATE = stringPreferencesKey("birth_date")
    }

    override suspend fun load(): SessionManager.SessionData? {
        val p = context.sessionDataStore.data.first()
        val token = p[Keys.TOKEN] ?: return null
        return SessionManager.SessionData(
            token = token,
            email = p[Keys.EMAIL],
            userId = p[Keys.USER_ID],
            name = p[Keys.NAME],
            birthDate = p[Keys.BIRTH_DATE]
        )
    }

    override suspend fun save(data: SessionManager.SessionData) {
        context.sessionDataStore.edit { p ->
            if (data.token != null) p[Keys.TOKEN] = data.token else p.remove(Keys.TOKEN)
            if (data.email != null) p[Keys.EMAIL] = data.email else p.remove(Keys.EMAIL)
            if (data.userId != null) p[Keys.USER_ID] = data.userId else p.remove(Keys.USER_ID)
            if (data.name != null) p[Keys.NAME] = data.name else p.remove(Keys.NAME)
            if (data.birthDate != null) p[Keys.BIRTH_DATE] = data.birthDate else p.remove(Keys.BIRTH_DATE)
        }
    }

    override suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }
}
