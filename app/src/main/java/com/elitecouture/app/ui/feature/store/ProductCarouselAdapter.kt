package com.elitecouture.app.ui.feature.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.elitecouture.app.R

/** 
 * Adapter for ViewPager2 carousel images. 
 * Uses Coil to load images from assets using file:///android_asset/ URIs.
 */
class ProductCarouselAdapter(private val images: List<String>) : RecyclerView.Adapter<ProductCarouselAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = images[position]
        
        // Usar Coil para cargar imagen desde assets
        // URIs con formato: "file:///android_asset/images/product_01_img_1.webp"
        holder.image.load(imageUri) {
            crossfade(300)
            scale(Scale.FIT) // Mantener proporciones sin recortar
            error(R.color.background_light)
            placeholder(R.color.background_light)
        }
        
        // Forzar que la imagen mantenga sus dimensiones correctas
        holder.image.scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image_item)
    }
}
