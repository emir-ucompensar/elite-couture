package com.elitecouture.app.domain.usecase.auth

import com.elitecouture.app.data.repository.SupabaseUserRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User
import java.util.UUID

/**
 * Caso de uso para habilitar el modo invitado.
 * 
 * ✨ MIGRADO A SUPABASE ✨
 * 
 * Responsabilidades:
 * - Crear perfil de invitado en Supabase
 * - Activar modo invitado en SessionManager
 * - Guardar UUID de usuario invitado
 * - Retornar perfil de invitado
 * 
 * @property userRepository repositorio de Supabase para operaciones de usuario
 * @property sessionManager gestor de sesión del usuario
 */
class EnableGuestAccessUseCase(
    private val userRepository: SupabaseUserRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Ejecuta la creación del perfil de invitado.
     * 
     * @return Result<User> con el perfil de invitado creado
     */
    suspend operator fun invoke(): Result<User> {
        return try {
            // Crear usuario invitado en Supabase
            val guestUser = User(
                id = 0L, // Será asignado por Supabase
                uuid = UUID.randomUUID().toString(),
                email = "invitado_${System.currentTimeMillis()}@elitecouture.app",
                firstName = "Invitado",
                lastName = null,
                address = null,
                isGuest = true,
                createdAt = System.currentTimeMillis()
            )
            
            val createdGuest = userRepository.createUser(guestUser, password = "")
            
            // Guardar sesión del invitado
            sessionManager.setUserFullInfo(
                id = createdGuest.id,
                uuid = createdGuest.uuid,
                email = createdGuest.email,
                firstName = createdGuest.firstName,
                lastName = createdGuest.lastName,
                address = createdGuest.address,
                isGuest = true,
                createdAt = createdGuest.createdAt
            )
            sessionManager.setGuestModeEnabled(true)
            
            Result.success(createdGuest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
