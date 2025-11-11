package com.elitecouture.app.data.remote

import com.elitecouture.app.domain.model.User
import com.elitecouture.app.util.CryptoUtil
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Supabase service for user operations (CRUD).
 * 
 * This service handles all user-related operations with Supabase PostgreSQL,
 * replacing the local SQLite implementation.
 */
object SupabaseUserService {
    
    /**
     * Data class representing the user table schema in Supabase.
     * 
     * Note: This matches the PostgreSQL schema exactly.
     * - `uuid` is a UUID type (not String)
     * - `created_at` is a BIGINT (milliseconds since epoch)
     */
    @Serializable
    data class UserDto(
        val id: Long? = null,
        val uuid: String = UUID.randomUUID().toString(),
        val email: String,
        val password: String? = null,
        val first_name: String,
        val last_name: String? = null,
        val address: String? = null,
        val is_guest: Boolean = false,
        val created_at: Long = System.currentTimeMillis()
    )
    
    /**
     * Converts domain model to DTO for Supabase operations.
     */
    private fun User.toDto(password: String? = null): UserDto = UserDto(
        id = if (id == 0L) null else id,
        uuid = uuid,
        email = email,
        password = password,
        first_name = firstName,
        last_name = lastName,
        address = address,
        is_guest = isGuest,
        created_at = createdAt
    )
    
    /**
     * Converts DTO to domain model.
     */
    private fun UserDto.toDomain(): User = User(
        id = id ?: 0L,
        uuid = uuid,
        email = email,
        firstName = first_name,
        lastName = last_name,
        address = address,
        isGuest = is_guest,
        createdAt = created_at
    )
    
    // ======================================================================
    // CREATE
    // ======================================================================
    
    /**
     * Creates a new user in Supabase.
     * 
     * @param user User domain model
     * @param password User password (nullable for guest users)
     * @return The created user with server-generated ID and UUID
     * @throws Exception if creation fails
     */
    suspend fun createUser(user: User, password: String? = null): User = withContext(Dispatchers.IO) {
    val hashedPassword = password?.let { CryptoUtil.sha256(it) }
    val dto = user.toDto(hashedPassword)
        val result = SupabaseClientProvider.client
            .from("users")
            .insert(dto) {
                select()
            }
            .decodeSingle<UserDto>()
        
        result.toDomain()
    }
    
    // ======================================================================
    // READ
    // ======================================================================
    
    /**
     * Retrieves a user by UUID.
     * 
     * @param uuid User UUID
     * @return User if found, null otherwise
     */
    suspend fun getUserByUuid(uuid: String): User? = withContext(Dispatchers.IO) {
        try {
            val result = SupabaseClientProvider.client
                .from("users")
                .select {
                    filter {
                        eq("uuid", uuid)
                    }
                }
                .decodeSingle<UserDto>()
            
            result.toDomain()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Retrieves a user by email.
     * 
     * @param email User email
     * @return User if found, null otherwise
     */
    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        try {
            val result = SupabaseClientProvider.client
                .from("users")
                .select {
                    filter {
                        eq("email", email)
                    }
                }
                .decodeSingle<UserDto>()
            
            result.toDomain()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Authenticates a user with email and password.
     * 
     * @param email User email
     * @param password User password
     * @return User if authentication successful, null otherwise
     */
    suspend fun authenticateUser(email: String, password: String): User? = withContext(Dispatchers.IO) {
        try {
            val hashedPassword = CryptoUtil.sha256(password)
            val result = SupabaseClientProvider.client
                .from("users")
                .select {
                    filter {
                        eq("email", email)
                        eq("password", hashedPassword)
                    }
                }
                .decodeSingle<UserDto>()
            result.toDomain()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Retrieves all users.
     * 
     * @return List of all users
     */
    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        val results = SupabaseClientProvider.client
            .from("users")
            .select()
            .decodeList<UserDto>()
        
        results.map { it.toDomain() }
    }
    
    // ======================================================================
    // UPDATE
    // ======================================================================
    
    /**
     * Updates an existing user.
     * 
     * @param user User with updated data (must have valid UUID)
     * @return Updated user
     * @throws Exception if update fails
     */
    suspend fun updateUser(user: User): User = withContext(Dispatchers.IO) {
        val dto = user.toDto()
        SupabaseClientProvider.client
            .from("users")
            .update(
                mapOf(
                    "email" to dto.email,
                    "first_name" to dto.first_name,
                    "last_name" to dto.last_name,
                    "address" to dto.address
                )
            ) {
                filter {
                    eq("uuid", user.uuid)
                }
            }
        
        user
    }
    
    /**
     * Updates user password.
     * 
     * @param uuid User UUID
     * @param newPassword New password
     * @throws Exception if update fails
     */
    suspend fun updatePassword(uuid: String, newPassword: String) = withContext(Dispatchers.IO) {
        val hashedPassword = CryptoUtil.sha256(newPassword)
        SupabaseClientProvider.client
            .from("users")
            .update(
                mapOf("password" to hashedPassword)
            ) {
                filter {
                    eq("uuid", uuid)
                }
            }
    }
    
    // ======================================================================
    // DELETE
    // ======================================================================
    
    /**
     * Deletes a user by UUID.
     * 
     * @param uuid User UUID
     * @throws Exception if deletion fails
     */
    suspend fun deleteUser(uuid: String) = withContext(Dispatchers.IO) {
        SupabaseClientProvider.client
            .from("users")
            .delete {
                filter {
                    eq("uuid", uuid)
                }
            }
    }
    
    /**
     * Deletes all users (for testing only).
     * 
     * ⚠️ WARNING: This will delete ALL users in the database!
     */
    suspend fun deleteAllUsers() = withContext(Dispatchers.IO) {
        SupabaseClientProvider.client
            .from("users")
            .delete {
                // No filter = delete all
            }
    }
}
