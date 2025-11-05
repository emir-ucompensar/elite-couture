package com.elitecouture.app.domain.usecase.product

import com.elitecouture.app.data.repository.ProductRepository
import com.elitecouture.app.domain.model.Product

/**
 * Caso de uso para buscar productos por texto.
 * 
 * Responsabilidades:
 * - Obtener productos del repositorio
 * - Buscar por nombre, descripción o categoría
 * - Retornar lista de coincidencias
 * 
 * @property productRepository repositorio de productos
 */
class SearchProductsUseCase(
    private val productRepository: ProductRepository
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
    operator fun invoke(query: String, includeGuestHidden: Boolean = false): List<Product> {
        if (query.isBlank()) {
            return productRepository.getCatalog(includeGuestHidden)
        }
        
        val allProducts = productRepository.getCatalog(includeGuestHidden)
        val searchQuery = query.lowercase()
        
        return allProducts.filter { product ->
            val description = product.description ?: ""
            val productType = product.type ?: ""
            val productGender = product.gender ?: ""
            
            product.name.lowercase().contains(searchQuery) ||
            description.lowercase().contains(searchQuery) ||
            productType.lowercase().contains(searchQuery) ||
            productGender.lowercase().contains(searchQuery)
        }
    }
}
