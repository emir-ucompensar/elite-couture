/**
 * MainActivity - Coordinador principal de la aplicación.
 * 
 * Responsabilidades:
 * - Configurar navegación con Navigation Component
 * - Delegar toda lógica de negocio al ViewModel
 * 
 * Esta Activity es el "corazón" de la app pero NO contiene lógica crítica,
 * solo coordina componentes de navegación.
 */
 
package com.elitecouture.app

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.elitecouture.app.di.ServiceLocator
import com.elitecouture.app.ui.common.base.BaseActivity
import com.elitecouture.app.ui.feature.main.MainViewModel
import com.elitecouture.app.ui.feature.main.MainViewModelFactory
import androidx.viewbinding.ViewBinding

class MainActivity : BaseActivity<ViewBinding>(R.layout.activity_main) {
    
    // ViewModel que contiene toda la lógica - inicializado con lazy para disponibilidad temprana
    private val viewModel: MainViewModel by lazy {
        val sessionManager = ServiceLocator.provideSessionManager(this)
        val factory = MainViewModelFactory(sessionManager)
        ViewModelProvider(this, factory)[MainViewModel::class.java]
    }
    
    // Componentes de navegación
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // BaseActivity ya hace setContentView() con el layoutResId
        
        // Configurar navegación
        setupNavigation()
        
        // Configurar navegación inicial solo en primera creación
        if (savedInstanceState == null) {
            setupInitialNavigation()
        }
    }
    
    // Configura el Navigation Component y sus listeners.     
    private fun setupNavigation() {
        // Obtener NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }
    
    // Configura la navegación inicial según el estado de sesión.
    private fun setupInitialNavigation() {
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        val startDestination = viewModel.getStartDestination()
        graph.setStartDestination(startDestination)
        navController.setGraph(graph, intent.extras)
    }

    // Configura los observers del ViewModel.
    override fun setupObservers() {
    }
}
