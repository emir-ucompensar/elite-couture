package com.elitecouture.app.domain.usecase

import com.elitecouture.app.data.repository.AuthRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User

class RegisterUserUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) {
    operator fun invoke(user: User, password: String): Result<User> {
        val result = authRepository.register(user, password)
        result.getOrNull()?.let { created ->
            sessionManager.setActiveUserId(created.id)
            sessionManager.setGuestModeEnabled(false)
        }
        return result
    }
}
