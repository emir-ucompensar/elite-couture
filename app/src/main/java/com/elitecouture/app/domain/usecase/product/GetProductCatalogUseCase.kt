package com.elitecouture.app.domain.usecase.product

import com.elitecouture.app.data.repository.ProductRepository
import com.elitecouture.app.domain.model.Product

/**
 * Caso de uso para obtener el catálogo completo de productos.
 * 
 * Responsabilidades:
 * - Obtener productos del repositorio
 * - Filtrar productos ocultos para invitados si es necesario
 * - Retornar lista de productos
 * 
 * @property productRepository repositorio de productos
 */
class GetProductCatalogUseCase(
    private val productRepository: ProductRepository
) {
    /**
     * Ejecuta la obtención del catálogo.
     * 
     * @param includeGuestHidden si debe incluir productos ocultos para invitados
     * @return List<Product> lista de productos disponibles
     */
    operator fun invoke(includeGuestHidden: Boolean = false): List<Product> {
        return productRepository.getCatalog(includeGuestHidden)
    }
}
