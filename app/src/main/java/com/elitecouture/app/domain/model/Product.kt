package com.elitecouture.app.domain.model

/** Domain representation of a product in the catalog. */
data class Product(
    val id: Long = 0,
    val uuid: String,
    val name: String,
    val description: String?,
    val type: String?,
    val gender: String?,
    val price: Double,
    val stock: Int,
    val images: List<String> = emptyList(), // Lista de rutas de imágenes (ej: ["product_01_img_1.webp", ...])
    val tags: List<String> = emptyList(), // Tags para categorización y filtrado (ej: ["Vestido", "Mujer", "Elegante"])
    val isVisibleToGuest: Boolean = true
)
