package com.elitecouture.app.domain.usecase.auth

import com.elitecouture.app.data.repository.SupabaseUserRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User

/**
 * Caso de uso para iniciar sesión con email y contraseña.
 * 
 * ✨ MIGRADO A SUPABASE ✨
 * 
 * Responsabilidades:
 * - Validar credenciales con Supabase
 * - Guardar sesión del usuario en SessionManager
 * - Desactivar modo invitado
 * - Retornar resultado del login
 * 
 * @property userRepository repositorio de Supabase para operaciones de usuario
 * @property sessionManager gestor de sesión del usuario
 */
class LoginUserUseCase(
    private val userRepository: SupabaseUserRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Ejecuta el login con las credenciales proporcionadas.
     * 
     * @param email correo electrónico del usuario
     * @param password contraseña del usuario
     * @return Result<User> con el usuario si es exitoso, o error si falla
     */
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return try {
            val user = userRepository.authenticateUser(email, password)
            
            if (user != null) {
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
                Result.success(user)
            } else {
                Result.failure(IllegalArgumentException("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
