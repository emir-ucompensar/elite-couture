package com.elitecouture.app.data.local.entity

import com.elitecouture.app.domain.model.User

/** Maps the users table to a Kotlin representation for easier conversions. */
data class UserEntity(
    val id: Long,
    val uuid: String,
    val email: String,
    val password: String?,
    val firstName: String,
    val lastName: String?,
    val address: String?,
    val isGuest: Boolean,
    val createdAt: Long
) {
    fun toDomain(): User = User(
        id = id,
        uuid = uuid,
        email = email,
        firstName = firstName,
        lastName = lastName,
        address = address,
        isGuest = isGuest,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(user: User, password: String?): UserEntity = UserEntity(
            id = user.id,
            uuid = user.uuid,
            email = user.email,
            password = password,
            firstName = user.firstName,
            lastName = user.lastName,
            address = user.address,
            isGuest = user.isGuest,
            createdAt = user.createdAt
        )
    }
}
