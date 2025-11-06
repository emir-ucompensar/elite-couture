package com.elitecouture.app.domain.usecase.favorites

import android.util.Log
import com.elitecouture.app.data.local.dao.FavoriteDao
import com.elitecouture.app.data.session.SessionManager

/**
 * Use case for adding a product to the current user's favorites.
 * 
 * @param favoriteDao DAO for favorites operations
 * @param sessionManager Session manager to get current user UUID
 */
class AddProductToFavoritesUseCase(
    private val favoriteDao: FavoriteDao,
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
    operator fun invoke(productUuid: String): Boolean {
        val userUuid = sessionManager.getUserUuid()
        Log.d(TAG, "invoke() -> productUuid=$productUuid, userUuid=$userUuid")
        
        if (userUuid == null) {
            Log.w(TAG, "User not logged in, cannot add to favorites")
            return false
        }
        
        // Check if already favorited to avoid unnecessary database call
        if (favoriteDao.isFavorite(userUuid, productUuid)) {
            Log.w(TAG, "Product already in favorites")
            return false
        }
        
        val result = favoriteDao.addFavorite(userUuid, productUuid)
        val success = result != -1L
        Log.d(TAG, "invoke() -> result=$result, success=$success")
        return success
    }
}
