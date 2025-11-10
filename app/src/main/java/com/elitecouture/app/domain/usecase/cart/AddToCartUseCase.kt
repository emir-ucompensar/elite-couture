package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.repository.SupabaseCartRepository
import com.elitecouture.app.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case for adding a product to the shopping cart.
 * 
 * @param cartRepository Supabase repository for cart operations
 * @param sessionManager Session manager to get current user UUID
 */
class AddToCartUseCase(
    private val cartRepository: SupabaseCartRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Adds a product to the cart with specified quantity.
     * If the product already exists, increases the quantity.
     * Returns the ID of the cart item, or -1 if user is not logged in.
     * 
     * @param productUuid UUID of the product to add
     * @param quantity Quantity to add (default: 1)
     * @return Cart item ID or -1 if failed
     */
    suspend operator fun invoke(productUuid: String, quantity: Int = 1): Long = withContext(Dispatchers.IO) {
        val userUuid = sessionManager.getUserUuid() ?: return@withContext -1L
        try {
            val cartItem = cartRepository.addToCart(userUuid, productUuid, quantity)
            cartItem.id
        } catch (e: Exception) {
            -1L
        }
    }
}
