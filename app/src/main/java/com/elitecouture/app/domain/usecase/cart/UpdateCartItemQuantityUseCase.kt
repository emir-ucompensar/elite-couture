package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.local.dao.CartDao
import com.elitecouture.app.data.session.SessionManager

/**
 * Use case for updating the quantity of a product in the shopping cart.
 * 
 * @param cartDao DAO for cart operations
 * @param sessionManager Session manager to get current user UUID
 */
class UpdateCartItemQuantityUseCase(
    private val cartDao: CartDao,
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
    operator fun invoke(productUuid: String, newQuantity: Int): Int {
        val userUuid = sessionManager.getUserUuid() ?: return 0
        return cartDao.updateQuantity(userUuid, productUuid, newQuantity)
    }
}
