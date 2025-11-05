package com.elitecouture.app.domain.usecase.auth

import com.elitecouture.app.data.repository.AuthRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User

/**
 * Caso de uso para iniciar sesión con email y contraseña.
 * 
 * Responsabilidades:
 * - Validar credenciales con el repositorio
 * - Guardar sesión del usuario en SessionManager
 * - Desactivar modo invitado
 * - Retornar resultado del login
 * 
 * @property authRepository repositorio para operaciones de autenticación
 * @property sessionManager gestor de sesión del usuario
 */
class LoginUserUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Ejecuta el login con las credenciales proporcionadas.
     * 
     * @param email correo electrónico del usuario
     * @param password contraseña del usuario
     * @return Result<User> con el usuario si es exitoso, o error si falla
     */
    operator fun invoke(email: String, password: String): Result<User> {
        val result = authRepository.login(email, password)
        result.getOrNull()?.let { user ->
            // Guardar sesión del usuario con toda la información
            sessionManager.setUserFullInfo(
                id = user.id,
                uuid = user.uuid,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                address = user.address,
                isGuest = user.isGuest,
                createdAt = user.createdAt
            )
        }
        return result
    }
}
