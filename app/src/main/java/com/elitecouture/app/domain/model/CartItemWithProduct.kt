package com.elitecouture.app.domain.model

/**
 * Domain representation of a cart item with complete product information.
 * Used to display cart items with product details.
 */
data class CartItemWithProduct(
    val cartItem: CartItem,
    val product: Product
)
