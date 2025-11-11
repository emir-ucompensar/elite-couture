package com.elitecouture.app.domain.usecase.auth

import com.elitecouture.app.data.repository.SupabaseUserRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User

/**
 * Caso de uso para registrar un nuevo usuario.
 * 
 * ✨ MIGRADO A SUPABASE ✨
 * 
 * Responsabilidades:
 * - Registrar usuario en Supabase
 * - Guardar sesión automáticamente después del registro
 * - Desactivar modo invitado
 * - Retornar resultado del registro
 * 
 * @property userRepository repositorio de Supabase para operaciones de usuario
 * @property sessionManager gestor de sesión del usuario
 */
class RegisterUserUseCase(
    private val userRepository: SupabaseUserRepository,
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
    suspend operator fun invoke(
        firstName: String,
        lastName: String?,
        email: String,
        password: String
    ): Result<User> {
        return try {
            android.util.Log.d("RegisterUserUseCase", "Starting registration for email: $email")
            
            // Crear usuario temporal con los datos
            val user = User(
                id = 0, // El ID será asignado por Supabase
                uuid = java.util.UUID.randomUUID().toString(), // Generar UUID válido
                email = email,
                firstName = firstName,
                lastName = lastName,
                isGuest = false,
                createdAt = System.currentTimeMillis()
            )
            
            android.util.Log.d("RegisterUserUseCase", "Calling userRepository.createUser()")
            val createdUser = userRepository.createUser(user, password)
            android.util.Log.d("RegisterUserUseCase", "User created successfully: ${createdUser.uuid}")
            
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
            
            Result.success(createdUser)
        } catch (e: Exception) {
            android.util.Log.e("RegisterUserUseCase", "Registration failed", e)
            android.util.Log.e("RegisterUserUseCase", "Error details: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
