package com.elitecouture.app.domain.usecase.auth

import com.elitecouture.app.data.repository.AuthRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User

/**
 * Caso de uso para habilitar el modo invitado.
 * 
 * Responsabilidades:
 * - Crear perfil de invitado en el repositorio
 * - Activar modo invitado en SessionManager
 * - Guardar ID de usuario invitado
 * - Retornar perfil de invitado
 * 
 * @property authRepository repositorio para operaciones de autenticación
 * @property sessionManager gestor de sesión del usuario
 */
class EnableGuestAccessUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Ejecuta la creación del perfil de invitado.
     * 
     * @return Result<User> con el perfil de invitado creado
     */
    operator fun invoke(): Result<User> {
        return try {
            // Simplemente activar el flag de invitado
            // NO crear usuario en base de datos, NO persistir datos
            sessionManager.setGuestModeEnabled(true)
            
            // Retornar un objeto User dummy solo para compatibilidad
            // (no se guarda en ningún lado)
            val dummyGuest = User(
                id = 0L,
                uuid = "",
                email = "",
                firstName = "Invitado",
                lastName = null,
                address = null,
                isGuest = true,
                createdAt = System.currentTimeMillis()
            )
            
            Result.success(dummyGuest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
