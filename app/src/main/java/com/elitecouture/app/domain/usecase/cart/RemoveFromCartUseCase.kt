package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.repository.SupabaseCartRepository
import com.elitecouture.app.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case for removing a product from the shopping cart.
 * 
 * @param cartRepository Supabase repository for cart operations
 * @param sessionManager Session manager to get current user UUID
 */
class RemoveFromCartUseCase(
    private val cartRepository: SupabaseCartRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Removes a product from the cart.
     * Returns the number of rows deleted, or 0 if user is not logged in.
     * 
     * @param productUuid UUID of the product to remove
     * @return Number of rows deleted
     */
    suspend operator fun invoke(productUuid: String): Int = withContext(Dispatchers.IO) {
        val userUuid = sessionManager.getUserUuid() ?: return@withContext 0
        try {
            cartRepository.removeFromCart(userUuid, productUuid)
            1 // Supabase doesn't return affected rows, assume success
        } catch (e: Exception) {
            0
        }
    }
}
