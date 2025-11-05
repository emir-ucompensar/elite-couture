package com.elitecouture.app.domain.usecase.product

import com.elitecouture.app.data.repository.ProductRepository
import com.elitecouture.app.domain.model.Product
import com.elitecouture.app.util.Constants

/**
 * Caso de uso para filtrar productos por categoría.
 * 
 * Responsabilidades:
 * - Obtener productos del repositorio
 * - Filtrar por categoría específica
 * - Retornar lista filtrada
 * 
 * @property productRepository repositorio de productos
 */
class GetProductsByCategoryUseCase(
    private val productRepository: ProductRepository
) {
    /**
     * Ejecuta la búsqueda de productos por categoría.
     * 
     * @param category categoría de productos (pants, jackets, coats, etc.)
     * @param includeGuestHidden si debe incluir productos ocultos para invitados
     * @return List<Product> lista de productos de la categoría
     */
    operator fun invoke(category: String, includeGuestHidden: Boolean = false): List<Product> {
        val allProducts = productRepository.getCatalog(includeGuestHidden)
        
        // Si es "all", retornar todos
        if (category == Constants.Store.FILTER_ALL_PRODUCTS) {
            return allProducts
        }
        
        // Filtrar por categoría específica
        return allProducts.filter { product -> 
            product.type?.equals(category, ignoreCase = true) == true
        }
    }
}
