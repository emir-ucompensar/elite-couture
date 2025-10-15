package com.elitecouture.app.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.elitecouture.app.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class StoreFragment : Fragment() {
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        bottomNavigation = view.findViewById(R.id.bottom_navigation)
        
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_store -> {
                    // Ya estamos en la tienda
                    true
                }
                R.id.navigation_cart -> {
                    Toast.makeText(context, "Carrito en construcción", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_profile -> {
                    findNavController().navigate(R.id.action_storeFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }
    }
}
