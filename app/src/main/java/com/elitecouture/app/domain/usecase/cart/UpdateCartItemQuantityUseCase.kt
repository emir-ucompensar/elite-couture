package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.repository.SupabaseCartRepository
import com.elitecouture.app.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case for updating the quantity of a product in the shopping cart.
 * 
 * @param cartRepository Supabase repository for cart operations
 * @param sessionManager Session manager to get current user UUID
 */
class UpdateCartItemQuantityUseCase(
    private val cartRepository: SupabaseCartRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Updates the quantity of a cart item.
     * Returns the number of rows updated, or 0 if user is not logged in.
     * 
     * @param productUuid UUID of the product to update
     * @param newQuantity New quantity value
     * @return Number of rows updated
     */
    suspend operator fun invoke(productUuid: String, newQuantity: Int): Int = withContext(Dispatchers.IO) {
        val userUuid = sessionManager.getUserUuid() ?: return@withContext 0
        try {
            cartRepository.updateQuantity(userUuid, productUuid, newQuantity)
            1 // Supabase doesn't return affected rows, assume success
        } catch (e: Exception) {
            0
        }
    }
}
