package com.elitecouture.app.data.local.entity

import com.elitecouture.app.domain.model.CartItem

/**
 * SQLite-backed representation of a cart item.
 * Maps to the 'cart_items' table in the database.
 */
data class CartItemEntity(
    val id: Long,
    val userUuid: String,
    val productUuid: String,
    val quantity: Int,
    val addedAt: Long
) {
    /**
     * Converts this entity to a domain model.
     */
    fun toDomain(): CartItem = CartItem(
        id = id,
        userUuid = userUuid,
        productUuid = productUuid,
        quantity = quantity,
        addedAt = addedAt
    )

    companion object {
        /**
         * Creates an entity from a domain model.
         */
        fun fromDomain(cartItem: CartItem): CartItemEntity = CartItemEntity(
            id = cartItem.id,
            userUuid = cartItem.userUuid,
            productUuid = cartItem.productUuid,
            quantity = cartItem.quantity,
            addedAt = cartItem.addedAt
        )
    }
}
