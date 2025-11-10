package com.elitecouture.app.ui.feature.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elitecouture.app.R
import com.elitecouture.app.domain.model.Product
import com.elitecouture.app.domain.usecase.cart.AddToCartUseCase
import com.elitecouture.app.domain.usecase.cart.IsProductInCartUseCase
import com.elitecouture.app.domain.usecase.favorites.AddProductToFavoritesUseCase
import com.elitecouture.app.domain.usecase.favorites.IsProductFavoriteUseCase
import com.elitecouture.app.domain.usecase.favorites.RemoveProductFromFavoritesUseCase
import com.elitecouture.app.ui.common.extension.showStyledSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ProductListAdapter(
    private val items: List<Product>,
    private val addToFavoritesUseCase: AddProductToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveProductFromFavoritesUseCase,
    private val isProductFavoriteUseCase: IsProductFavoriteUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val isProductInCartUseCase: IsProductInCartUseCase,
    private val isGuestMode: Boolean = false,
    private val onNavigateToFavorites: () -> Unit = {},
    private val onNavigateToCart: () -> Unit = {},
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    companion object {
        private const val TAG = "ProductListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = items[position]
        holder.bind(
            product = product,
            addToFavoritesUseCase = addToFavoritesUseCase,
            removeFromFavoritesUseCase = removeFromFavoritesUseCase,
            isProductFavoriteUseCase = isProductFavoriteUseCase,
            addToCartUseCase = addToCartUseCase,
            isProductInCartUseCase = isProductInCartUseCase,
            isGuestMode = isGuestMode,
            onNavigateToFavorites = onNavigateToFavorites,
            onNavigateToCart = onNavigateToCart,
            coroutineScope = coroutineScope
        )
    }

    override fun getItemCount(): Int = items.size

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewPager = view.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewpager_images)
        private val recyclerThumbnails = view.findViewById<RecyclerView>(R.id.recycler_thumbnails)
        private val chipGroupTags = view.findViewById<android.widget.LinearLayout>(R.id.chip_group_tags)
        private val name = view.findViewById<TextView>(R.id.text_name)
        private val description = view.findViewById<TextView>(R.id.text_description)
        private val price = view.findViewById<TextView>(R.id.text_price)
        private val btnFav = view.findViewById<android.widget.ImageButton>(R.id.button_favorite)
        private val btnAdd = view.findViewById<Button>(R.id.button_add)

        fun bind(
            product: Product,
            addToFavoritesUseCase: AddProductToFavoritesUseCase,
            removeFromFavoritesUseCase: RemoveProductFromFavoritesUseCase,
            isProductFavoriteUseCase: IsProductFavoriteUseCase,
            addToCartUseCase: AddToCartUseCase,
            isProductInCartUseCase: IsProductInCartUseCase,
            isGuestMode: Boolean,
            onNavigateToFavorites: () -> Unit,
            onNavigateToCart: () -> Unit,
            coroutineScope: CoroutineScope
        ) {
            name.text = product.name
            description.text = product.description ?: ""
            // Formatear el precio con separador de miles
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            price.text = formatter.format(product.price)

            // Mostrar tags como badges cuadrados verticales con opacidad
            chipGroupTags.removeAllViews()
            product.tags.take(3).forEach { tag ->
                val tagView = TextView(itemView.context).apply {
                    text = tag
                    textSize = 10f
                    setTextColor(itemView.context.getColor(android.R.color.white))
                    gravity = android.view.Gravity.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    
                    // Fondo con color primario y opacidad (80%)
                    val colorPrimary = itemView.context.getColor(R.color.color_primary)
                    val colorWithAlpha = android.graphics.Color.argb(
                        204, // 80% opacity (255 * 0.8 = 204)
                        android.graphics.Color.red(colorPrimary),
                        android.graphics.Color.green(colorPrimary),
                        android.graphics.Color.blue(colorPrimary)
                    )
                    background = android.graphics.drawable.GradientDrawable().apply {
                        setColor(colorWithAlpha)
                        cornerRadius = 8f
                    }
                    
                    // Padding para hacer el tag más cuadrado
                    val paddingPx = android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        itemView.context.resources.displayMetrics
                    ).toInt()
                    setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                    
                    // Layout params para mantener forma cuadrada
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = android.util.TypedValue.applyDimension(
                            android.util.TypedValue.COMPLEX_UNIT_DIP,
                            4f,
                            itemView.context.resources.displayMetrics
                        ).toInt()
                    }
                    
                    minWidth = android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP,
                        60f,
                        itemView.context.resources.displayMetrics
                    ).toInt()
                    minHeight = android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP,
                        32f,
                        itemView.context.resources.displayMetrics
                    ).toInt()
                }
                chipGroupTags.addView(tagView)
            }

            // Ocultar botones si es modo invitado
            if (isGuestMode) {
                btnFav.visibility = View.GONE
                btnAdd.visibility = View.GONE
            } else {
                btnFav.visibility = View.VISIBLE
                btnAdd.visibility = View.VISIBLE
            }

            // Usar todas las imágenes del producto (3 imágenes por producto)
            val images = product.images.ifEmpty {
                // Fallback: lista vacía si no hay imágenes
                emptyList()
            }

            // Configurar ViewPager2 principal
            viewPager.adapter = ProductCarouselAdapter(images)

            // Configurar RecyclerView de miniaturas
            val thumbnailAdapter = ThumbnailAdapter(images) { position ->
                // Al hacer clic en una miniatura, ir a esa página en el ViewPager2
                viewPager.setCurrentItem(position, true)
            }
            recyclerThumbnails.adapter = thumbnailAdapter
            recyclerThumbnails.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                itemView.context,
                androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                false
            )

            // Sincronizar miniaturas cuando cambia la página del ViewPager2
            viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    thumbnailAdapter.updateSelectedPosition(position)
                }
            })

            // Optional: page transformer for zoom effect
            viewPager.setPageTransformer { page, position ->
                val scale = 0.85f + (1 - kotlin.math.abs(position)) * 0.15f
                page.scaleY = scale
            }

            // ============================================
            // FAVORITES Y CART - Solo si NO es modo invitado
            // ============================================
            if (!isGuestMode) {
                coroutineScope.launch {
                    // Verificar estado inicial de favoritos desde DB
                    val isFavorite = isProductFavoriteUseCase(product.uuid)
                    android.util.Log.d(TAG, "bind() -> Product: ${product.name} (${product.uuid}), isFavorite=$isFavorite")
                    updateFavoriteIcon(isFavorite)
                    
                    // Verificar y actualizar el estado inicial del botón de carrito
                    val isInCart = isProductInCartUseCase(product.uuid)
                    updateAddToCartButton(isInCart)
                }

                btnFav.setOnClickListener {
                    coroutineScope.launch {
                        android.util.Log.d(TAG, "btnFav clicked for product: ${product.name} (${product.uuid})")
                        
                        // Verificar estado actual desde la base de datos
                        val currentlyFavorite = isProductFavoriteUseCase(product.uuid)
                        android.util.Log.d(TAG, "Current favorite status: $currentlyFavorite")
                        
                        if (currentlyFavorite) {
                            // Eliminar de favoritos
                            android.util.Log.d(TAG, "Attempting to REMOVE from favorites...")
                            val success = removeFromFavoritesUseCase(product.uuid)
                            android.util.Log.d(TAG, "Remove result: $success")
                            if (success) {
                                updateFavoriteIcon(false)
                                itemView.showStyledSnackbar("Eliminado de favoritos")
                            } else {
                                // Error: no se pudo eliminar
                                itemView.showStyledSnackbar("Error al eliminar de favoritos")
                            }
                        } else {
                            // Añadir a favoritos
                            android.util.Log.d(TAG, "Attempting to ADD to favorites...")
                            val success = addToFavoritesUseCase(product.uuid)
                            android.util.Log.d(TAG, "Add result: $success")
                            if (success) {
                                updateFavoriteIcon(true)
                                itemView.showStyledSnackbar(
                                    message = itemView.context.getString(R.string.favorites_item_added),
                                    duration = com.google.android.material.snackbar.Snackbar.LENGTH_SHORT,
                                    actionText = itemView.context.getString(R.string.favorites_item_added_action),
                                    actionCallback = {
                                        onNavigateToFavorites()
                                    }
                                )
                            } else {
                                // Error: no se pudo añadir (puede que ya exista o usuario no esté logueado)
                                itemView.showStyledSnackbar("Error al añadir a favoritos")
                            }
                        }
                    }
                }

            btnAdd.setOnClickListener {
                coroutineScope.launch {
                    android.util.Log.d(TAG, "btnAdd clicked for product: ${product.name} (${product.uuid})")
                    
                    // Validar que haya stock disponible
                    if (product.stock <= 0) {
                        itemView.showStyledSnackbar("Producto sin stock disponible")
                        return@launch
                    }
                    
                    // Añadir al carrito
                    android.util.Log.d(TAG, "Attempting to ADD to cart...")
                    val result = addToCartUseCase(product.uuid, quantity = 1)
                    android.util.Log.d(TAG, "Add to cart result: $result")
                    
                    if (result > 0) {
                        // Éxito - cambiar estado del botón
                        updateAddToCartButton(true)
                        
                        itemView.showStyledSnackbar(
                            message = itemView.context.getString(R.string.cart_item_added, product.name),
                            duration = com.google.android.material.snackbar.Snackbar.LENGTH_SHORT,
                            actionText = "Ver carrito",
                            actionCallback = {
                                onNavigateToCart()
                            }
                        )
                    } else {
                        // Error: usuario no logueado o error en DB
                        itemView.showStyledSnackbar("Error al añadir al carrito")
                    }
                }
            }
            }
        }

        /**
         * Actualiza el icono del botón de favoritos según el estado.
         * @param isFavorite true si está en favoritos, false si no
         */
        private fun updateFavoriteIcon(isFavorite: Boolean) {
            if (isFavorite) {
                btnFav.setImageResource(R.drawable.star)
            } else {
                btnFav.setImageResource(R.drawable.star_plus_outline)
            }
        }

        /**
         * Actualiza el estado visual del botón "Añadir al carrito".
         * @param isAdded true si el producto fue añadido, false para estado normal
         */
        private fun updateAddToCartButton(isAdded: Boolean) {
            if (isAdded) {
                // Estado "añadido": magenta claro (disabled)
                btnAdd.setBackgroundColor(itemView.context.getColor(R.color.button_cart_added))
                btnAdd.text = itemView.context.getString(R.string.cart_button_added)
                btnAdd.isEnabled = false
            } else {
                // Estado normal: magenta oscuro
                btnAdd.setBackgroundColor(itemView.context.getColor(R.color.button_cart))
                btnAdd.text = itemView.context.getString(R.string.cart_button_add)
                btnAdd.isEnabled = true
            }
        }
    }
}
