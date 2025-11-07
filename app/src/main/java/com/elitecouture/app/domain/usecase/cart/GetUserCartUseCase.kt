package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.local.dao.CartDao
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.CartItemWithProduct

/**
 * Use case for getting all cart items with complete product information for the current user.
 * 
 * @param cartDao DAO for cart operations
 * @param sessionManager Session manager to get current user UUID
 */
class GetUserCartUseCase(
    private val cartDao: CartDao,
    private val sessionManager: SessionManager
) {
    /**
     * Gets all cart items with complete product details for the current user.
     * Returns an empty list if user is not logged in.
     * 
     * @return List of cart items with product information, ordered by oldest first
     */
    operator fun invoke(): List<CartItemWithProduct> {
        val userUuid = sessionManager.getUserUuid() ?: return emptyList()
        return cartDao.getCartItemsWithProducts(userUuid)
    }
}
