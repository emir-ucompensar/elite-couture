package com.elitecouture.app.ui.feature.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elitecouture.app.domain.usecase.auth.EnableGuestAccessUseCase
import com.elitecouture.app.domain.usecase.auth.LoginUserUseCase
import com.elitecouture.app.ui.common.base.BaseViewModel
import com.elitecouture.app.util.ValidationUtil

/**
 * ViewModel para LoginFragment.
 * 
 * Responsabilidades:
 * - Validar credenciales de login
 * - Ejecutar caso de uso de login
 * - Manejar modo invitado
 * - Gestionar estados de carga y error
 * - Notificar navegación exitosa
 */
class LoginViewModel(
    private val loginUserUseCase: LoginUserUseCase,
    private val enableGuestAccessUseCase: EnableGuestAccessUseCase
) : BaseViewModel() {

    /**
     * Estados del proceso de login
     */
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
        data class ValidationError(val emailError: String? = null, val passwordError: String? = null) : LoginState()
    }

    /**
     * Eventos de navegación
     */
    sealed class LoginEvent {
        object NavigateToStore : LoginEvent()
        object NavigateToRegister : LoginEvent()
        object ShowForgotPassword : LoginEvent()
        object ShowGoogleLogin : LoginEvent()
        object ShowAppleLogin : LoginEvent()
    }

    /**
     * Estado observable del login
     */
    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    /**
     * Eventos de navegación
     */
    private val _loginEvent = MutableLiveData<LoginEvent?>()
    val loginEvent: LiveData<LoginEvent?> = _loginEvent

    /**
     * Intenta hacer login con email y contraseña.
     * 
     * @param email correo electrónico del usuario
     * @param password contraseña del usuario
     */
    fun login(email: String, password: String) {
        // Validar entradas primero
        val validation = ValidationUtil.validateLoginCredentials(email, password)
        if (!validation.isValid) {
            _loginState.value = LoginState.ValidationError(
                emailError = if (email.isBlank() || !ValidationUtil.validateEmail(email).isValid) {
                    validation.errorMessage
                } else null,
                passwordError = if (password.isBlank() || !ValidationUtil.validatePassword(password).isValid) {
                    validation.errorMessage
                } else null
            )
            return
        }

        // Ejecutar login
        launchSafe {
            _loginState.value = LoginState.Loading
            
            try {
                val result = loginUserUseCase(email, password)
                
                result.fold(
                    onSuccess = { user ->
                        _loginState.value = LoginState.Success
                        _loginEvent.value = LoginEvent.NavigateToStore
                    },
                    onFailure = { error ->
                        _loginState.value = LoginState.Error(
                            error.message ?: "Error al iniciar sesión"
                        )
                    }
                )
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(
                    e.message ?: "Error al iniciar sesión"
                )
            }
        }
    }

    /**
     * Habilita el acceso como invitado.
     */
    fun enableGuestMode() {
        launchSafe {
            _loginState.value = LoginState.Loading
            
            try {
                val result = enableGuestAccessUseCase()
                
                result.fold(
                    onSuccess = { user ->
                        _loginState.value = LoginState.Success
                        _loginEvent.value = LoginEvent.NavigateToStore
                    },
                    onFailure = { error ->
                        _loginState.value = LoginState.Error(
                            error.message ?: "Error al habilitar modo invitado"
                        )
                    }
                )
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(
                    e.message ?: "Error al habilitar modo invitado"
                )
            }
        }
    }

    /**
     * Navega a la pantalla de registro.
     */
    fun navigateToRegister() {
        _loginEvent.value = LoginEvent.NavigateToRegister
    }

    /**
     * Muestra diálogo/pantalla de recuperación de contraseña.
     */
    fun forgotPassword() {
        _loginEvent.value = LoginEvent.ShowForgotPassword
    }

    /**
     * Inicia login con Google.
     */
    fun loginWithGoogle() {
        _loginEvent.value = LoginEvent.ShowGoogleLogin
    }

    /**
     * Inicia login con Apple.
     */
    fun loginWithApple() {
        _loginEvent.value = LoginEvent.ShowAppleLogin
    }

    /**
     * Limpia el evento después de ser manejado.
     */
    fun onEventHandled() {
        _loginEvent.value = null
    }

    /**
     * Resetea el estado a Idle.
     */
    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
