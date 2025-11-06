package com.elitecouture.app.ui.feature.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elitecouture.app.R
import com.elitecouture.app.domain.model.Product
import com.elitecouture.app.domain.usecase.favorites.AddProductToFavoritesUseCase
import com.elitecouture.app.domain.usecase.favorites.IsProductFavoriteUseCase
import com.elitecouture.app.domain.usecase.favorites.RemoveProductFromFavoritesUseCase
import com.elitecouture.app.ui.common.extension.showStyledSnackbar

class ProductListAdapter(
    private val items: List<Product>,
    private val addToFavoritesUseCase: AddProductToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveProductFromFavoritesUseCase,
    private val isProductFavoriteUseCase: IsProductFavoriteUseCase,
    private val onNavigateToFavorites: () -> Unit = {}
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
            onNavigateToFavorites = onNavigateToFavorites
        )
    }

    override fun getItemCount(): Int = items.size

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewPager = view.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewpager_images)
        private val recyclerThumbnails = view.findViewById<RecyclerView>(R.id.recycler_thumbnails)
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
            onNavigateToFavorites: () -> Unit
        ) {
            name.text = product.name
            description.text = product.description ?: ""
            price.text = String.format("$%.2f", product.price)

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
            // FAVORITES - Verificar estado inicial desde DB
            // ============================================
            val isFavorite = isProductFavoriteUseCase(product.uuid)
            android.util.Log.d(TAG, "bind() -> Product: ${product.name} (${product.uuid}), isFavorite=$isFavorite")
            updateFavoriteIcon(isFavorite)

            btnFav.setOnClickListener {
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

            btnAdd.setOnClickListener {
                // Add to cart placeholder
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
    }
}
