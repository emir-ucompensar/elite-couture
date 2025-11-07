package com.elitecouture.app.data.local.entity

import com.elitecouture.app.domain.model.Product

/** SQLite-backed representation of a product available in the catalog. */
data class ProductEntity(
    val id: Long,
    val uuid: String,
    val name: String,
    val description: String?,
    val type: String?,
    val gender: String?,
    val price: Double,
    val stock: Int,
    val images: String, // Lista serializada como string separado por '|' (ej: "img1.webp|img2.webp|img3.webp")
    val tags: String, // Tags serializados como string separado por '|' (ej: "Vestido|Mujer|Elegante")
    val isVisibleToGuest: Boolean
) {
    fun toDomain(): Product = Product(
        id = id,
        uuid = uuid,
        name = name,
        description = description,
        type = type,
        gender = gender,
        price = price,
        stock = stock,
        images = if (images.isBlank()) emptyList() else images.split('|'),
        tags = if (tags.isBlank()) emptyList() else tags.split('|'),
        isVisibleToGuest = isVisibleToGuest
    )

    companion object {
        fun fromDomain(product: Product): ProductEntity = ProductEntity(
            id = product.id,
            uuid = product.uuid,
            name = product.name,
            description = product.description,
            type = product.type,
            gender = product.gender,
            price = product.price,
            stock = product.stock,
            images = product.images.joinToString("|"),
            tags = product.tags.joinToString("|"),
            isVisibleToGuest = product.isVisibleToGuest
        )
    }
}
