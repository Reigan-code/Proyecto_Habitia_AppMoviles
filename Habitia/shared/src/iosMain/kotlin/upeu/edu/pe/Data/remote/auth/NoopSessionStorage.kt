package upeu.edu.pe.Data.remote.auth

import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.auth.SessionStorage

class NoopSessionStorage : SessionStorage {
    override suspend fun load(): SessionManager.SessionData? = null
    override suspend fun save(data: SessionManager.SessionData) {}
    override suspend fun clear() {}
}
