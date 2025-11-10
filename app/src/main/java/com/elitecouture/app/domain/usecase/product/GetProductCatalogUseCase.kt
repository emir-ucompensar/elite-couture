package com.elitecouture.app.domain.usecase.product

import com.elitecouture.app.data.repository.SupabaseProductRepository
import com.elitecouture.app.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caso de uso para obtener el catálogo completo de productos.
 * 
 * ✨ MIGRADO A SUPABASE ✨
 * 
 * Responsabilidades:
 * - Obtener productos desde Supabase
 * - Filtrar productos ocultos para invitados si es necesario
 * - Retornar lista de productos
 * 
 * @property productRepository repositorio de Supabase para productos
 */
class GetProductCatalogUseCase(
    private val productRepository: SupabaseProductRepository
) {
    /**
     * Ejecuta la obtención del catálogo.
     * 
     * @param includeGuestHidden si debe incluir productos ocultos para invitados
     * @return List<Product> lista de productos disponibles
     */
    suspend operator fun invoke(includeGuestHidden: Boolean = false): List<Product> = withContext(Dispatchers.IO) {
        productRepository.getCatalog(includeGuestHidden)
    }
}
