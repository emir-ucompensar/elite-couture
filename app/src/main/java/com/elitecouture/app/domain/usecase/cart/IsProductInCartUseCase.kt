package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.local.dao.CartDao
import com.elitecouture.app.data.session.SessionManager

/**
 * Use case to check if a product is in the user's cart.
 * 
 * @param cartDao DAO for cart operations
 * @param sessionManager Session manager to get current user
 */
class IsProductInCartUseCase(
    private val cartDao: CartDao,
    private val sessionManager: SessionManager
) {
    /**
     * Checks if a product is in the current user's cart.
     * 
     * @param productUuid UUID of the product to check
     * @return true if product is in cart, false otherwise (or if user not logged in)
     */
    operator fun invoke(productUuid: String): Boolean {
        val userUuid = sessionManager.getUserUuid() ?: return false
        return cartDao.isProductInCart(userUuid, productUuid)
    }
}
