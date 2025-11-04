package com.elitecouture.app.domain.usecase

import com.elitecouture.app.data.repository.AuthRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User

class LoginUserUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) {
    operator fun invoke(email: String, password: String): Result<User> {
        val result = authRepository.login(email, password)
        result.getOrNull()?.let { user ->
            sessionManager.setActiveUserId(user.id)
            sessionManager.setGuestModeEnabled(false)
        }
        return result
    }
}
