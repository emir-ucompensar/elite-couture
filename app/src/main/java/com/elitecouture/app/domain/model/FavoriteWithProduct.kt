package com.elitecouture.app.domain.model

/**
 * Domain representation of a favorite with complete product information.
 * Used to display favorites list with product details.
 */
data class FavoriteWithProduct(
    val favorite: Favorite,
    val product: Product
)
