package com.elitecouture.app.data.repository

import com.elitecouture.app.domain.model.Store

object StoreRepository {
    private val supabaseStoreRepository = SupabaseStoreRepository()

    /**
     * Obtiene todas las tiendas desde Supabase
     */
    suspend fun getStores(): List<Store> {
        return supabaseStoreRepository.getAllStores()
    }

    /**
     * Obtiene una tienda por su ID
     */
    suspend fun getStoreById(id: Int): Store? {
        return supabaseStoreRepository.getStoreById(id)
    }

    /**
     * Obtiene el centro geográfico de todas las tiendas (para centrar el mapa)
     */
    suspend fun getCenterLocation(): Pair<Double, Double> {
        val stores = getStores()
        val avgLat = stores.map { it.latitude }.average()
        val avgLng = stores.map { it.longitude }.average()
        return Pair(avgLat, avgLng)
    }
}
