package com.elitecouture.app.data.repository

import com.elitecouture.app.data.local.dao.UserDao
import com.elitecouture.app.data.local.entity.UserEntity
import com.elitecouture.app.domain.model.User
import java.util.UUID

/**
 * Encapsulates authentication-related operations. Wraps DAO calls so the rest of the app
 * can operate strictly with domain models.
 */
class AuthRepository(private val userDao: UserDao) {
    fun register(user: User, password: String?): Result<User> {
        val normalizedUser = if (user.uuid.isBlank()) {
            user.copy(uuid = UUID.randomUUID().toString())
        } else {
            user
        }

        val userEntity = UserEntity.fromDomain(normalizedUser, password)
        val rowId = userDao.insert(userEntity)
        return if (rowId == -1L) {
            Result.failure(IllegalStateException("Could not insert user"))
        } else {
            userDao.findByEmail(normalizedUser.email)?.toDomain()?.let { Result.success(it) }
                ?: Result.failure(IllegalStateException("User not found after insertion"))
        }
    }

    fun login(email: String, password: String): Result<User> {
        val entity = userDao.authenticate(email, password)
        return if (entity != null) {
            Result.success(entity.toDomain())
        } else {
            Result.failure(IllegalArgumentException("Invalid credentials"))
        }
    }

    fun findByEmail(email: String): User? = userDao.findByEmail(email)?.toDomain()

    fun createGuestProfile(): User {
        val guest = User(
            id = 0L,
            uuid = UUID.randomUUID().toString(),
            email = "guest@elite-couture",
            firstName = "Invitado",
            lastName = null,
            isGuest = true,
            createdAt = System.currentTimeMillis()
        )
        val inserted = userDao.insert(UserEntity.fromDomain(guest, password = null))
        return if (inserted == -1L) {
            guest
        } else {
            guest.copy(id = inserted)
        }
    }
}
