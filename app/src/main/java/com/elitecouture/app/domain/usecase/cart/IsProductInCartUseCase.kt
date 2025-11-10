package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.repository.SupabaseCartRepository
import com.elitecouture.app.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case to check if a product is in the user's cart.
 * 
 * @param cartRepository Supabase repository for cart operations
 * @param sessionManager Session manager to get current user
 */
class IsProductInCartUseCase(
    private val cartRepository: SupabaseCartRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Checks if a product is in the current user's cart.
     * 
     * @param productUuid UUID of the product to check
     * @return true if product is in cart, false otherwise (or if user not logged in)
     */
    suspend operator fun invoke(productUuid: String): Boolean = withContext(Dispatchers.IO) {
        val userUuid = sessionManager.getUserUuid() ?: return@withContext false
        try {
            val cartItem = cartRepository.getCartItem(userUuid, productUuid)
            cartItem != null
        } catch (e: Exception) {
            false
        }
    }
}
