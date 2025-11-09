package com.elitecouture.app.ui.feature.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.elitecouture.app.R
import com.elitecouture.app.di.ServiceLocator
import com.elitecouture.app.ui.common.EliteCoutureDialog
import com.google.android.material.appbar.MaterialToolbar
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
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton
import java.text.Normalizer
import java.util.Locale

class StoreFragment : Fragment() {
    private val sessionManager by lazy { ServiceLocator.provideSessionManager(requireContext()) }
    private lateinit var toolbarStore: MaterialToolbar
    private lateinit var bottomNavigation: BottomNavigationView
    private val storeViewModel: StoreViewModel by activityViewModels()
    private lateinit var recyclerProducts: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerCloseButton: MaterialButton
    private lateinit var textActiveFilter: android.widget.TextView
    private lateinit var emptyCategoryState: android.widget.LinearLayout
    private lateinit var textEmptyCategoryTitle: android.widget.TextView
    private lateinit var textEmptyCategoryMessage: android.widget.TextView
    private lateinit var btnExploreProducts: MaterialButton
    // Lista completa de productos y filtro activo
    private var fullProductList: List<Product> = emptyList()
    private var lastFilterTags: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feature_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inicializar vistas
        drawerLayout = view.findViewById(R.id.drawer_layout)
        drawerCloseButton = view.findViewById(R.id.drawer_close_button)
        toolbarStore = view.findViewById(R.id.toolbar_store)
        bottomNavigation = view.findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_store
        recyclerProducts = view.findViewById(R.id.recycler_products)
        recyclerProducts.layoutManager = LinearLayoutManager(requireContext())
        textActiveFilter = view.findViewById(R.id.text_active_filter)
        emptyCategoryState = view.findViewById(R.id.empty_category_state)
        textEmptyCategoryTitle = view.findViewById(R.id.text_empty_category_title)
        textEmptyCategoryMessage = view.findViewById(R.id.text_empty_category_message)
        btnExploreProducts = view.findViewById(R.id.btn_explore_products)
        
        // Configurar botón de estado vacío
        btnExploreProducts.setOnClickListener {
            clearFilter()
        }
        
        // Configurar drawer
        setupDrawer()
        
        // Configurar toolbar
        setupToolbar()
        
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
                        requireView().showStyledSnackbar(
                            message = getString(R.string.toast_guest_restricted_feature),
                            duration = com.google.android.material.snackbar.Snackbar.LENGTH_LONG,
                            actionText = getString(R.string.action_login),
                            actionCallback = {
                                findNavController().navigate(R.id.action_storeFragment_to_loginFragment)
                            }
                        )
                        false
                    } else {
                        findNavController().navigate(R.id.action_storeFragment_to_cartFragment)
                        true
                    }
                }
                R.id.navigation_profile -> {
                    if (sessionManager.isGuestMode()) {
                        requireView().showStyledSnackbar(
                            message = getString(R.string.toast_guest_restricted_feature),
                            duration = com.google.android.material.snackbar.Snackbar.LENGTH_LONG,
                            actionText = getString(R.string.action_login),
                            actionCallback = {
                                findNavController().navigate(R.id.action_storeFragment_to_loginFragment)
                            }
                        )
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
                // Guardar lista completa para filtrados
                fullProductList = products
                
                // Asegurar que el estado vacío esté oculto al inicio
                emptyCategoryState.visibility = View.GONE
                recyclerProducts.visibility = View.VISIBLE
                
                // Proporcionar use cases de favoritos
                val addToFavoritesUseCase = ServiceLocator.provideAddProductToFavoritesUseCase(requireContext())
                val removeFromFavoritesUseCase = ServiceLocator.provideRemoveProductFromFavoritesUseCase(requireContext())
                val isProductFavoriteUseCase = ServiceLocator.provideIsProductFavoriteUseCase(requireContext())
                
                // Proporcionar use case de carrito
                val addToCartUseCase = ServiceLocator.provideAddToCartUseCase(requireContext())
                val isProductInCartUseCase = ServiceLocator.provideIsProductInCartUseCase(requireContext())
                
                recyclerProducts.adapter = ProductListAdapter(
                    items = fullProductList,
                    addToFavoritesUseCase = addToFavoritesUseCase,
                    removeFromFavoritesUseCase = removeFromFavoritesUseCase,
                    isProductFavoriteUseCase = isProductFavoriteUseCase,
                    addToCartUseCase = addToCartUseCase,
                    isProductInCartUseCase = isProductInCartUseCase,
                    isGuestMode = sessionManager.isGuestMode(),
                    onNavigateToFavorites = {
                        // Navegar a favoritos
                        findNavController().navigate(R.id.action_storeFragment_to_favoritesFragment)
                    },
                    onNavigateToCart = {
                        // Navegar al carrito
                        findNavController().navigate(R.id.action_storeFragment_to_cartFragment)
                    }
                )
            }
        }
    }

    // Normaliza texto: quita tildes y pasa a minúsculas para comparar
    private fun normalizeText(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "").lowercase(Locale.getDefault())
    }

    // Chequea que un producto contenga todos los tags (substring match, normalizado)
    private fun productMatchesAllTags(product: Product, tags: List<String>): Boolean {
        if (tags.isEmpty()) return true
        val normalizedProductTags = product.tags.map { normalizeText(it) }
        return tags.all { desiredTag ->
            val nTag = normalizeText(desiredTag)
            normalizedProductTags.any { pt -> pt.contains(nTag) || nTag.contains(pt) }
        }
    }

    // Limpia el filtro y muestra todos los productos
    private fun clearFilter() {
        lastFilterTags = null
        textActiveFilter.visibility = View.GONE
        emptyCategoryState.visibility = View.GONE
        recyclerProducts.visibility = View.VISIBLE
        recyclerProducts.adapter = ProductListAdapter(
            items = fullProductList,
            addToFavoritesUseCase = ServiceLocator.provideAddProductToFavoritesUseCase(requireContext()),
            removeFromFavoritesUseCase = ServiceLocator.provideRemoveProductFromFavoritesUseCase(requireContext()),
            isProductFavoriteUseCase = ServiceLocator.provideIsProductFavoriteUseCase(requireContext()),
            addToCartUseCase = ServiceLocator.provideAddToCartUseCase(requireContext()),
            isProductInCartUseCase = ServiceLocator.provideIsProductInCartUseCase(requireContext()),
            isGuestMode = sessionManager.isGuestMode(),
            onNavigateToFavorites = { findNavController().navigate(R.id.action_storeFragment_to_favoritesFragment) },
            onNavigateToCart = { findNavController().navigate(R.id.action_storeFragment_to_cartFragment) }
        )
        requireView().showStyledSnackbar("Mostrando todos los productos")
        drawerLayout.closeDrawers()
    }

    // Filtra la lista en memoria y reemplaza el adapter (toggle si es el mismo filtro)
    private fun applyFilterToggle(tagsToFilter: List<String>, toastMessage: String) {
        val normalizedRequested = tagsToFilter.map { normalizeText(it) }
        // Si el filtro pedido es igual al activo, lo limpiamos
        if (lastFilterTags != null && lastFilterTags!!.map { normalizeText(it) } == normalizedRequested) {
            clearFilter()
            return
        }

        // Aplicar nuevo filtro
        val filtered = fullProductList.filter { productMatchesAllTags(it, tagsToFilter) }
        lastFilterTags = tagsToFilter
        
        // Mostrar indicador de filtro activo
        textActiveFilter.text = toastMessage.replace("Filtrando: ", "")
        textActiveFilter.visibility = View.VISIBLE
        
        // Verificar si hay productos filtrados
        if (filtered.isEmpty()) {
            // Mostrar estado vacío
            recyclerProducts.visibility = View.GONE
            emptyCategoryState.visibility = View.VISIBLE
            
            // Personalizar mensaje según la categoría
            val categoryName = toastMessage.replace("Filtrando: ", "")
            textEmptyCategoryMessage.text = getString(R.string.empty_category_message, categoryName)
        } else {
            // Mostrar productos
            emptyCategoryState.visibility = View.GONE
            recyclerProducts.visibility = View.VISIBLE
            
            recyclerProducts.adapter = ProductListAdapter(
                items = filtered,
                addToFavoritesUseCase = ServiceLocator.provideAddProductToFavoritesUseCase(requireContext()),
                removeFromFavoritesUseCase = ServiceLocator.provideRemoveProductFromFavoritesUseCase(requireContext()),
                isProductFavoriteUseCase = ServiceLocator.provideIsProductFavoriteUseCase(requireContext()),
                addToCartUseCase = ServiceLocator.provideAddToCartUseCase(requireContext()),
                isProductInCartUseCase = ServiceLocator.provideIsProductInCartUseCase(requireContext()),
                isGuestMode = sessionManager.isGuestMode(),
                onNavigateToFavorites = { findNavController().navigate(R.id.action_storeFragment_to_favoritesFragment) },
                onNavigateToCart = { findNavController().navigate(R.id.action_storeFragment_to_cartFragment) }
            )
            requireView().showStyledSnackbar(toastMessage)
        }
        
        drawerLayout.closeDrawers()
    }

    private fun setupDrawer() {
        // Botón de cierre del drawer
        drawerCloseButton.setOnClickListener {
            drawerLayout.closeDrawers()
        }
        
        // Cerrar drawer al tocar fuera del área del menú
        drawerLayout.setScrimColor(android.graphics.Color.parseColor("#80000000"))
        
        // Configurar menús desplegables
        setupMenuExpanders()
    }
    
    private fun setupMenuExpanders() {
        // Referencias a las vistas del menú Hombre
        val menHeaderView = view?.findViewById<View>(R.id.menu_men_header)
        val menItemsView = view?.findViewById<View>(R.id.menu_men_items)
        val menIconView = view?.findViewById<android.widget.ImageView>(R.id.menu_men_icon)
        
        // Referencias a las vistas del menú Mujer
        val womenHeaderView = view?.findViewById<View>(R.id.menu_women_header)
        val womenItemsView = view?.findViewById<View>(R.id.menu_women_items)
        val womenIconView = view?.findViewById<android.widget.ImageView>(R.id.menu_women_icon)
        
        // Toggle del menú Hombre
        menHeaderView?.setOnClickListener {
            if (menItemsView?.visibility == View.VISIBLE) {
                menItemsView.visibility = View.GONE
                menIconView?.rotation = 0f
            } else {
                menItemsView?.visibility = View.VISIBLE
                menIconView?.rotation = 180f
            }
        }
        
        // Toggle del menú Mujer
        womenHeaderView?.setOnClickListener {
            if (womenItemsView?.visibility == View.VISIBLE) {
                womenItemsView.visibility = View.GONE
                womenIconView?.rotation = 0f
            } else {
                womenItemsView?.visibility = View.VISIBLE
                womenIconView?.rotation = 180f
            }
        }
        
        // Click listeners para items del menú (por ahora solo mostrarán toast)
        setupMenuItemClickListeners()
    }
    
    private fun setupMenuItemClickListeners() {
        // Opción TODO - muestra todos los productos
        view?.findViewById<View>(R.id.menu_all_products)?.setOnClickListener {
            clearFilter()
        }
        
        // Items de Hombre
        view?.findViewById<View>(R.id.menu_men_shirts)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Camisas"), "Filtrando: Hombre - Camisas")
        }
        view?.findViewById<View>(R.id.menu_men_jackets)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Chaqueta"), "Filtrando: Hombre - Chaquetas")
        }
        view?.findViewById<View>(R.id.menu_men_pants)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Pantalones"), "Filtrando: Hombre - Pantalones")
        }
        view?.findViewById<View>(R.id.menu_men_shoes)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Zapatos"), "Filtrando: Hombre - Zapatos")
        }
        view?.findViewById<View>(R.id.menu_men_accessories)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Accesorios"), "Filtrando: Hombre - Accesorios")
        }
        view?.findViewById<View>(R.id.menu_men_jerseys)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Jersey"), "Filtrando: Hombre - Jerseys")
        }
        view?.findViewById<View>(R.id.menu_men_sacos)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Saco"), "Filtrando: Hombre - Sacos")
        }
        
        // Items de Mujer
        view?.findViewById<View>(R.id.menu_women_dresses)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Vestido"), "Filtrando: Mujer - Vestidos")
        }
        view?.findViewById<View>(R.id.menu_women_pants)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Pantalones"), "Filtrando: Mujer - Pantalones")
        }
        view?.findViewById<View>(R.id.menu_women_shoes)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Zapatos"), "Filtrando: Mujer - Zapatos")
        }
        view?.findViewById<View>(R.id.menu_women_accessories)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Accesorios"), "Filtrando: Mujer - Accesorios")
        }
        view?.findViewById<View>(R.id.menu_women_bags)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Bolsos"), "Filtrando: Mujer - Bolsos")
        }
        view?.findViewById<View>(R.id.menu_women_suits)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Conjunto"), "Filtrando: Mujer - Conjuntos")
        }
    }

    private fun setupToolbar() {
        toolbarStore.setNavigationOnClickListener {
            // Abrir el drawer
            drawerLayout.openDrawer(android.view.Gravity.START)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Reconfigurar restricciones cada vez que el fragment se vuelve visible
        configureGuestModeRestrictions()
        // Asegurar que el ítem correcto esté seleccionado al volver a este fragmento
        bottomNavigation.selectedItemId = R.id.navigation_store
        // Recargar el adapter para actualizar el estado de los botones
        recyclerProducts.adapter?.notifyDataSetChanged()
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
