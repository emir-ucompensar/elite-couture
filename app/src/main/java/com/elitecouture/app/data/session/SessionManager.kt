package com.elitecouture.app.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.elitecouture.app.util.CryptoUtil

/**
 * Session manager con encriptación de datos sensibles.
 * 
 * Información personal como direcciones se almacena encriptada usando AES-256
 * para proteger la privacidad del usuario en caso de acceso no autorizado.
 */
class SessionManager(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setActiveUserId(userId: Long) {
        preferences.edit {
            putLong(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
        }
    }

    fun setGuestModeEnabled(isGuest: Boolean) {
        if (isGuest) {
            // Activar modo invitado: limpiar todo y SOLO guardar el flag
            preferences.edit()
                .clear() // Limpiar cualquier sesión previa
                .putBoolean(KEY_IS_GUEST, true)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .commit() // Commit síncrono para asegurar escritura inmediata
        } else {
            // Desactivar modo invitado
            preferences.edit {
                putBoolean(KEY_IS_GUEST, false)
            }
        }
    }

    fun clearSession() {
        preferences.edit { clear() }
    }

    fun isGuest(): Boolean = preferences.getBoolean(KEY_IS_GUEST, false)
    
    fun isGuestMode(): Boolean = isGuest()

    fun isLoggedIn(): Boolean = preferences.getBoolean(KEY_IS_LOGGED_IN, false)

    fun currentUserId(): Long = preferences.getLong(KEY_USER_ID, -1L)
    
    fun getUserId(): Long? {
        val id = currentUserId()
        return if (id != -1L) id else null
    }
    
    fun getUserEmail(): String? = preferences.getString(KEY_USER_EMAIL, null)
    
    fun getUserName(): String? = preferences.getString(KEY_USER_NAME, null)
    
    fun getUserFirstName(): String? = preferences.getString(KEY_USER_FIRST_NAME, null)
    
    fun getUserLastName(): String? = preferences.getString(KEY_USER_LAST_NAME, null)
    
    /**
     * Obtiene la dirección del usuario desencriptada.
     * La dirección se almacena encriptada por seguridad.
     */
    fun getUserAddress(): String? {
        val encryptedAddress = preferences.getString(KEY_USER_ADDRESS, null)
        return CryptoUtil.decrypt(encryptedAddress)
    }
    
    fun setUserInfo(email: String, name: String?) {
        preferences.edit {
            putString(KEY_USER_EMAIL, email)
            if (name != null) {
                putString(KEY_USER_NAME, name)
            }
        }
    }
    
    fun setUserFullInfo(email: String, firstName: String?, lastName: String?, address: String?) {
        preferences.edit {
            putString(KEY_USER_EMAIL, email)
            if (firstName != null) {
                putString(KEY_USER_FIRST_NAME, firstName)
                // También guardar el nombre completo para compatibilidad
                val fullName = if (lastName != null) "$firstName $lastName" else firstName
                putString(KEY_USER_NAME, fullName)
            }
            if (lastName != null) {
                putString(KEY_USER_LAST_NAME, lastName)
            }
            if (address != null) {
                // Encriptar la dirección antes de guardarla
                val encryptedAddress = CryptoUtil.encrypt(address)
                putString(KEY_USER_ADDRESS, encryptedAddress)
            }
        }
    }
    
    /**
     * Actualiza solo la dirección del usuario (encriptada).
     * Útil cuando el usuario agrega/actualiza su dirección desde el perfil.
     */
    fun setUserAddress(address: String?) {
        preferences.edit {
            if (address != null) {
                val encryptedAddress = CryptoUtil.encrypt(address)
                putString(KEY_USER_ADDRESS, encryptedAddress)
            } else {
                remove(KEY_USER_ADDRESS)
            }
        }
    }
    
    fun getUserUuid(): String? = preferences.getString(KEY_USER_UUID, null)
    
    fun getUserCreatedAt(): Long? {
        val timestamp = preferences.getLong(KEY_USER_CREATED_AT, -1L)
        return if (timestamp != -1L) timestamp else null
    }
    
    /**
     * Versión completa de setUserFullInfo que acepta todos los campos del usuario.
     * 
     * IMPORTANTE: Si isGuest=true, la sesión NO se persiste en SharedPreferences,
     * solo se activa el flag de invitado. La sesión de invitado muere al cerrar la app.
     */
    fun setUserFullInfo(
        id: Long,
        uuid: String,
        email: String,
        firstName: String,
        lastName: String?,
        address: String?,
        isGuest: Boolean,
        createdAt: Long
    ) {
        if (isGuest) {
            // Modo invitado: SOLO guardar el flag, sin persistir datos del usuario
            // Usar commit() en lugar de apply() para asegurar escritura síncrona
            preferences.edit()
                .putBoolean(KEY_IS_GUEST, true)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_USER_ID)
                .remove(KEY_USER_UUID)
                .remove(KEY_USER_EMAIL)
                .remove(KEY_USER_FIRST_NAME)
                .remove(KEY_USER_LAST_NAME)
                .remove(KEY_USER_NAME)
                .remove(KEY_USER_ADDRESS)
                .remove(KEY_USER_CREATED_AT)
                .commit() // commit() es síncrono, apply() es asíncrono
        } else {
            // Usuario real: guardar todos los datos
            preferences.edit {
                putLong(KEY_USER_ID, id)
                putString(KEY_USER_UUID, uuid)
                putString(KEY_USER_EMAIL, email)
                putString(KEY_USER_FIRST_NAME, firstName)
                putBoolean(KEY_IS_GUEST, false)
                putLong(KEY_USER_CREATED_AT, createdAt)
                putBoolean(KEY_IS_LOGGED_IN, true)
                
                // Nombre completo para compatibilidad
                val fullName = if (lastName != null) {
                    putString(KEY_USER_LAST_NAME, lastName)
                    "$firstName $lastName"
                } else {
                    remove(KEY_USER_LAST_NAME)
                    firstName
                }
                putString(KEY_USER_NAME, fullName)
                
                // Dirección encriptada
                if (address != null) {
                    val encryptedAddress = CryptoUtil.encrypt(address)
                    putString(KEY_USER_ADDRESS, encryptedAddress)
                } else {
                    remove(KEY_USER_ADDRESS)
                }
            }
        }
    }

    companion object {
        private const val PREFS_NAME = "elite_couture_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_UUID = "user_uuid"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_GUEST = "is_guest"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_FIRST_NAME = "user_first_name"
        private const val KEY_USER_LAST_NAME = "user_last_name"
        private const val KEY_USER_ADDRESS = "user_address"
        private const val KEY_USER_CREATED_AT = "user_created_at"
    }
}
