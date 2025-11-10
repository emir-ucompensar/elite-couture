package com.elitecouture.app.ui.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elitecouture.app.data.session.SessionManager

/**
 * Factory para crear instancias de SplashViewModel.
 */
class SplashViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
