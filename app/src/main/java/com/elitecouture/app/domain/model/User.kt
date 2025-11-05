package com.elitecouture.app.domain.model

/** Domain representation of a user, regardless of where it is stored. */
data class User(
    val id: Long = 0,
    val uuid: String,
    val email: String,
    val firstName: String,
    val lastName: String?,
    val address: String? = null,
    val isGuest: Boolean,
    val createdAt: Long,
)
