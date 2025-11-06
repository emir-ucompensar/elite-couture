package com.elitecouture.app.domain.usecase.favorites

import com.elitecouture.app.data.local.dao.FavoriteDao
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.FavoriteWithProduct

/**
 * Use case for getting all favorites with complete product information for the current user.
 * 
 * @param favoriteDao DAO for favorites operations
 * @param sessionManager Session manager to get current user UUID
 */
class GetUserFavoritesUseCase(
    private val favoriteDao: FavoriteDao,
    private val sessionManager: SessionManager
) {
    /**
     * Gets all favorites with complete product details for the current user.
     * Returns an empty list if user is not logged in.
     * 
     * @return List of favorites with product information, ordered by most recent first
     */
    operator fun invoke(): List<FavoriteWithProduct> {
        val userUuid = sessionManager.getUserUuid() ?: return emptyList()
        return favoriteDao.getFavoritesWithProducts(userUuid)
    }
}
