package com.elitecouture.app.domain.usecase.profile

import com.elitecouture.app.data.repository.AuthRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User

/**
 * Caso de uso para actualizar el perfil del usuario.
 * 
 * Responsabilidades:
 * - Actualizar información del usuario en la base de datos
 * - Actualizar la sesión con los nuevos datos
 * - Retornar resultado de la operación
 * 
 * @property authRepository repositorio para operaciones de usuario
 * @property sessionManager gestor de sesión del usuario
 */
class UpdateProfileUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Actualiza el perfil del usuario.
     * 
     * @param user datos actualizados del usuario
     * @return Result<User> con el usuario actualizado si es exitoso, o error si falla
     */
    operator fun invoke(user: User): Result<User> {
        val result = authRepository.updateProfile(user)
        
        result.getOrNull()?.let { updatedUser ->
            // Actualizar la sesión con los nuevos datos
            sessionManager.setUserFullInfo(
                id = updatedUser.id,
                uuid = updatedUser.uuid,
                email = updatedUser.email,
                firstName = updatedUser.firstName,
                lastName = updatedUser.lastName,
                address = updatedUser.address,
                isGuest = updatedUser.isGuest,
                createdAt = updatedUser.createdAt
            )
        }
        
        return result
    }
}
