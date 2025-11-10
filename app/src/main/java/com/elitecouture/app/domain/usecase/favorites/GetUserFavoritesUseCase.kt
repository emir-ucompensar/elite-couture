package com.elitecouture.app.domain.usecase.favorites

import com.elitecouture.app.data.repository.SupabaseFavoriteRepository
import com.elitecouture.app.data.repository.SupabaseProductRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.FavoriteWithProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case for getting all favorites with complete product information for the current user.
 * 
 * @param favoriteRepository Supabase repository for favorites operations
 * @param productRepository Supabase repository for product operations
 * @param sessionManager Session manager to get current user UUID
 */
class GetUserFavoritesUseCase(
    private val favoriteRepository: SupabaseFavoriteRepository,
    private val productRepository: SupabaseProductRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Gets all favorites with complete product details for the current user.
     * Returns an empty list if user is not logged in.
     * 
     * @return List of favorites with product information, ordered by most recent first
     */
    suspend operator fun invoke(): List<FavoriteWithProduct> = withContext(Dispatchers.IO) {
        val userUuid = sessionManager.getUserUuid() ?: return@withContext emptyList()
        
        // Get favorites from Supabase
        val favorites = favoriteRepository.getFavorites(userUuid)
        
        // Manually join with products
        favorites.mapNotNull { favorite ->
            val product = productRepository.getProductByUuid(favorite.productUuid)
            product?.let { FavoriteWithProduct(favorite, it) }
        }
    }
}
