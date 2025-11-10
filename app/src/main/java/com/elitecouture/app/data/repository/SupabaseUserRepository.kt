package com.elitecouture.app.data.repository

import com.elitecouture.app.data.remote.SupabaseUserService
import com.elitecouture.app.domain.model.User

/**
 * User repository using Supabase as backend.
 * 
 * This repository replaces the local SQLite implementation with cloud-based storage.
 * All operations are now asynchronous and use Kotlin coroutines.
 */
class SupabaseUserRepository {
    
    /**
     * Creates a new user in Supabase.
     * 
     * @param user User to create
     * @param password User password
     * @return Created user with server-generated ID and UUID
     */
    suspend fun createUser(user: User, password: String): User {
        return SupabaseUserService.createUser(user, password)
    }
    
    /**
     * Retrieves a user by UUID.
     * 
     * @param uuid User UUID
     * @return User if found, null otherwise
     */
    suspend fun getUserByUuid(uuid: String): User? {
        return SupabaseUserService.getUserByUuid(uuid)
    }
    
    /**
     * Retrieves a user by email.
     * 
     * @param email User email
     * @return User if found, null otherwise
     */
    suspend fun getUserByEmail(email: String): User? {
        return SupabaseUserService.getUserByEmail(email)
    }
    
    /**
     * Authenticates a user with email and password.
     * 
     * @param email User email
     * @param password User password
     * @return User if authentication successful, null otherwise
     */
    suspend fun authenticateUser(email: String, password: String): User? {
        return SupabaseUserService.authenticateUser(email, password)
    }
    
    /**
     * Retrieves all users.
     * 
     * @return List of all users
     */
    suspend fun getAllUsers(): List<User> {
        return SupabaseUserService.getAllUsers()
    }
    
    /**
     * Updates an existing user.
     * 
     * @param user User with updated data
     * @return Updated user
     */
    suspend fun updateUser(user: User): User {
        return SupabaseUserService.updateUser(user)
    }
    
    /**
     * Updates user password.
     * 
     * @param uuid User UUID
     * @param newPassword New password
     */
    suspend fun updatePassword(uuid: String, newPassword: String) {
        SupabaseUserService.updatePassword(uuid, newPassword)
    }
    
    /**
     * Deletes a user.
     * 
     * @param uuid User UUID
     */
    suspend fun deleteUser(uuid: String) {
        SupabaseUserService.deleteUser(uuid)
    }
}
