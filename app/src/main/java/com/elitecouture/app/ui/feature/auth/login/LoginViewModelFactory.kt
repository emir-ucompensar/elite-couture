package com.elitecouture.app.ui.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elitecouture.app.domain.usecase.auth.EnableGuestAccessUseCase
import com.elitecouture.app.domain.usecase.auth.LoginUserUseCase

/**
 * Factory para crear instancias de LoginViewModel.
 */
class LoginViewModelFactory(
    private val loginUserUseCase: LoginUserUseCase,
    private val enableGuestAccessUseCase: EnableGuestAccessUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginUserUseCase, enableGuestAccessUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
