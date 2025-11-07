package com.elitecouture.app.ui.feature.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.elitecouture.app.R
import com.elitecouture.app.domain.model.CartItemWithProduct
import java.text.NumberFormat
import java.util.Locale

/**
 * Adapter para la lista de items en el carrito de compras.
 * Maneja controles de cantidad, límites de stock y notifica cambios al fragment.
 */
class CartAdapter(
    private var items: MutableList<CartItemWithProduct>,
    private val onQuantityChanged: (CartItemWithProduct, Int) -> Unit,
    private val onItemRemoved: (CartItemWithProduct) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {

    companion object {
        private const val TAG = "CartAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartItemViewHolder(view, onQuantityChanged, onItemRemoved)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItemWithProduct = items[position]
        holder.bind(cartItemWithProduct)
    }

    override fun getItemCount(): Int = items.size

    /**
     * Actualiza la lista completa de items del carrito.
     */
    fun updateCartItems(newItems: List<CartItemWithProduct>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    /**
     * Actualiza la cantidad de un item específico en la lista.
     */
    fun updateItemQuantity(productUuid: String, newQuantity: Int) {
        val position = items.indexOfFirst { it.product.uuid == productUuid }
        if (position != -1) {
            // Crear una copia actualizada del item
            val updatedItem = items[position].copy(
                cartItem = items[position].cartItem.copy(quantity = newQuantity)
            )
            items[position] = updatedItem
            notifyItemChanged(position)
        }
    }

    /**
     * Elimina un item de la lista.
     */
    fun removeItem(productUuid: String) {
        val position = items.indexOfFirst { it.product.uuid == productUuid }
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class CartItemViewHolder(
        view: View,
        private val onQuantityChanged: (CartItemWithProduct, Int) -> Unit,
        private val onItemRemoved: (CartItemWithProduct) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val imageProduct = view.findViewById<ImageView>(R.id.image_product)
        private val textProductName = view.findViewById<TextView>(R.id.text_product_name)
        private val textProductDescription = view.findViewById<TextView>(R.id.text_product_description)
        private val textProductPrice = view.findViewById<TextView>(R.id.text_product_price)
        private val textQuantity = view.findViewById<TextView>(R.id.text_quantity)
        private val textStock = view.findViewById<TextView>(R.id.text_stock)
        private val buttonDecrease = view.findViewById<ImageButton>(R.id.button_decrease)
        private val buttonIncrease = view.findViewById<ImageButton>(R.id.button_increase)

        private var currentCartItem: CartItemWithProduct? = null

        fun bind(cartItemWithProduct: CartItemWithProduct) {
            currentCartItem = cartItemWithProduct
            val product = cartItemWithProduct.product
            val cartItem = cartItemWithProduct.cartItem

            // Configurar información del producto
            textProductName.text = product.name
            textProductDescription.text = product.description ?: ""

            // Formatear precio
            val priceFormat = NumberFormat.getCurrencyInstance(Locale.US)
            textProductPrice.text = priceFormat.format(product.price)

            // Mostrar cantidad actual
            textQuantity.text = cartItem.quantity.toString()

            // Mostrar stock disponible
            textStock.text = itemView.context.getString(R.string.cart_stock_label, product.stock)

            // Cargar imagen del producto
            val firstImage = product.images.firstOrNull()
            if (firstImage != null) {
                imageProduct.load(firstImage) {
                    crossfade(true)
                    placeholder(R.color.background_light)
                    error(R.drawable.product_placeholder)
                }
            } else {
                imageProduct.setImageResource(R.drawable.product_placeholder)
            }

            // Configurar botones de cantidad
            setupQuantityControls(cartItem.quantity, product.stock)
        }

        private fun setupQuantityControls(currentQuantity: Int, maxStock: Int) {
            // Botón decrementar
            buttonDecrease.setOnClickListener {
                val newQuantity = currentQuantity - 1
                
                if (newQuantity <= 0) {
                    // Si la cantidad llega a 0, eliminar del carrito
                    currentCartItem?.let { onItemRemoved(it) }
                } else {
                    // Actualizar cantidad
                    currentCartItem?.let { onQuantityChanged(it, newQuantity) }
                    textQuantity.text = newQuantity.toString()
                    updateButtonStates(newQuantity, maxStock)
                }
            }

            // Botón incrementar
            buttonIncrease.setOnClickListener {
                val newQuantity = currentQuantity + 1
                
                if (newQuantity <= maxStock) {
                    // Actualizar cantidad si no excede el stock
                    currentCartItem?.let { onQuantityChanged(it, newQuantity) }
                    textQuantity.text = newQuantity.toString()
                    updateButtonStates(newQuantity, maxStock)
                }
            }

            // Estado inicial de botones
            updateButtonStates(currentQuantity, maxStock)
        }

        private fun updateButtonStates(currentQuantity: Int, maxStock: Int) {
            // Deshabilitar botón de incremento si se alcanzó el máximo stock
            buttonIncrease.isEnabled = currentQuantity < maxStock
            buttonIncrease.alpha = if (currentQuantity < maxStock) 1.0f else 0.3f

            // El botón de decremento siempre está habilitado (permite eliminar)
            buttonDecrease.isEnabled = true
            buttonDecrease.alpha = 1.0f
        }
    }
}
