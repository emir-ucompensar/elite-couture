package com.elitecouture.app.ui.feature.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.elitecouture.app.MainActivity
import com.elitecouture.app.R
import com.elitecouture.app.di.ServiceLocator
import com.elitecouture.app.ui.common.base.BaseActivity

/**
 * SplashActivity - Pantalla de bienvenida de la aplicación.
 * 
 * Responsabilidades:
 * - Mostrar logo y animación de carga
 * - Inicializar recursos necesarios
 * - Verificar autenticación (delegado al ViewModel)
 * - Navegar a MainActivity cuando esté listo
 * 
 * Toda la lógica está en SplashViewModel, esta Activity solo muestra UI
 * y reacciona a los cambios de estado.
 */
class SplashActivity : BaseActivity<ViewBinding>(R.layout.activity_splash) {
    
    // Inicializar ViewModel usando lazy para que esté disponible antes de setupObservers()
    private val viewModel: SplashViewModel by lazy {
        val sessionManager = ServiceLocator.provideSessionManager(this)
        val factory = SplashViewModelFactory(sessionManager)
        ViewModelProvider(this, factory)[SplashViewModel::class.java]
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Iniciar proceso de carga
        viewModel.startLoading()
    }
    
    /**
     * Observa el estado del splash para navegar cuando esté listo.
     */
    override fun setupObservers() {
        viewModel.splashState.observe(this) { state ->
            when (state) {
                is SplashViewModel.SplashState.Loading -> {
                    // Mostrar animación de carga (ya visible en el layout)
                }
                is SplashViewModel.SplashState.NavigateToMain -> {
                    navigateToMain()
                }
            }
        }
    }
    
    /**
     * Navega a MainActivity y finaliza SplashActivity.
     */
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
