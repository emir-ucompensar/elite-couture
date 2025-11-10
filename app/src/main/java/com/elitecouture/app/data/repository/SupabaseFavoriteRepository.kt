package com.elitecouture.app.data.repository

import com.elitecouture.app.data.remote.SupabaseFavoriteService
import com.elitecouture.app.domain.model.Favorite

/**
 * Favorite repository using Supabase as backend.
 * 
 * This repository replaces the local SQLite implementation with cloud-based storage.
 * All operations are now asynchronous and use Kotlin coroutines.
 */
class SupabaseFavoriteRepository {
    
    /**
     * Adds a product to user's favorites.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @return Created favorite
     */
    suspend fun addFavorite(userUuid: String, productUuid: String): Favorite {
        return SupabaseFavoriteService.addFavorite(userUuid, productUuid)
    }
    
    /**
     * Retrieves all favorites for a user.
     * 
     * @param userUuid User UUID
     * @return List of favorites
     */
    suspend fun getFavorites(userUuid: String): List<Favorite> {
        return SupabaseFavoriteService.getFavorites(userUuid)
    }
    
    /**
     * Checks if a product is favorited by a user.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @return true if favorited, false otherwise
     */
    suspend fun isFavorite(userUuid: String, productUuid: String): Boolean {
        return SupabaseFavoriteService.isFavorite(userUuid, productUuid)
    }
    
    /**
     * Gets favorite count for a product.
     * 
     * @param productUuid Product UUID
     * @return Number of users who favorited this product
     */
    suspend fun getFavoriteCount(productUuid: String): Int {
        return SupabaseFavoriteService.getFavoriteCount(productUuid)
    }
    
    /**
     * Removes a product from user's favorites.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     */
    suspend fun removeFavorite(userUuid: String, productUuid: String) {
        SupabaseFavoriteService.removeFavorite(userUuid, productUuid)
    }
    
    /**
     * Removes all favorites for a user.
     * 
     * @param userUuid User UUID
     */
    suspend fun clearFavorites(userUuid: String) {
        SupabaseFavoriteService.clearFavorites(userUuid)
    }
}
