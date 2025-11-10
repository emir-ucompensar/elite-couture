package com.elitecouture.app.domain.usecase.favorites

import android.util.Log
import com.elitecouture.app.data.repository.SupabaseFavoriteRepository
import com.elitecouture.app.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case for adding a product to the current user's favorites.
 * 
 * @param favoriteRepository Supabase repository for favorites operations
 * @param sessionManager Session manager to get current user UUID
 */
class AddProductToFavoritesUseCase(
    private val favoriteRepository: SupabaseFavoriteRepository,
    private val sessionManager: SessionManager
) {
    companion object {
        private const val TAG = "AddToFavoritesUseCase"
    }
    
    /**
     * Adds the specified product to favorites.
     * 
     * @param productUuid UUID of the product to add
     * @return true if added successfully, false if already exists or user not logged in
     */
    suspend operator fun invoke(productUuid: String): Boolean = withContext(Dispatchers.IO) {
        val userUuid = sessionManager.getUserUuid()
        Log.d(TAG, "invoke() -> productUuid=$productUuid, userUuid=$userUuid")
        
        if (userUuid == null) {
            Log.w(TAG, "User not logged in, cannot add to favorites")
            return@withContext false
        }
        
        // Check if already favorited to avoid unnecessary database call
        if (favoriteRepository.isFavorite(userUuid, productUuid)) {
            Log.w(TAG, "Product already in favorites")
            return@withContext false
        }
        
        try {
            val favorite = favoriteRepository.addFavorite(userUuid, productUuid)
            Log.d(TAG, "invoke() -> Added favorite: ${favorite.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "invoke() -> Error adding favorite", e)
            false
        }
    }
}
