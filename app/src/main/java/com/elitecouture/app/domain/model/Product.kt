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
    val imageUrl: String?,
    val isVisibleToGuest: Boolean = true
)
