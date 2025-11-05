package com.elitecouture.app.domain.usecase.auth

import com.elitecouture.app.data.session.SessionManager

/**
 * Caso de uso para cerrar sesión del usuario actual.
 * 
 * Responsabilidades:
 * - Limpiar sesión del usuario
 * - Limpiar datos almacenados localmente
 * - Retornar al estado no autenticado
 * 
 * @property sessionManager gestor de sesión del usuario
 */
class LogoutUserUseCase(
    private val sessionManager: SessionManager
) {
    /**
     * Ejecuta el logout del usuario.
     * 
     * Limpia toda la información de sesión almacenada.
     */
    operator fun invoke() {
        sessionManager.clearSession()
    }
}
