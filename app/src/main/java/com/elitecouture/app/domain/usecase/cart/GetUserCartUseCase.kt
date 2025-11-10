package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.repository.SupabaseCartRepository
import com.elitecouture.app.data.repository.SupabaseProductRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.CartItemWithProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case for getting all cart items with complete product information for the current user.
 * 
 * @param cartRepository Supabase repository for cart operations
 * @param productRepository Supabase repository for product operations
 * @param sessionManager Session manager to get current user UUID
 */
class GetUserCartUseCase(
    private val cartRepository: SupabaseCartRepository,
    private val productRepository: SupabaseProductRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Gets all cart items with complete product details for the current user.
     * Returns an empty list if user is not logged in.
     * 
     * @return List of cart items with product information, ordered by oldest first
     */
    suspend operator fun invoke(): List<CartItemWithProduct> = withContext(Dispatchers.IO) {
        val userUuid = sessionManager.getUserUuid() ?: return@withContext emptyList()
        
        try {
            val cartItems = cartRepository.getCartItems(userUuid)
            
            // Map cart items to CartItemWithProduct by fetching product details
            cartItems.mapNotNull { cartItem ->
                val product = productRepository.getProductByUuid(cartItem.productUuid)
                product?.let {
                    CartItemWithProduct(cartItem, it)
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
