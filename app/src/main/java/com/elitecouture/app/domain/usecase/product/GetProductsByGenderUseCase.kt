package com.elitecouture.app.domain.usecase.product

import com.elitecouture.app.data.repository.ProductRepository
import com.elitecouture.app.domain.model.Product
import com.elitecouture.app.util.Constants

/**
 * Caso de uso para filtrar productos por género.
 * 
 * Responsabilidades:
 * - Obtener productos del repositorio
 * - Filtrar por género (men/women)
 * - Retornar lista filtrada
 * 
 * @property productRepository repositorio de productos
 */
class GetProductsByGenderUseCase(
    private val productRepository: ProductRepository
) {
    /**
     * Ejecuta la búsqueda de productos por género.
     * 
     * @param gender género de productos (men, women)
     * @param includeGuestHidden si debe incluir productos ocultos para invitados
     * @return List<Product> lista de productos del género
     */
    operator fun invoke(gender: String, includeGuestHidden: Boolean = false): List<Product> {
        val allProducts = productRepository.getCatalog(includeGuestHidden)
        
        // Si es "all", retornar todos
        if (gender == Constants.Store.FILTER_ALL_PRODUCTS) {
            return allProducts
        }
        
        // Filtrar por género específico
        return allProducts.filter { it.gender.equals(gender, ignoreCase = true) }
    }
}
