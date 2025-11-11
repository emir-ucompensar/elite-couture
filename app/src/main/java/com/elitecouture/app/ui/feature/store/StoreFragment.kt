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
import com.elitecouture.app.ui.common.extension.showStyledSnackbar
import kotlinx.coroutines.launch
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import java.text.Normalizer
import java.util.Locale

class StoreFragment : Fragment() {
    private val sessionManager by lazy { ServiceLocator.provideSessionManager(requireContext()) }
    private lateinit var toolbarStore: MaterialToolbar
    private lateinit var bottomNavigation: BottomNavigationView
    private val storeViewModel: StoreViewModel by activityViewModels()
    private lateinit var recyclerProducts: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerCloseButton: MaterialButton
    private lateinit var textActiveFilter: android.widget.TextView
    private lateinit var emptyCategoryState: android.widget.LinearLayout
    private lateinit var textEmptyCategoryTitle: android.widget.TextView
    private lateinit var textEmptyCategoryMessage: android.widget.TextView
    private lateinit var btnExploreProducts: MaterialButton
    
    // Variables para los menús expandibles
    private var menItemsView: View? = null
    private var menIconView: android.widget.ImageView? = null
    private var womenItemsView: View? = null
    private var womenIconView: android.widget.ImageView? = null
    
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
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        textActiveFilter = view.findViewById(R.id.text_active_filter)
        emptyCategoryState = view.findViewById(R.id.empty_category_state)
        textEmptyCategoryTitle = view.findViewById(R.id.text_empty_category_title)
        textEmptyCategoryMessage = view.findViewById(R.id.text_empty_category_message)
        btnExploreProducts = view.findViewById(R.id.btn_explore_products)
        
        // Configurar SwipeRefreshLayout
        setupSwipeRefresh()
        
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
        // Load products - ya no necesitamos seed porque usamos Supabase
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {

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

    // Chequea que un producto coincida con el filtro especificado
    // Los filtros se componen de: [género, categoría] o solo [género] o solo [categoría]
    // Ejemplo: ["Mujer", "Vestidos"] - debe tener género=Mujer Y type=Vestidos
    private fun productMatchesAllTags(product: Product, tags: List<String>): Boolean {
        if (tags.isEmpty()) return true
        
        return tags.all { desiredTag ->
            val nTag = normalizeText(desiredTag)
            
            // Buscar en gender (comparación exacta normalizada)
            val genderMatch = product.gender?.let { normalizeText(it) == nTag } ?: false
            
            // Buscar en type (comparación exacta normalizada o substring)
            val typeMatch = product.type?.let { 
                val nType = normalizeText(it)
                nType == nTag || nType.contains(nTag) || nTag.contains(nType)
            } ?: false
            
            // Buscar en tags (substring match)
            val tagsMatch = product.tags.any { tag ->
                val nProductTag = normalizeText(tag)
                nProductTag.contains(nTag) || nTag.contains(nProductTag)
            }
            
            // El tag deseado debe coincidir con al menos uno de los campos
            genderMatch || typeMatch || tagsMatch
        }
    }

    // Limpia el filtro y muestra todos los productos
    private fun clearFilter() {
        lastFilterTags = null
        textActiveFilter.visibility = View.GONE
        emptyCategoryState.visibility = View.GONE
        recyclerProducts.visibility = View.VISIBLE
        updateRecyclerAdapter(fullProductList)
        requireView().showStyledSnackbar(getString(R.string.filter_showing_all))
        
        // Cerrar menús expandidos
        menItemsView?.visibility = View.GONE
        menIconView?.rotation = 0f
        womenItemsView?.visibility = View.GONE
        womenIconView?.rotation = 0f
        
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
        textActiveFilter.text = toastMessage.replace(getString(R.string.filter_prefix), "")
        textActiveFilter.visibility = View.VISIBLE
        
        // Verificar si hay productos filtrados
        if (filtered.isEmpty()) {
            // Mostrar estado vacío
            recyclerProducts.visibility = View.GONE
            emptyCategoryState.visibility = View.VISIBLE
            
            // Personalizar mensaje según la categoría
            val categoryName = toastMessage.replace(getString(R.string.filter_prefix), "")
            textEmptyCategoryMessage.text = getString(R.string.empty_category_message, categoryName)
        } else {
            // Mostrar productos
            emptyCategoryState.visibility = View.GONE
            recyclerProducts.visibility = View.VISIBLE
            updateRecyclerAdapter(filtered)
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
        menItemsView = view?.findViewById<View>(R.id.menu_men_items)
        menIconView = view?.findViewById<android.widget.ImageView>(R.id.menu_men_icon)
        
        // Referencias a las vistas del menú Mujer
        val womenHeaderView = view?.findViewById<View>(R.id.menu_women_header)
        womenItemsView = view?.findViewById<View>(R.id.menu_women_items)
        womenIconView = view?.findViewById<android.widget.ImageView>(R.id.menu_women_icon)
        
        // Toggle del menú Hombre
        menHeaderView?.setOnClickListener {
            if (menItemsView?.visibility == View.VISIBLE) {
                // Cerrar menú Hombre
                menItemsView?.visibility = View.GONE
                menIconView?.rotation = 0f
            } else {
                // Abrir menú Hombre y cerrar Mujer
                menItemsView?.visibility = View.VISIBLE
                menIconView?.rotation = 180f
                
                // Cerrar menú Mujer si está abierto
                womenItemsView?.visibility = View.GONE
                womenIconView?.rotation = 0f
            }
        }
        
        // Toggle del menú Mujer
        womenHeaderView?.setOnClickListener {
            if (womenItemsView?.visibility == View.VISIBLE) {
                // Cerrar menú Mujer
                womenItemsView?.visibility = View.GONE
                womenIconView?.rotation = 0f
            } else {
                // Abrir menú Mujer y cerrar Hombre
                womenItemsView?.visibility = View.VISIBLE
                womenIconView?.rotation = 180f
                
                // Cerrar menú Hombre si está abierto
                menItemsView?.visibility = View.GONE
                menIconView?.rotation = 0f
            }
        }
        
        // Click listeners para items del menú
        setupMenuItemClickListeners(menItemsView, menIconView, womenItemsView, womenIconView)
    }
    
    private fun setupMenuItemClickListeners(
        menItemsView: View?,
        menIconView: android.widget.ImageView?,
        womenItemsView: View?,
        womenIconView: android.widget.ImageView?
    ) {
        // Opción TODO - muestra todos los productos y cierra los menús expandidos
        view?.findViewById<View>(R.id.menu_all_products)?.setOnClickListener {
            // Cerrar ambos menús
            menItemsView?.visibility = View.GONE
            menIconView?.rotation = 0f
            womenItemsView?.visibility = View.GONE
            womenIconView?.rotation = 0f
            
            clearFilter()
        }
        
        // Items de Hombre (por ahora no hay productos de hombre en la BD)
        view?.findViewById<View>(R.id.menu_men_shirts)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Camisas"), getString(R.string.filter_men_shirts))
        }
        view?.findViewById<View>(R.id.menu_men_jackets)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Chaquetas"), getString(R.string.filter_men_jackets))
        }
        view?.findViewById<View>(R.id.menu_men_pants)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Pantalones"), getString(R.string.filter_men_pants))
        }
        view?.findViewById<View>(R.id.menu_men_shoes)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Zapatos"), getString(R.string.filter_men_shoes))
        }
        view?.findViewById<View>(R.id.menu_men_accessories)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Accesorios"), getString(R.string.filter_men_accessories))
        }
        view?.findViewById<View>(R.id.menu_men_jerseys)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Suéteres"), getString(R.string.filter_men_jerseys))
        }
        view?.findViewById<View>(R.id.menu_men_sacos)?.setOnClickListener {
            applyFilterToggle(listOf("Hombre", "Chaquetas"), getString(R.string.filter_men_sacos))
        }
        
        // Items de Mujer (categorías según datos reales en Supabase)
        view?.findViewById<View>(R.id.menu_women_dresses)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Vestidos"), getString(R.string.filter_women_dresses))
        }
        view?.findViewById<View>(R.id.menu_women_pants)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Pantalones"), getString(R.string.filter_women_pants))
        }
        view?.findViewById<View>(R.id.menu_women_shoes)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Zapatos"), getString(R.string.filter_women_shoes))
        }
        view?.findViewById<View>(R.id.menu_women_accessories)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Accesorios"), getString(R.string.filter_women_accessories))
        }
        view?.findViewById<View>(R.id.menu_women_bags)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Bolsos"), getString(R.string.filter_women_bags))
        }
        view?.findViewById<View>(R.id.menu_women_suits)?.setOnClickListener {
            applyFilterToggle(listOf("Mujer", "Conjuntos"), getString(R.string.filter_women_suits))
        }
    }

    private fun setupSwipeRefresh() {
        // Configurar colores del SwipeRefreshLayout
        swipeRefresh.setColorSchemeResources(
            R.color.color_primary,
            R.color.color_primary_dark,
            R.color.accent_red
        )
        
        // Configurar el listener para el gesto de pull-to-refresh
        swipeRefresh.setOnRefreshListener {
            refreshProducts()
        }
    }

    private fun setupToolbar() {
        // Configurar el botón de navegación (hamburguesa) para abrir el drawer
        toolbarStore.setNavigationOnClickListener {
            drawerLayout.openDrawer(android.view.Gravity.START)
        }
        
        // Inflar el menú en la toolbar
        toolbarStore.inflateMenu(R.menu.menu_store_toolbar)
        
        // Configurar listener para el botón de refresh
        toolbarStore.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_refresh -> {
                    refreshProducts()
                    true
                }
                else -> false
            }
        }
    }
    
    private fun refreshProducts() {
        // Mostrar indicador de carga
        swipeRefresh.isRefreshing = true
        
        lifecycleScope.launch {
            try {
                // Recargar productos desde Supabase
                val products = ServiceLocator.provideGetProductCatalogUseCase(requireContext())()
                fullProductList = products
                
                // Si hay un filtro activo, reaplicarlo
                if (lastFilterTags != null) {
                    val filtered = fullProductList.filter { productMatchesAllTags(it, lastFilterTags!!) }
                    
                    if (filtered.isEmpty()) {
                        recyclerProducts.visibility = View.GONE
                        emptyCategoryState.visibility = View.VISIBLE
                    } else {
                        emptyCategoryState.visibility = View.GONE
                        recyclerProducts.visibility = View.VISIBLE
                        updateRecyclerAdapter(filtered)
                    }
                } else {
                    // Sin filtro, mostrar todos
                    emptyCategoryState.visibility = View.GONE
                    recyclerProducts.visibility = View.VISIBLE
                    updateRecyclerAdapter(fullProductList)
                }
                
                requireView().showStyledSnackbar(getString(R.string.products_updated_success))
            } catch (e: Exception) {
                requireView().showStyledSnackbar(getString(R.string.products_updated_error))
            } finally {
                // Ocultar indicador de carga
                swipeRefresh.isRefreshing = false
            }
        }
    }
    
    private fun updateRecyclerAdapter(products: List<Product>) {
        recyclerProducts.adapter = ProductListAdapter(
            items = products,
            addToFavoritesUseCase = ServiceLocator.provideAddProductToFavoritesUseCase(requireContext()),
            removeFromFavoritesUseCase = ServiceLocator.provideRemoveProductFromFavoritesUseCase(requireContext()),
            isProductFavoriteUseCase = ServiceLocator.provideIsProductFavoriteUseCase(requireContext()),
            addToCartUseCase = ServiceLocator.provideAddToCartUseCase(requireContext()),
            isProductInCartUseCase = ServiceLocator.provideIsProductInCartUseCase(requireContext()),
            isGuestMode = sessionManager.isGuestMode(),
            onNavigateToFavorites = { findNavController().navigate(R.id.action_storeFragment_to_favoritesFragment) },
            onNavigateToCart = { findNavController().navigate(R.id.action_storeFragment_to_cartFragment) }
        )
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
                // Si hay un filtro activo, volver a "TODO" en lugar de salir
                if (lastFilterTags != null) {
                    clearFilter()
                    return
                }
                
                // Si no hay filtro, mostrar diálogo de salida
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
