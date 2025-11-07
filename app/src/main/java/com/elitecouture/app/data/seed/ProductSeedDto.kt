package com.elitecouture.app.data.seed

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for parsing product seed data from JSON.
 * Maps to the structure defined in assets/products_seed.json
 */
@Serializable
data class ProductSeedDto(
    val uuid: String,
    val name: String,
    val description: String? = null,
    val type: String? = null,
    val gender: String? = null,
    val price: Double,
    val stock: Int,
    val images: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val imageResource: String? = null, // Legacy support
    val isVisibleToGuest: Boolean = true
)

/**
 * Wrapper for the root JSON structure containing product list
 */
@Serializable
data class ProductSeedData(
    val products: List<ProductSeedDto>
)
