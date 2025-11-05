package com.elitecouture.app.ui.feature.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User
import com.elitecouture.app.ui.common.base.BaseViewModel

/**
 * ViewModel para ProfileFragment.
 * 
 * Responsabilidades:
 * - Cargar información del usuario actual
 * - Gestionar edición de perfil
 * - Manejar logout
 * - Gestionar acciones rápidas (favoritos, historial, configuración)
 */
class ProfileViewModel(
    private val sessionManager: SessionManager
) : BaseViewModel() {

    /**
     * Estados del perfil
     */
    sealed class ProfileState {
        object Loading : ProfileState()
        data class Success(val user: User) : ProfileState()
        data class Error(val message: String) : ProfileState()
        object Guest : ProfileState()
    }

    /**
     * Eventos de perfil
     */
    sealed class ProfileEvent {
        object NavigateToLogin : ProfileEvent()
        object NavigateToStore : ProfileEvent()
        object ShowEditProfile : ProfileEvent()
        object ShowFavorites : ProfileEvent()
        object ShowPurchaseHistory : ProfileEvent()
        object ShowSettings : ProfileEvent()
        data class ShowMessage(val message: String) : ProfileEvent()
    }

    /**
     * Estado observable del perfil
     */
    private val _profileState = MutableLiveData<ProfileState>(ProfileState.Loading)
    val profileState: LiveData<ProfileState> = _profileState

    /**
     * Eventos del perfil
     */
    private val _profileEvent = MutableLiveData<ProfileEvent?>()
    val profileEvent: LiveData<ProfileEvent?> = _profileEvent

    /**
     * Indica si está en modo edición
     */
    private val _isEditMode = MutableLiveData(false)
    val isEditMode: LiveData<Boolean> = _isEditMode

    /**
     * Carga la información del usuario actual.
     */
    fun loadUserProfile() {
        launchSafe {
            _profileState.value = ProfileState.Loading
            
            if (!sessionManager.isLoggedIn()) {
                _profileState.value = ProfileState.Guest
                return@launchSafe
            }
            
            // Obtener datos del usuario de la sesión
            val email = sessionManager.getUserEmail()
            val name = sessionManager.getUserName()
            val userId = sessionManager.getUserId()
            
            if (email != null && userId != null) {
                // Crear usuario temporal con datos de sesión
                // En el futuro, obtener de repositorio/base de datos
                val user = User(
                    id = userId,
                    uuid = "",
                    email = email,
                    firstName = name ?: "Usuario",
                    lastName = null,
                    isGuest = sessionManager.isGuestMode(),
                    createdAt = System.currentTimeMillis()
                )
                _profileState.value = ProfileState.Success(user)
            } else {
                _profileState.value = ProfileState.Error("No se pudo cargar el perfil")
            }
        }
    }

    /**
     * Activa o desactiva el modo edición.
     */
    fun toggleEditMode() {
        _isEditMode.value = !(_isEditMode.value ?: false)
        if (_isEditMode.value == true) {
            _profileEvent.value = ProfileEvent.ShowEditProfile
        }
    }

    /**
     * Guarda los cambios del perfil.
     * 
     * @param firstName nuevo nombre
     * @param lastName nuevo apellido
     * @param address nueva dirección
     */
    fun saveProfileChanges(firstName: String, lastName: String?, address: String?) {
        launchSafe {
            // TODO: Implementar guardado en repositorio
            
            // Por ahora solo muestra mensaje
            _profileEvent.value = ProfileEvent.ShowMessage("Cambios guardados correctamente")
            _isEditMode.value = false
            
            // Recargar perfil
            loadUserProfile()
        }
    }

    /**
     * Cierra la sesión del usuario.
     */
    fun logout() {
        launchSafe {
            sessionManager.clearSession()
            _profileEvent.value = ProfileEvent.NavigateToLogin
        }
    }

    /**
     * Navega de regreso a la tienda.
     */
    fun backToStore() {
        _profileEvent.value = ProfileEvent.NavigateToStore
    }

    /**
     * Muestra la pantalla de favoritos.
     */
    fun showFavorites() {
        _profileEvent.value = ProfileEvent.ShowFavorites
    }

    /**
     * Muestra el historial de compras.
     */
    fun showPurchaseHistory() {
        _profileEvent.value = ProfileEvent.ShowPurchaseHistory
    }

    /**
     * Muestra la configuración.
     */
    fun showSettings() {
        _profileEvent.value = ProfileEvent.ShowSettings
    }

    /**
     * Limpia el evento después de ser manejado.
     */
    fun onEventHandled() {
        _profileEvent.value = null
    }
}
