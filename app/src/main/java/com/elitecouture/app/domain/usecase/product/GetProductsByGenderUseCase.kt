package com.elitecouture.app.domain.usecase.product

import com.elitecouture.app.data.repository.SupabaseProductRepository
import com.elitecouture.app.domain.model.Product
import com.elitecouture.app.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caso de uso para filtrar productos por género.
 * 
 * ✨ MIGRADO A SUPABASE ✨
 * 
 * Responsabilidades:
 * - Obtener productos desde Supabase
 * - Filtrar por género (men/women)
 * - Retornar lista filtrada
 * 
 * @property productRepository repositorio de Supabase para productos
 */
class GetProductsByGenderUseCase(
    private val productRepository: SupabaseProductRepository
) {
    /**
     * Ejecuta la búsqueda de productos por género.
     * 
     * @param gender género de productos (men, women)
     * @param includeGuestHidden si debe incluir productos ocultos para invitados
     * @return List<Product> lista de productos del género
     */
    suspend operator fun invoke(gender: String?, includeGuestHidden: Boolean = false): List<Product> = withContext(Dispatchers.IO) {
        try {
            val allProducts = productRepository.getCatalog(includeGuestHidden)
            
            // Filtrar por género si se especifica
            gender?.let { genderFilter ->
                allProducts.filter { product ->
                    product.gender.equals(genderFilter, ignoreCase = true)
                }
            } ?: allProducts
        } catch (e: Exception) {
            emptyList()
        }
    }
}
