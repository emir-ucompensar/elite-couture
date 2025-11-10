package com.elitecouture.app.domain.usecase.favorites

import android.util.Log
import com.elitecouture.app.data.repository.SupabaseFavoriteRepository
import com.elitecouture.app.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case for removing a product from the current user's favorites.
 * 
 * @param favoriteRepository Supabase repository for favorites operations
 * @param sessionManager Session manager to get current user UUID
 */
class RemoveProductFromFavoritesUseCase(
    private val favoriteRepository: SupabaseFavoriteRepository,
    private val sessionManager: SessionManager
) {
    companion object {
        private const val TAG = "RemoveFromFavoritesUC"
    }
    
    /**
     * Removes the specified product from favorites.
     * 
     * @param productUuid UUID of the product to remove
     * @return true if removed successfully, false if not found or user not logged in
     */
    suspend operator fun invoke(productUuid: String): Boolean = withContext(Dispatchers.IO) {
        val userUuid = sessionManager.getUserUuid()
        Log.d(TAG, "invoke() -> productUuid=$productUuid, userUuid=$userUuid")
        
        if (userUuid == null) {
            Log.w(TAG, "User not logged in, cannot remove from favorites")
            return@withContext false
        }
        
        try {
            favoriteRepository.removeFavorite(userUuid, productUuid)
            Log.d(TAG, "invoke() -> Successfully removed from favorites")
            true
        } catch (e: Exception) {
            Log.e(TAG, "invoke() -> Error removing from favorites", e)
            false
        }
    }
}
