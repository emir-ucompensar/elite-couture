package com.elitecouture.app.domain.usecase.product

import com.elitecouture.app.data.repository.SupabaseProductRepository
import com.elitecouture.app.domain.model.Product

/**
 * Caso de uso para buscar productos por texto.
 * 
 * ✨ MIGRADO A SUPABASE ✨
 * 
 * Responsabilidades:
 * - Obtener productos desde Supabase
 * - Buscar por nombre, descripción o categoría
 * - Retornar lista de coincidencias
 * 
 * @property productRepository repositorio de Supabase para productos
 */
class SearchProductsUseCase(
    private val productRepository: SupabaseProductRepository
) {
    /**
     * Ejecuta la búsqueda de productos.
     * 
     * Busca coincidencias en:
     * - Nombre del producto
     * - Descripción
     * - Categoría
     * - Género
     * 
     * @param query texto de búsqueda
     * @param includeGuestHidden si debe incluir productos ocultos para invitados
     * @return List<Product> lista de productos que coinciden
     */
    suspend operator fun invoke(query: String, includeGuestHidden: Boolean = false): List<Product> {
        return try {
            if (query.isBlank()) {
                productRepository.getCatalog(includeGuestHidden)
            } else {
                productRepository.searchProducts(query)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
