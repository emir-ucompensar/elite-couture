package com.elitecouture.app.domain.model

/**
 * Domain representation of a favorite product.
 * Represents the relationship between a user and their favorite products.
 */
data class Favorite(
    val id: Long = 0,
    val userUuid: String,
    val productUuid: String,
    val createdAt: Long
)
