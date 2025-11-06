package com.elitecouture.app.ui.feature.favorites

import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.elitecouture.app.R
import com.elitecouture.app.domain.model.FavoriteWithProduct
import kotlin.math.abs

/**
 * Adapter para la lista de favoritos con soporte para swipe-to-delete.
 * Usa detección de gestos manual para un control preciso sin interferencias.
 */
class FavoritesAdapter(
    private var items: MutableList<FavoriteWithProduct>,
    private val onDeleteClick: (FavoriteWithProduct, Int) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    companion object {
        private const val TAG = "FavoritesAdapter"
        private const val SWIPE_THRESHOLD = 110f // Distancia en dp para revelar el botón
    }
    
    // Rastrea qué ViewHolder está actualmente revelado
    private var currentlyRevealedHolder: FavoriteViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_compact, parent, false)
        return FavoriteViewHolder(view, onDeleteClick, ::onHolderRevealed)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteWithProduct = items[position]
        holder.bind(favoriteWithProduct)
    }
    
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads[0] == "close_swipe") {
            holder.closeSwipe()
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateFavorites(newFavorites: List<FavoriteWithProduct>) {
        items.clear()
        items.addAll(newFavorites)
        notifyDataSetChanged()
    }

    fun removeFavorite(position: Int): FavoriteWithProduct {
        val removed = items.removeAt(position)
        notifyItemRemoved(position)
        return removed
    }

    fun restoreFavorite(position: Int, favorite: FavoriteWithProduct) {
        items.add(position, favorite)
        notifyItemInserted(position)
    }

    /**
     * Callback cuando un holder se revela.
     * Cierra automáticamente cualquier otro holder revelado.
     */
    private fun onHolderRevealed(holder: FavoriteViewHolder) {
        if (currentlyRevealedHolder != null && currentlyRevealedHolder != holder) {
            currentlyRevealedHolder?.closeSwipe()
        }
        currentlyRevealedHolder = holder
    }
    
    /**
     * Cierra el holder actualmente revelado (si existe).
     */
    fun closeCurrentlyRevealed() {
        currentlyRevealedHolder?.closeSwipe()
        currentlyRevealedHolder = null
    }

    class FavoriteViewHolder(
        view: View,
        private val onDeleteClick: (FavoriteWithProduct, Int) -> Unit,
        private val onRevealed: (FavoriteViewHolder) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        
        private val imageProduct = view.findViewById<ImageView>(R.id.image_product)
        private val textProductName = view.findViewById<TextView>(R.id.text_product_name)
        private val textProductDescription = view.findViewById<TextView>(R.id.text_product_description)
        private val deleteButton = view.findViewById<android.widget.FrameLayout>(R.id.delete_button)
        private val cardView = view.findViewById<androidx.cardview.widget.CardView>(R.id.card_favorite)
        
        private val density = view.context.resources.displayMetrics.density
        private val swipeThresholdPx = SWIPE_THRESHOLD * density
        
        private var initialX = 0f
        private var initialCardTranslation = 0f
        private var isSwiping = false
        
        private var currentFavorite: FavoriteWithProduct? = null

        fun bind(favoriteWithProduct: FavoriteWithProduct) {
            currentFavorite = favoriteWithProduct
            val product = favoriteWithProduct.product

            textProductName.text = product.name
            textProductDescription.text = product.description ?: ""

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
            
            // Restablecer posición al hacer rebind
            cardView.translationX = 0f
            
            setupSwipeGesture()
            setupDeleteButton()
        }
        
        private fun setupSwipeGesture() {
            // Detector de gestos para diferenciar tap de swipe
            val gestureDetector = GestureDetectorCompat(itemView.context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    // Tap en la tarjeta - cerrar si está revelado
                    if (isRevealed()) {
                        closeSwipe()
                        return true
                    }
                    return false
                }
            })
            
            cardView.setOnTouchListener { _, event ->
                // Primero dejar que el detector de gestos lo revise
                gestureDetector.onTouchEvent(event)
                
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = event.rawX
                        initialCardTranslation = cardView.translationX
                        isSwiping = false
                        true
                    }
                    
                    MotionEvent.ACTION_MOVE -> {
                        val deltaX = event.rawX - initialX
                        
                        // Solo considerar swipe si se mueve más de 10px
                        if (abs(deltaX) > 10 * density && !isSwiping) {
                            isSwiping = true
                            // Cerrar cualquier otro swipe abierto INMEDIATAMENTE al empezar a deslizar
                            onRevealed(this)
                        }
                        
                        if (isSwiping) {
                            val newTranslation = initialCardTranslation + deltaX
                            
                            // Solo permitir swipe hacia la izquierda (negativo)
                            // Y hacia la derecha solo si ya está revelado
                            when {
                                newTranslation < 0 -> {
                                    // Swipe izquierda - limitar al ancho del botón
                                    cardView.translationX = maxOf(newTranslation, -swipeThresholdPx)
                                }
                                newTranslation > 0 && initialCardTranslation < 0 -> {
                                    // Swipe derecha desde posición revelada - cerrar
                                    cardView.translationX = minOf(newTranslation, 0f)
                                }
                            }
                        }
                        isSwiping
                    }
                    
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        if (isSwiping) {
                            // Decidir si completar la revelación o cerrar
                            val currentTranslation = cardView.translationX
                            
                            if (currentTranslation < -swipeThresholdPx * 0.5f) {
                                // Más de la mitad - completar revelación
                                revealDeleteButton()
                            } else {
                                // Menos de la mitad - cerrar
                                closeSwipe()
                            }
                            isSwiping = false
                            true
                        } else {
                            false
                        }
                    }
                    
                    else -> false
                }
            }
        }
        
        private fun setupDeleteButton() {
            deleteButton.setOnClickListener {
                currentFavorite?.let { favorite ->
                    onDeleteClick(favorite, adapterPosition)
                }
            }
        }
        
        private fun revealDeleteButton() {
            cardView.animate()
                .translationX(-swipeThresholdPx)
                .setDuration(200)
                .start()
        }
        
        fun closeSwipe() {
            cardView.animate()
                .translationX(0f)
                .setDuration(200)
                .start()
        }
        
        private fun isRevealed(): Boolean = cardView.translationX < -10f
    }
}
