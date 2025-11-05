package com.elitecouture.app.domain.usecase.auth

import com.elitecouture.app.data.repository.AuthRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User

/**
 * Caso de uso para registrar un nuevo usuario.
 * 
 * Responsabilidades:
 * - Registrar usuario en el repositorio
 * - Guardar sesión automáticamente después del registro
 * - Desactivar modo invitado
 * - Retornar resultado del registro
 * 
 * @property authRepository repositorio para operaciones de autenticación
 * @property sessionManager gestor de sesión del usuario
 */
class RegisterUserUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Ejecuta el registro con los datos proporcionados.
     * 
     * @param firstName nombre del usuario
     * @param lastName apellido del usuario (opcional)
     * @param email correo electrónico
     * @param password contraseña
     * @return Result<User> con el usuario creado si es exitoso, o error si falla
     */
    operator fun invoke(
        firstName: String,
        lastName: String?,
        email: String,
        password: String
    ): Result<User> {
        // Crear usuario temporal con los datos
        val user = User(
            id = 0, // El ID será asignado por el repositorio
            uuid = "", // Será asignado por el repositorio
            email = email,
            firstName = firstName,
            lastName = lastName,
            isGuest = false,
            createdAt = System.currentTimeMillis()
        )
        
        val result = authRepository.register(user, password)
        result.getOrNull()?.let { createdUser ->
            // Guardar sesión automáticamente con todos los datos
            sessionManager.setUserFullInfo(
                id = createdUser.id,
                uuid = createdUser.uuid,
                email = createdUser.email,
                firstName = createdUser.firstName,
                lastName = createdUser.lastName,
                address = createdUser.address,
                isGuest = createdUser.isGuest,
                createdAt = createdUser.createdAt
            )
        }
        return result
    }
}
