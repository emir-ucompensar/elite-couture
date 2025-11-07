package com.elitecouture.app.ui.feature.cart

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
import com.elitecouture.app.domain.model.CartItemWithProduct
import com.elitecouture.app.ui.common.extension.showStyledSnackbar
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * Fragment que muestra el carrito de compras del usuario.
 * Incluye controles de cantidad, subtotal dinámico y botón de finalizar compra.
 */
class CartFragment : Fragment() {

    private lateinit var toolbarCart: MaterialToolbar
    private lateinit var recyclerCart: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var layoutSummary: LinearLayout
    private lateinit var textCartCount: TextView
    private lateinit var textSubtotal: TextView
    private lateinit var buttonBrowseProducts: Button
    private lateinit var buttonCheckout: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView
    
    private lateinit var cartAdapter: CartAdapter
    
    // Use cases
    private val getUserCartUseCase by lazy {
        ServiceLocator.provideGetUserCartUseCase(requireContext())
    }
    
    private val updateCartItemQuantityUseCase by lazy {
        ServiceLocator.provideUpdateCartItemQuantityUseCase(requireContext())
    }
    
    private val removeFromCartUseCase by lazy {
        ServiceLocator.provideRemoveFromCartUseCase(requireContext())
    }
    
    private val addToCartUseCase by lazy {
        ServiceLocator.provideAddToCartUseCase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inicializar vistas
        toolbarCart = view.findViewById(R.id.toolbar_cart)
        recyclerCart = view.findViewById(R.id.recycler_cart)
        layoutEmptyState = view.findViewById(R.id.layout_empty_state)
        layoutSummary = view.findViewById(R.id.layout_summary)
        textCartCount = view.findViewById(R.id.text_cart_count)
        textSubtotal = view.findViewById(R.id.text_subtotal)
        buttonBrowseProducts = view.findViewById(R.id.button_browse_products)
        buttonCheckout = view.findViewById(R.id.button_checkout)
        bottomNavigation = view.findViewById(R.id.bottom_navigation)
        
        // Configurar toolbar con botón de retroceso
        setupToolbar()
        
        // Configurar RecyclerView
        setupRecyclerView()
        
        // Configurar bottom navigation
        setupBottomNavigation()
        
        // Configurar botón de estado vacío
        buttonBrowseProducts.setOnClickListener {
            navigateToStore()
        }
        
        // Configurar botón de finalizar compra
        buttonCheckout.setOnClickListener {
            handleCheckout()
        }
        
        // Cargar items del carrito
        loadCartItems()
    }

    override fun onResume() {
        super.onResume()
        // Recargar carrito por si hubo cambios
        loadCartItems()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            items = mutableListOf(),
            onQuantityChanged = { cartItem, newQuantity ->
                updateItemQuantity(cartItem, newQuantity)
            },
            onItemRemoved = { cartItem ->
                removeItemFromCart(cartItem)
            }
        )
        
        recyclerCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupToolbar() {
        toolbarCart.setNavigationOnClickListener {
            // Navegar de vuelta
            findNavController().navigateUp()
        }
    }

    private fun setupBottomNavigation() {
        // Mantener Carrito seleccionado
        bottomNavigation.selectedItemId = R.id.navigation_cart
        
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_store -> {
                    navigateToStore()
                    true
                }
                R.id.navigation_cart -> {
                    // Ya estamos en el carrito
                    true
                }
                R.id.navigation_profile -> {
                    navigateToProfile()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadCartItems() {
        lifecycleScope.launch {
            val cartItems = getUserCartUseCase()
            updateUI(cartItems)
        }
    }

    private fun updateUI(cartItems: List<CartItemWithProduct>) {
        if (cartItems.isEmpty()) {
            // Mostrar estado vacío
            recyclerCart.visibility = View.GONE
            layoutSummary.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
            textCartCount.visibility = View.GONE
        } else {
            // Mostrar lista de items
            recyclerCart.visibility = View.VISIBLE
            layoutSummary.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
            textCartCount.visibility = View.VISIBLE
            
            // Actualizar contador
            val totalItems = cartItems.sumOf { it.cartItem.quantity }
            val itemText = if (totalItems == 1) {
                getString(R.string.cart_count_singular)
            } else {
                getString(R.string.cart_count_plural, totalItems)
            }
            textCartCount.text = itemText
            
            // Actualizar adapter
            cartAdapter.updateCartItems(cartItems)
            
            // Calcular y mostrar subtotal
            updateSubtotal(cartItems)
        }
    }

    private fun updateSubtotal(cartItems: List<CartItemWithProduct>) {
        val subtotal = cartItems.sumOf { item ->
            item.product.price * item.cartItem.quantity
        }
        
        val priceFormat = NumberFormat.getCurrencyInstance(Locale.US)
        textSubtotal.text = priceFormat.format(subtotal)
    }

    private fun updateItemQuantity(cartItem: CartItemWithProduct, newQuantity: Int) {
        lifecycleScope.launch {
            // Actualizar en base de datos
            val rowsUpdated = updateCartItemQuantityUseCase(cartItem.product.uuid, newQuantity)
            
            if (rowsUpdated > 0) {
                // Actualizar UI local
                cartAdapter.updateItemQuantity(cartItem.product.uuid, newQuantity)
                
                // Recargar para actualizar subtotal
                loadCartItems()
            }
        }
    }

    private fun removeItemFromCart(cartItem: CartItemWithProduct) {
        lifecycleScope.launch {
            // Guardar la cantidad original antes de eliminar
            val originalQuantity = cartItem.cartItem.quantity
            
            // Eliminar de base de datos
            val rowsDeleted = removeFromCartUseCase(cartItem.product.uuid)
            
            if (rowsDeleted > 0) {
                // Actualizar UI local
                cartAdapter.removeItem(cartItem.product.uuid)
                
                // Mostrar mensaje con botón "Deshacer"
                requireView().showStyledSnackbar(
                    message = getString(R.string.cart_item_removed, cartItem.product.name),
                    duration = com.google.android.material.snackbar.Snackbar.LENGTH_LONG,
                    actionText = "Deshacer",
                    actionCallback = {
                        // Revertir la eliminación
                        lifecycleScope.launch {
                            // Volver a añadir el producto con la cantidad original
                            val result = addToCartUseCase(cartItem.product.uuid, originalQuantity)
                            if (result > 0) {
                                // Recargar la lista del carrito
                                loadCartItems()
                                requireView().showStyledSnackbar("Producto restaurado")
                            }
                        }
                    }
                )
                
                // Recargar para actualizar subtotal y estado vacío
                loadCartItems()
            }
        }
    }

    private fun handleCheckout() {
        // Mostrar mensaje de funcionalidad no disponible
        requireView().showStyledSnackbar(getString(R.string.toast_checkout_under_construction))
    }

    private fun navigateToStore() {
        findNavController().navigate(R.id.action_cartFragment_to_storeFragment)
    }

    private fun navigateToProfile() {
        findNavController().navigate(R.id.action_cartFragment_to_profileFragment)
    }
}
