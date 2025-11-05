package com.elitecouture.app.ui.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elitecouture.app.data.session.SessionManager

/**
 * Factory para crear instancias de MainViewModel.
 * 
 * Necesario porque MainViewModel requiere dependencias que no puede
 * proporcionar el constructor por defecto.
 * 
 * @property sessionManager instancia de SessionManager para inyectar
 */
class MainViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
