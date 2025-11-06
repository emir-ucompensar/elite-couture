package com.elitecouture.app.data.local.entity

import com.elitecouture.app.domain.model.Favorite

/**
 * SQLite-backed representation of a favorite product.
 * Maps to the 'favorites' table in the database.
 */
data class FavoriteEntity(
    val id: Long,
    val userUuid: String,
    val productUuid: String,
    val createdAt: Long
) {
    /**
     * Converts this entity to a domain model.
     */
    fun toDomain(): Favorite = Favorite(
        id = id,
        userUuid = userUuid,
        productUuid = productUuid,
        createdAt = createdAt
    )

    companion object {
        /**
         * Creates an entity from a domain model.
         */
        fun fromDomain(favorite: Favorite): FavoriteEntity = FavoriteEntity(
            id = favorite.id,
            userUuid = favorite.userUuid,
            productUuid = favorite.productUuid,
            createdAt = favorite.createdAt
        )
    }
}
