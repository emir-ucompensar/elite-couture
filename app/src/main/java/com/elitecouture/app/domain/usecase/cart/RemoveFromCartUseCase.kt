package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.local.dao.CartDao
import com.elitecouture.app.data.session.SessionManager

/**
 * Use case for removing a product from the shopping cart.
 * 
 * @param cartDao DAO for cart operations
 * @param sessionManager Session manager to get current user UUID
 */
class RemoveFromCartUseCase(
    private val cartDao: CartDao,
    private val sessionManager: SessionManager
) {
    /**
     * Removes a product from the cart.
     * Returns the number of rows deleted, or 0 if user is not logged in.
     * 
     * @param productUuid UUID of the product to remove
     * @return Number of rows deleted
     */
    operator fun invoke(productUuid: String): Int {
        val userUuid = sessionManager.getUserUuid() ?: return 0
        return cartDao.removeFromCart(userUuid, productUuid)
    }
}
