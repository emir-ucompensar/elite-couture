package com.elitecouture.app.domain.usecase.favorites

import com.elitecouture.app.data.repository.SupabaseFavoriteRepository
import com.elitecouture.app.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case for checking if a product is in the current user's favorites.
 * 
 * @param favoriteRepository Supabase repository for favorites operations
 * @param sessionManager Session manager to get current user UUID
 */
class IsProductFavoriteUseCase(
    private val favoriteRepository: SupabaseFavoriteRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Checks if the specified product is favorited by the current user.
     * 
     * @param productUuid UUID of the product to check
     * @return true if the product is favorited, false otherwise or if user not logged in
     */
    suspend operator fun invoke(productUuid: String): Boolean = withContext(Dispatchers.IO) {
        val userUuid = sessionManager.getUserUuid() ?: return@withContext false
        favoriteRepository.isFavorite(userUuid, productUuid)
    }
}
