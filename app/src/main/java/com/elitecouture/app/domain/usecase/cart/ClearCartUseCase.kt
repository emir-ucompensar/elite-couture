package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.local.dao.CartDao
import com.elitecouture.app.data.session.SessionManager

/**
 * Use case for clearing all items from the shopping cart.
 * 
 * @param cartDao DAO for cart operations
 * @param sessionManager Session manager to get current user UUID
 */
class ClearCartUseCase(
    private val cartDao: CartDao,
    private val sessionManager: SessionManager
) {
    /**
     * Clears all items from the cart.
     * Returns the number of rows deleted, or 0 if user is not logged in.
     * 
     * @return Number of rows deleted
     */
    operator fun invoke(): Int {
        val userUuid = sessionManager.getUserUuid() ?: return 0
        return cartDao.clearCart(userUuid)
    }
}
