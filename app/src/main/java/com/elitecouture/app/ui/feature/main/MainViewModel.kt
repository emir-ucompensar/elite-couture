package com.elitecouture.app.ui.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.ui.common.base.BaseViewModel
import com.elitecouture.app.util.Constants

/**
 * ViewModel para MainActivity.
 * 
 * Responsabilidades:
 * - Determinar el destino de navegación inicial (login o store)
 * - Gestionar estado de la toolbar (título, visibilidad, icono)
 * - Manejar eventos de navegación del menú
 * - Coordinar con SessionManager para estado de autenticación
 * 
 * MainActivity solo observa este ViewModel y actualiza la UI,
 * sin lógica de negocio propia.
 */
class MainViewModel(
    private val sessionManager: SessionManager
) : BaseViewModel() {

    /**
     * Estado de la toolbar
     */
    data class ToolbarState(
        val isVisible: Boolean = true,
        val title: String = "",
        val showNavigationIcon: Boolean = false,
        val navigationIconType: NavigationIconType = NavigationIconType.HAMBURGER
    )

    /**
     * Tipos de iconos de navegación en la toolbar
     */
    enum class NavigationIconType {
        HAMBURGER,  // Menú lateral
        BACK,       // Flecha hacia atrás
        NONE        // Sin icono
    }

    /**
     * Eventos de navegación que MainActivity debe manejar
     */
    sealed class NavigationEvent {
        data class NavigateToDestination(val destinationId: Int) : NavigationEvent()
        object ShowMenuInProgress : NavigationEvent()
    }

    /**
     * Estado observable de la toolbar
     */
    private val _toolbarState = MutableLiveData<ToolbarState>()
    val toolbarState: LiveData<ToolbarState> = _toolbarState

    /**
     * Eventos de navegación one-shot
     */
    private val _navigationEvent = MutableLiveData<NavigationEvent?>()
    val navigationEvent: LiveData<NavigationEvent?> = _navigationEvent

    /**
     * Determina el destino inicial de navegación según el estado de sesión.
     * 
     * IMPORTANTE: El modo invitado NO persiste, por lo que al abrir la app
     * siempre debe ir a login si no hay una sesión de usuario real.
     * 
     * @return ID del fragment de inicio (login o store)
     */
    fun getStartDestination(): Int {
        return if (sessionManager.isLoggedIn() && !sessionManager.isGuestMode()) {
            // Usuario autenticado real -> ir a la tienda
            com.elitecouture.app.R.id.storeFragment
        } else {
            // Sin sesión persistente -> ir a login
            // (Esto incluye: no logueado, invitado previo que cerró app)
            com.elitecouture.app.R.id.loginFragment
        }
    }

    /**
     * Actualiza el estado de la toolbar según el destino actual.
     * 
     * La toolbar solo se muestra en destinos autenticados (Store y Profile).
     * Los destinos de autenticación (Login, Register) no deben mostrar toolbar.
     * 
     * @param destinationId ID del destino de navegación actual
     * @param titleResId ID del recurso de string para el título
     */
    fun updateToolbarForDestination(destinationId: Int, titleResId: Int?) {
        // Destinos que NO deben mostrar toolbar (pantallas de autenticación)
        val authDestinations = setOf(
            com.elitecouture.app.R.id.loginFragment,
            com.elitecouture.app.R.id.registerFragment
        )
        
        val shouldShowToolbar = titleResId != null && destinationId !in authDestinations
        val isStoreDestination = destinationId == com.elitecouture.app.R.id.storeFragment
        
        _toolbarState.value = ToolbarState(
            isVisible = shouldShowToolbar,
            title = "", // El título se actualizará desde MainActivity con getString()
            showNavigationIcon = isStoreDestination,
            navigationIconType = if (isStoreDestination) NavigationIconType.HAMBURGER else NavigationIconType.NONE
        )
    }

    /**
     * Maneja el click en el botón de navegación de la toolbar.
     * 
     * Por ahora muestra mensaje de "en progreso".
     * En el futuro abrirá el menú lateral.
     */
    fun onNavigationIconClick() {
        _navigationEvent.value = NavigationEvent.ShowMenuInProgress
    }

    /**
     * Limpia el evento de navegación después de ser manejado.
     * 
     * Importante: Llamar después de procesar el evento para evitar
     * que se ejecute múltiples veces en rotaciones de pantalla.
     */
    fun onNavigationEventHandled() {
        _navigationEvent.value = null
    }

    /**
     * Verifica si el usuario está autenticado.
     * 
     * @return true si hay sesión activa
     */
    fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    /**
     * Obtiene información del usuario actual.
     * 
     * @return email del usuario o null si no está logueado
     */
    fun getCurrentUserEmail(): String? {
        return sessionManager.getUserEmail()
    }

    /**
     * Cierra la sesión del usuario actual.
     * 
     * Limpia los datos de sesión y notifica para navegar a login.
     */
    fun logout() {
        launchSafe {
            sessionManager.clearSession()
            _navigationEvent.value = NavigationEvent.NavigateToDestination(
                com.elitecouture.app.R.id.loginFragment
            )
        }
    }
}
