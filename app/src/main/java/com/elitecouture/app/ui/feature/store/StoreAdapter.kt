package com.elitecouture.app.ui.feature.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elitecouture.app.R
import com.elitecouture.app.domain.model.Store
import com.google.android.material.button.MaterialButton

class StoreAdapter(
    private val stores: List<Store>,
    private val onDirectionsClick: (Store) -> Unit,
    private val onStoreClick: (Store) -> Unit
) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    inner class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.store_item_name)
        val address: TextView = itemView.findViewById(R.id.store_item_address)
        val phone: TextView = itemView.findViewById(R.id.store_item_phone)
        val hours: TextView = itemView.findViewById(R.id.store_item_hours)
        val directionsButton: MaterialButton = itemView.findViewById(R.id.store_item_directions_button)
        
        init {
            directionsButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDirectionsClick(stores[position])
                }
            }
            
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onStoreClick(stores[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_store, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = stores[position]
        holder.name.text = store.name
        holder.address.text = store.address
        holder.phone.text = store.phone
        holder.hours.text = store.hours
    }

    override fun getItemCount(): Int = stores.size
}
