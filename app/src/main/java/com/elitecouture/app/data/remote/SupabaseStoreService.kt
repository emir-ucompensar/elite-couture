package com.elitecouture.app.data.remote

import com.elitecouture.app.domain.model.Store
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

object SupabaseStoreService {

	@Serializable
	data class StoreDto(
		val id: Int? = null,
		val name: String,
		val address: String,
		val phone: String,
		val hours: String,
		val latitude: Double,
		val longitude: Double
	)

	private fun StoreDto.toDomain(): Store = Store(
		id = id ?: 0,
		name = name,
		address = address,
		phone = phone,
		hours = hours,
		latitude = latitude,
		longitude = longitude
	)

	/**
	 * Obtiene todas las tiendas desde Supabase
	 */
	suspend fun getAllStores(): List<Store> = withContext(Dispatchers.IO) {
		val results = SupabaseClientProvider.client
			.from("stores")
			.select()
			.decodeList<StoreDto>()
		results.map { it.toDomain() }
	}

	/**
	 * Obtiene una tienda por ID
	 */
	suspend fun getStoreById(id: Int): Store? = withContext(Dispatchers.IO) {
		try {
			val result = SupabaseClientProvider.client
				.from("stores")
				.select {
					filter { eq("id", id) }
				}
				.decodeSingle<StoreDto>()
			result.toDomain()
		} catch (e: Exception) {
			null
		}
	}
}
