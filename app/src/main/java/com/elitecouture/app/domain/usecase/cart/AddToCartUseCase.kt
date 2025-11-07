package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.local.dao.CartDao
import com.elitecouture.app.data.session.SessionManager

/**
 * Use case for adding a product to the shopping cart.
 * 
 * @param cartDao DAO for cart operations
 * @param sessionManager Session manager to get current user UUID
 */
class AddToCartUseCase(
    private val cartDao: CartDao,
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
    operator fun invoke(productUuid: String, quantity: Int = 1): Long {
        val userUuid = sessionManager.getUserUuid() ?: return -1
        return cartDao.addToCart(userUuid, productUuid, quantity)
    }
}
