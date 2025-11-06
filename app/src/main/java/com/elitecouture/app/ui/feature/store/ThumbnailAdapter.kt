package com.elitecouture.app.ui.feature.store

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.elitecouture.app.R

/**
 * Adapter para mostrar miniaturas de las imágenes del producto.
 * Permite al usuario ver y seleccionar qué imagen mostrar en el carrusel principal.
 */
class ThumbnailAdapter(
    private val images: List<String>,
    private val onThumbnailClick: (Int) -> Unit
) : RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail, parent, false)
        return ThumbnailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        val imageUri = images[position]
        holder.bind(imageUri, position == selectedPosition)
        
        holder.itemView.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
            onThumbnailClick(position)
        }
    }

    override fun getItemCount(): Int = images.size

    /**
     * Actualiza la posición seleccionada (llamado cuando el ViewPager2 cambia de página)
     */
    fun updateSelectedPosition(position: Int) {
        val oldPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedPosition)
    }

    class ThumbnailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.image_thumbnail)
        private val border: View = view.findViewById(R.id.border_selected)

        fun bind(imageUri: String, isSelected: Boolean) {
            // Cargar miniatura con Coil
            imageView.load(imageUri) {
                crossfade(200)
                scale(Scale.FILL)
                error(R.color.background_light)
                placeholder(R.color.background_light)
            }

            // Mostrar borde si está seleccionada
            if (isSelected) {
                border.setBackgroundColor(Color.parseColor("#560E2D")) // color_primary
                border.alpha = 0.7f
                border.setPadding(2, 2, 2, 2)
            } else {
                border.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}
