package com.elitecouture.app.ui.feature.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elitecouture.app.domain.usecase.auth.RegisterUserUseCase
import com.elitecouture.app.ui.common.base.BaseViewModel
import com.elitecouture.app.util.ValidationUtil

/**
 * ViewModel para RegisterFragment.
 * 
 * Responsabilidades:
 * - Validar datos de registro
 * - Ejecutar caso de uso de registro
 * - Gestionar estados de carga y error
 * - Notificar registro exitoso
 */
class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase
) : BaseViewModel() {

    /**
     * Estados del proceso de registro
     */
    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
        data class ValidationError(
            val firstNameError: String? = null,
            val emailError: String? = null,
            val passwordError: String? = null,
            val confirmPasswordError: String? = null
        ) : RegisterState()
    }

    /**
     * Eventos de navegación
     */
    sealed class RegisterEvent {
        object NavigateToStore : RegisterEvent()
        object NavigateToLogin : RegisterEvent()
        object ShowGoogleRegister : RegisterEvent()
        object ShowAppleRegister : RegisterEvent()
    }

    /**
     * Estado observable del registro
     */
    private val _registerState = MutableLiveData<RegisterState>(RegisterState.Idle)
    val registerState: LiveData<RegisterState> = _registerState

    /**
     * Eventos de navegación
     */
    private val _registerEvent = MutableLiveData<RegisterEvent?>()
    val registerEvent: LiveData<RegisterEvent?> = _registerEvent

    /**
     * Intenta registrar un nuevo usuario.
     * 
     * @param firstName nombre del usuario
     * @param lastName apellido del usuario (opcional)
     * @param email correo electrónico
     * @param password contraseña
     * @param confirmPassword confirmación de contraseña
     */
    fun register(
        firstName: String,
        lastName: String?,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        // Validar entradas
        val validation = ValidationUtil.validateRegistrationForm(
            firstName, email, password, confirmPassword
        )
        
        if (!validation.isValid) {
            // Determinar qué campo tiene error
            val firstNameValidation = ValidationUtil.validateName(firstName, "Nombre")
            val emailValidation = ValidationUtil.validateEmail(email)
            val passwordValidation = ValidationUtil.validatePassword(password)
            val matchValidation = ValidationUtil.validatePasswordMatch(password, confirmPassword)
            
            _registerState.value = RegisterState.ValidationError(
                firstNameError = if (!firstNameValidation.isValid) firstNameValidation.errorMessage else null,
                emailError = if (!emailValidation.isValid) emailValidation.errorMessage else null,
                passwordError = if (!passwordValidation.isValid) passwordValidation.errorMessage else null,
                confirmPasswordError = if (!matchValidation.isValid) matchValidation.errorMessage else null
            )
            return
        }

        // Ejecutar registro
        launchSafe {
            _registerState.value = RegisterState.Loading
            
            try {
                val result = registerUserUseCase(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password
                )
                
                result.fold(
                    onSuccess = { user ->
                        _registerState.value = RegisterState.Success
                        _registerEvent.value = RegisterEvent.NavigateToStore
                    },
                    onFailure = { error ->
                        _registerState.value = RegisterState.Error(
                            error.message ?: "Error al crear la cuenta"
                        )
                    }
                )
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(
                    e.message ?: "Error al crear la cuenta"
                )
            }
        }
    }

    /**
     * Navega a la pantalla de login.
     */
    fun navigateToLogin() {
        _registerEvent.value = RegisterEvent.NavigateToLogin
    }

    /**
     * Inicia registro con Google.
     */
    fun registerWithGoogle() {
        _registerEvent.value = RegisterEvent.ShowGoogleRegister
    }

    /**
     * Inicia registro con Apple.
     */
    fun registerWithApple() {
        _registerEvent.value = RegisterEvent.ShowAppleRegister
    }

    /**
     * Limpia el evento después de ser manejado.
     */
    fun onEventHandled() {
        _registerEvent.value = null
    }

    /**
     * Resetea el estado a Idle.
     */
    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}
