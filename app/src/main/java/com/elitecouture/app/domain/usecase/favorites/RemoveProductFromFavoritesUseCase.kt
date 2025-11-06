package com.elitecouture.app.domain.usecase.favorites

import android.util.Log
import com.elitecouture.app.data.local.dao.FavoriteDao
import com.elitecouture.app.data.session.SessionManager

/**
 * Use case for removing a product from the current user's favorites.
 * 
 * @param favoriteDao DAO for favorites operations
 * @param sessionManager Session manager to get current user UUID
 */
class RemoveProductFromFavoritesUseCase(
    private val favoriteDao: FavoriteDao,
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
    operator fun invoke(productUuid: String): Boolean {
        val userUuid = sessionManager.getUserUuid()
        Log.d(TAG, "invoke() -> productUuid=$productUuid, userUuid=$userUuid")
        
        if (userUuid == null) {
            Log.w(TAG, "User not logged in, cannot remove from favorites")
            return false
        }
        
        val rowsAffected = favoriteDao.removeFavorite(userUuid, productUuid)
        val success = rowsAffected > 0
        Log.d(TAG, "invoke() -> rowsAffected=$rowsAffected, success=$success")
        return success
    }
}
