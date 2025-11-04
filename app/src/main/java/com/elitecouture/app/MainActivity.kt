package com.elitecouture.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.elitecouture.app.R
import com.elitecouture.app.di.ServiceLocator

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (savedInstanceState == null) {
            val graph = navController.navInflater.inflate(R.navigation.nav_graph)
            val sessionManager = ServiceLocator.provideSessionManager(this)
            val startDestination = if (sessionManager.isLoggedIn()) {
                R.id.storeFragment
            } else {
                R.id.loginFragment
            }
            graph.setStartDestination(startDestination)
            navController.setGraph(graph, intent.extras)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
