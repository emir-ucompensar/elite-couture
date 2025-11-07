package com.elitecouture.app.domain.model

/**
 * Domain representation of a cart item.
 * Represents a product in a user's shopping cart with quantity.
 */
data class CartItem(
    val id: Long = 0,
    val userUuid: String,
    val productUuid: String,
    val quantity: Int,
    val addedAt: Long
)
