package com.elitecouture.app.domain.usecase.product

import com.elitecouture.app.data.repository.SupabaseProductRepository
import com.elitecouture.app.domain.model.Product
import com.elitecouture.app.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caso de uso para filtrar productos por categoría.
 * 
 * ✨ MIGRADO A SUPABASE ✨
 * 
 * Responsabilidades:
 * - Obtener productos desde Supabase
 * - Filtrar por categoría específica
 * - Retornar lista filtrada
 * 
 * @property productRepository repositorio de Supabase para productos
 */
class GetProductsByCategoryUseCase(
    private val productRepository: SupabaseProductRepository
) {
    /**
     * Ejecuta la búsqueda de productos por categoría.
     * 
     * @param category categoría de productos (pants, jackets, coats, etc.)
     * @param includeGuestHidden si debe incluir productos ocultos para invitados
     * @return List<Product> lista de productos de la categoría
     */
    suspend operator fun invoke(category: String?, includeGuestHidden: Boolean = false): List<Product> = withContext(Dispatchers.IO) {
        try {
            val allProducts = productRepository.getCatalog(includeGuestHidden)
            
            // Filtrar por categoría si se especifica
            category?.let { categoryFilter ->
                allProducts.filter { product ->
                    product.type.equals(categoryFilter, ignoreCase = true)
                }
            } ?: allProducts
        } catch (e: Exception) {
            emptyList()
        }
    }
}
