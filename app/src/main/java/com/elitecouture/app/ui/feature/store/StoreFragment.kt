package com.elitecouture.app.ui.feature.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.elitecouture.app.R
import com.elitecouture.app.di.ServiceLocator
import com.elitecouture.app.ui.common.EliteCoutureDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elitecouture.app.domain.model.Product
import com.elitecouture.app.ui.feature.store.ProductListAdapter
import com.elitecouture.app.data.seed.DatabaseSeeder
import com.elitecouture.app.ui.common.extension.showStyledSnackbar
import kotlinx.coroutines.launch

class StoreFragment : Fragment() {
    private val sessionManager by lazy { ServiceLocator.provideSessionManager(requireContext()) }
    private lateinit var bottomNavigation: BottomNavigationView
    private val storeViewModel: StoreViewModel by activityViewModels()
    private lateinit var recyclerProducts: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feature_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        bottomNavigation = view.findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_store
    recyclerProducts = view.findViewById(R.id.recycler_products)
    recyclerProducts.layoutManager = LinearLayoutManager(requireContext())
        
        // Configurar restricciones para modo invitado
        configureGuestModeRestrictions()

    // Inicializar lista de productos
    initProductList()
        
        // Configurar interceptor de botón back
        setupBackPressHandler()
        
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_store -> {
                    // Ya estamos en la tienda
                    true
                }
                R.id.navigation_cart -> {
                    if (sessionManager.isGuestMode()) {
                        requireView().showStyledSnackbar(getString(R.string.toast_guest_restricted_feature))
                        false
                    } else {
                        requireView().showStyledSnackbar(getString(R.string.toast_cart_under_construction))
                        true
                    }
                }
                R.id.navigation_profile -> {
                    if (sessionManager.isGuestMode()) {
                        requireView().showStyledSnackbar(getString(R.string.toast_guest_restricted_feature))
                        false
                    } else {
                        findNavController().navigate(R.id.action_storeFragment_to_profileFragment)
                        true
                    }
                }
                else -> false
            }
        }
    }

    private fun initProductList() {
        val seeder: DatabaseSeeder = ServiceLocator.provideDatabaseSeeder(requireContext())

        // Seed database in background if needed, then load products on main thread
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                try {
                    seeder.seedDatabaseIfNeeded()
                } catch (e: Exception) {
                    android.util.Log.e("StoreFragment", "Error seeding database", e)
                }

                val products = ServiceLocator.provideGetProductCatalogUseCase(requireContext())()
                
                // Proporcionar use cases de favoritos
                val addToFavoritesUseCase = ServiceLocator.provideAddProductToFavoritesUseCase(requireContext())
                val removeFromFavoritesUseCase = ServiceLocator.provideRemoveProductFromFavoritesUseCase(requireContext())
                val isProductFavoriteUseCase = ServiceLocator.provideIsProductFavoriteUseCase(requireContext())
                
                recyclerProducts.adapter = ProductListAdapter(
                    items = products,
                    addToFavoritesUseCase = addToFavoritesUseCase,
                    removeFromFavoritesUseCase = removeFromFavoritesUseCase,
                    isProductFavoriteUseCase = isProductFavoriteUseCase,
                    onNavigateToFavorites = {
                        // Navegar a favoritos
                        findNavController().navigate(R.id.action_storeFragment_to_favoritesFragment)
                    }
                )
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Reconfigurar restricciones cada vez que el fragment se vuelve visible
        configureGuestModeRestrictions()
        // Asegurar que el ítem correcto esté seleccionado al volver a este fragmento
        bottomNavigation.selectedItemId = R.id.navigation_store
    }
    
    private fun configureGuestModeRestrictions() {
        val isGuest = sessionManager.isGuestMode()
        android.util.Log.d("StoreFragment", "=== CONFIGURANDO RESTRICCIONES ===")
        android.util.Log.d("StoreFragment", "isGuestMode: $isGuest")
        
        // Colores para íconos y texto
        val disabledColor = resources.getColor(R.color.text_disabled_pink, null) // Gris rosado
        val normalColor = resources.getColor(R.color.color_primary, null)
        
        android.util.Log.d("StoreFragment", "Colores - Normal: $normalColor, Disabled: $disabledColor")
        
        if (isGuest) {
            // MODO INVITADO: Aplicar ColorStateList personalizado para íconos y texto
            
            // ColorStateList para íconos (gris rosado cuando no seleccionado)
            val guestColorStateList = android.content.res.ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked), // Estado seleccionado
                    intArrayOf() // Estado normal (no seleccionado)
                ),
                intArrayOf(
                    normalColor,    // Ítem seleccionado: color normal (rojo)
                    disabledColor   // Ítems no seleccionados: gris rosado
                )
            )
            
            // Aplicar el mismo ColorStateList a íconos Y texto
            bottomNavigation.itemIconTintList = guestColorStateList
            bottomNavigation.itemTextColor = guestColorStateList
            
            android.util.Log.d("StoreFragment", "Modo invitado - ColorStateList aplicado a íconos y texto")
            
        } else {
            // MODO USUARIO NORMAL: ColorStateList normal para todos
            val normalColorStateList = android.content.res.ColorStateList.valueOf(normalColor)
            bottomNavigation.itemIconTintList = normalColorStateList
            bottomNavigation.itemTextColor = normalColorStateList
            
            android.util.Log.d("StoreFragment", "Usuario normal - ColorStateList aplicado")
        }
    }

    private fun setupBackPressHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                EliteCoutureDialog.create(requireContext())
                    .setTitle(R.string.store_exit_dialog_title)
                    .setMessage(R.string.store_exit_dialog_message)
                    .setPositiveButton(R.string.store_exit_dialog_confirm) {
                        requireActivity().finish()
                    }
                    .setNegativeButton(R.string.store_exit_dialog_cancel)
                    .setCancelable(true)
                    .show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}
