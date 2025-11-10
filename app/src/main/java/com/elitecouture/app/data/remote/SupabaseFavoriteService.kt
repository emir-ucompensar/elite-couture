package com.elitecouture.app.data.remote

import com.elitecouture.app.domain.model.Favorite
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

/**
 * Supabase service for favorite operations (CRUD).
 * 
 * This service handles all favorite-related operations with Supabase PostgreSQL,
 * replacing the local SQLite implementation.
 */
object SupabaseFavoriteService {
    
    /**
     * Data class representing the favorites table schema in Supabase.
     */
    @Serializable
    data class FavoriteDto(
        val id: Long? = null,
        val user_uuid: String,
        val product_uuid: String,
        val created_at: Long = System.currentTimeMillis()
    )
    
    /**
     * Converts domain model to DTO for Supabase operations.
     */
    private fun Favorite.toDto(): FavoriteDto = FavoriteDto(
        id = if (id == 0L) null else id,
        user_uuid = userUuid,
        product_uuid = productUuid,
        created_at = createdAt
    )
    
    /**
     * Converts DTO to domain model.
     */
    private fun FavoriteDto.toDomain(): Favorite = Favorite(
        id = id ?: 0L,
        userUuid = user_uuid,
        productUuid = product_uuid,
        createdAt = created_at
    )
    
    // ======================================================================
    // CREATE
    // ======================================================================
    
    /**
     * Adds a product to user's favorites.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @return The created favorite
     * @throws Exception if creation fails (e.g., already exists)
     */
    suspend fun addFavorite(userUuid: String, productUuid: String): Favorite = withContext(Dispatchers.IO) {
        val dto = FavoriteDto(
            user_uuid = userUuid,
            product_uuid = productUuid
        )
        
        val result = SupabaseClientProvider.client
            .from("favorites")
            .insert(dto) {
                select()
            }
            .decodeSingle<FavoriteDto>()
        
        result.toDomain()
    }
    
    // ======================================================================
    // READ
    // ======================================================================
    
    /**
     * Retrieves all favorites for a user.
     * 
     * @param userUuid User UUID
     * @return List of favorites
     */
    suspend fun getFavorites(userUuid: String): List<Favorite> = withContext(Dispatchers.IO) {
        val results = SupabaseClientProvider.client
            .from("favorites")
            .select {
                filter {
                    eq("user_uuid", userUuid)
                }
            }
            .decodeList<FavoriteDto>()
        
        results.map { it.toDomain() }
    }
    
    /**
     * Checks if a product is favorited by a user.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @return true if favorited, false otherwise
     */
    suspend fun isFavorite(userUuid: String, productUuid: String): Boolean = withContext(Dispatchers.IO) {
        try {
            SupabaseClientProvider.client
                .from("favorites")
                .select {
                    filter {
                        eq("user_uuid", userUuid)
                        eq("product_uuid", productUuid)
                    }
                }
                .decodeSingle<FavoriteDto>()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Gets favorite count for a product.
     * 
     * @param productUuid Product UUID
     * @return Number of users who favorited this product
     */
    suspend fun getFavoriteCount(productUuid: String): Int = withContext(Dispatchers.IO) {
        val results = SupabaseClientProvider.client
            .from("favorites")
            .select {
                filter {
                    eq("product_uuid", productUuid)
                }
            }
            .decodeList<FavoriteDto>()
        
        results.size
    }
    
    // ======================================================================
    // DELETE
    // ======================================================================
    
    /**
     * Removes a product from user's favorites.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @throws Exception if deletion fails
     */
    suspend fun removeFavorite(userUuid: String, productUuid: String) = withContext(Dispatchers.IO) {
        SupabaseClientProvider.client
            .from("favorites")
            .delete {
                filter {
                    eq("user_uuid", userUuid)
                    eq("product_uuid", productUuid)
                }
            }
    }
    
    /**
     * Removes all favorites for a user.
     * 
     * @param userUuid User UUID
     * @throws Exception if deletion fails
     */
    suspend fun clearFavorites(userUuid: String) = withContext(Dispatchers.IO) {
        SupabaseClientProvider.client
            .from("favorites")
            .delete {
                filter {
                    eq("user_uuid", userUuid)
                }
            }
    }
}
