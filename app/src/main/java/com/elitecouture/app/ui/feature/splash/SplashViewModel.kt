package com.elitecouture.app.ui.feature.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.ui.common.base.BaseViewModel
import com.elitecouture.app.util.Constants
import kotlinx.coroutines.delay

/**
 * ViewModel para SplashActivity.
 * 
 * Responsabilidades:
 * - Simular carga de recursos iniciales
 * - Verificar estado de autenticación
 * - Determinar siguiente destino (MainActivity con login o store)
 * - Notificar cuando completar la carga
 */
class SplashViewModel(
    private val sessionManager: SessionManager
) : BaseViewModel() {

    /**
     * Estado del splash screen
     */
    sealed class SplashState {
        object Loading : SplashState()
        data class NavigateToMain(val isLoggedIn: Boolean) : SplashState()
    }

    /**
     * Estado observable del splash
     */
    private val _splashState = MutableLiveData<SplashState>(SplashState.Loading)
    val splashState: LiveData<SplashState> = _splashState

    /**
     * Inicia el proceso de carga del splash.
     * 
     * Simula carga de recursos y verifica autenticación.
     */
    fun startLoading() {
        launchSafe {
            // Simular carga de recursos (mínimo tiempo de splash)
            delay(Constants.UI.SPLASH_DURATION_MS)
            
            // Verificar estado de autenticación
            val isLoggedIn = sessionManager.isLoggedIn()
            
            // Notificar para navegar a MainActivity
            _splashState.value = SplashState.NavigateToMain(isLoggedIn)
        }
    }

    /**
     * Obtiene información del usuario si está logueado.
     * 
     * Útil para pre-cargar datos o personalizar la experiencia.
     */
    fun getUserInfo(): Pair<String?, String?> {
        return Pair(
            sessionManager.getUserEmail(),
            sessionManager.getUserName()
        )
    }
}
