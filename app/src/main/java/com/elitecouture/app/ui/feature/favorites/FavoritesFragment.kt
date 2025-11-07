package com.elitecouture.app.ui.feature.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elitecouture.app.R
import com.elitecouture.app.di.ServiceLocator
import com.elitecouture.app.domain.model.FavoriteWithProduct
import com.elitecouture.app.ui.common.extension.showStyledSnackbar
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Fragment que muestra la lista de productos favoritos del usuario.
 * Incluye funcionalidad de swipe-to-reveal-delete con opción de deshacer.
 */
class FavoritesFragment : Fragment() {

    private lateinit var toolbarFavorites: MaterialToolbar
    private lateinit var recyclerFavorites: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var textFavoritesCount: TextView
    private lateinit var buttonBrowseProducts: Button
    private lateinit var bottomNavigation: BottomNavigationView
    
    private lateinit var favoritesAdapter: FavoritesAdapter
    
    // Use cases
    private val getUserFavoritesUseCase by lazy {
        ServiceLocator.provideGetUserFavoritesUseCase(requireContext())
    }
    
    private val removeFromFavoritesUseCase by lazy {
        ServiceLocator.provideRemoveProductFromFavoritesUseCase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inicializar vistas
        toolbarFavorites = view.findViewById(R.id.toolbar_favorites)
        recyclerFavorites = view.findViewById(R.id.recycler_favorites)
        layoutEmptyState = view.findViewById(R.id.layout_empty_state)
        textFavoritesCount = view.findViewById(R.id.text_favorites_count)
        buttonBrowseProducts = view.findViewById(R.id.button_browse_products)
        bottomNavigation = view.findViewById(R.id.bottom_navigation)
        
        val scrollView = view.findViewById<androidx.core.widget.NestedScrollView>(R.id.scroll_favorites)
        val contentContainer = view.findViewById<android.widget.LinearLayout>(R.id.content_container)
        
        // Detectar toques fuera para cerrar swipes abiertos
        setupOutsideTouchDetection(view, scrollView, contentContainer)
        
        // Configurar toolbar con botón de retroceso
        setupToolbar()
        
        // Configurar RecyclerView
        setupRecyclerView()
        
        // Configurar bottom navigation (mantener estado de Perfil)
        setupBottomNavigation()
        
        // Configurar botón de estado vacío
        buttonBrowseProducts.setOnClickListener {
            navigateToStore()
        }
        
        // Cargar favoritos
        loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        // Recargar favoritos por si hubo cambios
        loadFavorites()
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoritesAdapter(
            items = mutableListOf(),
            onDeleteClick = { _, position ->
                // Eliminar con posibilidad de undo
                showUndoSnackbar(position)
            }
        )
        
        recyclerFavorites.apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            
            // Interceptar toques en áreas vacías del RecyclerView
            addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: android.view.MotionEvent): Boolean {
                    if (e.action == android.view.MotionEvent.ACTION_DOWN) {
                        // Verificar si el toque está sobre un item
                        val child = rv.findChildViewUnder(e.x, e.y)
                        if (child == null) {
                            // Toque en área vacía del RecyclerView
                            favoritesAdapter.closeCurrentlyRevealed()
                        }
                    }
                    return false // No consumir el evento
                }
            })
        }
    }
    
    private fun setupOutsideTouchDetection(
        rootView: View, 
        scrollView: androidx.core.widget.NestedScrollView,
        contentContainer: android.widget.LinearLayout
    ) {
        // Listener común para cerrar swipes
        val closeSwipeListener = View.OnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                favoritesAdapter.closeCurrentlyRevealed()
            }
            false // No consumir el evento
        }
        
        // Agregar al scroll view (captura toques en área vacía debajo de los items)
        scrollView.setOnTouchListener(closeSwipeListener)
        
        // Agregar al contenedor de contenido (captura toques en áreas de padding)
        contentContainer.setOnTouchListener(closeSwipeListener)
        
        // Agregar al toolbar para cerrar al tocar ahí
        toolbarFavorites.setOnTouchListener(closeSwipeListener)
        
        // Agregar al contador de favoritos
        textFavoritesCount.setOnTouchListener(closeSwipeListener)
        
        // Agregar a la vista raíz como fallback
        rootView.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                // Verificar si el toque está fuera del RecyclerView
                val recyclerViewLocation = IntArray(2)
                recyclerFavorites.getLocationOnScreen(recyclerViewLocation)
                
                val isOutsideRecyclerView = event.rawY < recyclerViewLocation[1] ||
                    event.rawY > recyclerViewLocation[1] + recyclerFavorites.height ||
                    event.rawX < recyclerViewLocation[0] ||
                    event.rawX > recyclerViewLocation[0] + recyclerFavorites.width
                
                if (isOutsideRecyclerView) {
                    favoritesAdapter.closeCurrentlyRevealed()
                }
            }
            false
        }
    }

    private fun setupToolbar() {
        toolbarFavorites.setNavigationOnClickListener {
            // Navegar de vuelta a Perfil
            findNavController().navigateUp()
        }
    }

    private fun setupBottomNavigation() {
        // Mantener Perfil seleccionado (ya que venimos de ahí)
        bottomNavigation.selectedItemId = R.id.navigation_profile
        
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_store -> {
                    navigateToStore()
                    true
                }
                R.id.navigation_cart -> {
                    findNavController().navigate(R.id.action_favoritesFragment_to_cartFragment)
                    true
                }
                R.id.navigation_profile -> {
                    // Volver a Perfil usando navigateUp
                    findNavController().navigateUp()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            try {
                val favorites = getUserFavoritesUseCase()
                android.util.Log.d(TAG, "Loaded ${favorites.size} favorites")
                
                if (favorites.isEmpty()) {
                    showEmptyState()
                } else {
                    showFavoritesList(favorites)
                    updateFavoritesCount(favorites.size)
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading favorites", e)
                requireView().showStyledSnackbar("Error al cargar favoritos")
            }
        }
    }

    private fun showUndoSnackbar(position: Int) {
        // Eliminar visualmente del adapter
        val removedFavorite = favoritesAdapter.removeFavorite(position)
        updateFavoritesCount(favoritesAdapter.itemCount)
        
        // Eliminar de la base de datos inmediatamente
        removeFromFavoritesUseCase(removedFavorite.product.uuid)
        
        // Si la lista queda vacía, mostrar empty state
        if (favoritesAdapter.itemCount == 0) {
            showEmptyState()
        }
        
        requireView().showStyledSnackbar(
            message = getString(R.string.favorites_item_removed),
            duration = Snackbar.LENGTH_SHORT, // Más corto: ~2 segundos
            actionText = getString(R.string.favorites_item_removed_undo),
            actionCallback = {
                // Restaurar favorito en el adapter
                favoritesAdapter.restoreFavorite(position, removedFavorite)
                updateFavoritesCount(favoritesAdapter.itemCount)
                showFavoritesList(emptyList()) // Forzar mostrar lista
                
                // Restaurar en la base de datos
                lifecycleScope.launch {
                    val addToFavoritesUseCase = ServiceLocator.provideAddProductToFavoritesUseCase(requireContext())
                    addToFavoritesUseCase(removedFavorite.product.uuid)
                }
            }
        )
    }

    private fun showEmptyState() {
        layoutEmptyState.visibility = View.VISIBLE
        recyclerFavorites.visibility = View.GONE
        textFavoritesCount.text = getString(R.string.favorites_count_single, 0)
    }

    private fun showFavoritesList(favorites: List<FavoriteWithProduct>) {
        if (favorites.isNotEmpty()) {
            favoritesAdapter.updateFavorites(favorites)
        }
        layoutEmptyState.visibility = View.GONE
        recyclerFavorites.visibility = View.VISIBLE
    }

    private fun updateFavoritesCount(count: Int) {
        textFavoritesCount.text = if (count == 1) {
            getString(R.string.favorites_count_single, count)
        } else {
            getString(R.string.favorites_count_plural, count)
        }
    }

    private fun navigateToStore() {
        // Pop back stack hasta StoreFragment
        findNavController().popBackStack(R.id.storeFragment, false)
    }

    companion object {
        private const val TAG = "FavoritesFragment"
    }
}
