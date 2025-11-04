package com.elitecouture.app.domain.usecase

import com.elitecouture.app.data.repository.AuthRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.model.User

class EnableGuestAccessUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) {
    operator fun invoke(): User {
        val guestProfile = authRepository.createGuestProfile()
        sessionManager.setGuestModeEnabled(true)
        sessionManager.setActiveUserId(guestProfile.id)
        return guestProfile
    }
}
