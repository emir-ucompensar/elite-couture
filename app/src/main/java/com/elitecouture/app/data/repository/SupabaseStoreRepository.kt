package com.elitecouture.app.data.repository

import com.elitecouture.app.data.remote.SupabaseStoreService
import com.elitecouture.app.domain.model.Store

/**
 * Repositorio de tiendas usando Supabase como backend.
 * Todas las operaciones son suspend y usan corrutinas.
 */
class SupabaseStoreRepository {

	/**
	 * Obtiene todas las tiendas desde Supabase
	 */
	suspend fun getAllStores(): List<Store> {
		return SupabaseStoreService.getAllStores()
	}

	/**
	 * Obtiene una tienda por ID
	 */
	suspend fun getStoreById(id: Int): Store? {
		return SupabaseStoreService.getStoreById(id)
	}
}
