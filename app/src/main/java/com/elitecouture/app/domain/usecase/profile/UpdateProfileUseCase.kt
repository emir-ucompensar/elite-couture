package com.elitecouture.app.domain.usecase.profile

import com.elitecouture.app.data.repository.SupabaseUserRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User

/**
 * Caso de uso para actualizar el perfil del usuario.
 * 
 * ✨ MIGRADO A SUPABASE ✨
 * 
 * Responsabilidades:
 * - Actualizar información del usuario en Supabase
 * - Actualizar la sesión con los nuevos datos
 * - Retornar resultado de la operación
 * 
 * @property userRepository repositorio de Supabase para operaciones de usuario
 * @property sessionManager gestor de sesión del usuario
 */
class UpdateProfileUseCase(
    private val userRepository: SupabaseUserRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Actualiza el perfil del usuario.
     * 
     * @param user datos actualizados del usuario
     * @return Result<User> con el usuario actualizado si es exitoso, o error si falla
     */
    suspend operator fun invoke(user: User): Result<User> {
        return try {
            val updatedUser = userRepository.updateUser(user)
            
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
            
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
