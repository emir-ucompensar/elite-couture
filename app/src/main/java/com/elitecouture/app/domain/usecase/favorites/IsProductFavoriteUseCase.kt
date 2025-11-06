package com.elitecouture.app.domain.usecase.favorites

import com.elitecouture.app.data.local.dao.FavoriteDao
import com.elitecouture.app.data.session.SessionManager

/**
 * Use case for checking if a product is in the current user's favorites.
 * 
 * @param favoriteDao DAO for favorites operations
 * @param sessionManager Session manager to get current user UUID
 */
class IsProductFavoriteUseCase(
    private val favoriteDao: FavoriteDao,
    private val sessionManager: SessionManager
) {
    /**
     * Checks if the specified product is favorited by the current user.
     * 
     * @param productUuid UUID of the product to check
     * @return true if the product is favorited, false otherwise or if user not logged in
     */
    operator fun invoke(productUuid: String): Boolean {
        val userUuid = sessionManager.getUserUuid() ?: return false
        return favoriteDao.isFavorite(userUuid, productUuid)
    }
}
