package com.elitecouture.app.ui.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Clase base para todos los ViewModels de la aplicación.
 * 
 * Proporciona funcionalidad común:
 * - Manejo centralizado de errores con CoroutineExceptionHandler
 * - Método launch seguro para coroutines
 * - Lifecycle-aware scope (viewModelScope)
 * - Logging de errores no manejados
 * 
 * Todos los ViewModels de la app deben heredar de esta clase.
 * 
 * Ejemplo de uso:
 * ```kotlin
 * class LoginViewModel : BaseViewModel() {
 *     fun login(email: String, password: String) {
 *         launchSafe {
 *             // Código que puede lanzar excepciones
 *             val result = loginUseCase(email, password)
 *             // Actualizar UI con resultado
 *         }
 *     }
 * }
 * ```
 */
abstract class BaseViewModel : ViewModel() {

    /**
     * Handler global para excepciones no capturadas en coroutines.
     * 
     * Sobrescribir [onError] para manejar errores de forma personalizada.
     */
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    /**
     * Lanza una coroutine de forma segura dentro del viewModelScope.
     * 
     * Características:
     * - Usa viewModelScope (lifecycle-aware)
     * - Captura excepciones automáticamente con exceptionHandler
     * - Llama a onError() en caso de excepción
     * 
     * @param block bloque de código suspendido a ejecutar
     * @return Job de la coroutine lanzada
     */
    protected fun launchSafe(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(exceptionHandler) {
            block()
        }
    }

    /**
     * Método llamado cuando ocurre una excepción no manejada.
     * 
     * Override este método para implementar manejo de errores personalizado:
     * - Mostrar mensajes al usuario
     * - Logging específico
     * - Analytics de errores
     * - Recuperación de estados
     * 
     * Por defecto, solo imprime el stack trace.
     * 
     * @param throwable la excepción capturada
     */
    protected open fun onError(throwable: Throwable) {
        // Log del error (en producción usar Crashlytics/Firebase)
        throwable.printStackTrace()
        
        // Aquí se puede agregar lógica común de manejo de errores:
        // - Mostrar LiveData de error general
        // - Enviar a analytics
        // - Logging estructurado
    }

    /**
     * Método llamado cuando el ViewModel está a punto de ser destruido.
     * 
     * Override para limpiar recursos:
     * - Cancelar listeners
     * - Limpiar referencias
     * - Guardar estados pendientes
     * 
     * No es necesario cancelar coroutines del viewModelScope, se cancelan automáticamente.
     */
    override fun onCleared() {
        super.onCleared()
        // Por defecto no hace nada, los ViewModels hijos pueden sobrescribir
    }
}
