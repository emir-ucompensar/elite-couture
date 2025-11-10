package com.elitecouture.app.data.repository

import com.elitecouture.app.data.remote.SupabaseProductService
import com.elitecouture.app.domain.model.Product

/**
 * Product repository using Supabase as backend.
 * 
 * This repository replaces the local SQLite implementation with cloud-based storage.
 * All operations are now asynchronous and use Kotlin coroutines.
 */
class SupabaseProductRepository {
    
    /**
     * Seeds the catalog with initial products.
     * 
     * @param products List of products to seed
     * @return List of created products with server-generated IDs
     */
    suspend fun seedCatalog(products: List<Product>): List<Product> {
        return SupabaseProductService.createProducts(products)
    }
    
    /**
     * Creates a new product.
     * 
     * @param product Product to create
     * @return Created product with server-generated ID
     */
    suspend fun createProduct(product: Product): Product {
        return SupabaseProductService.createProduct(product)
    }
    
    /**
     * Retrieves the full product catalog.
     * 
     * @param includeGuestHidden If true, includes products not visible to guests
     * @return List of products
     */
    suspend fun getCatalog(includeGuestHidden: Boolean = false): List<Product> {
        return SupabaseProductService.getAllProducts(includeGuestHidden)
    }
    
    /**
     * Retrieves a product by UUID.
     * 
     * @param uuid Product UUID
     * @return Product if found, null otherwise
     */
    suspend fun getProductByUuid(uuid: String): Product? {
        return SupabaseProductService.getProductByUuid(uuid)
    }
    
    /**
     * Searches products by name.
     * 
     * @param query Search query
     * @return List of matching products
     */
    suspend fun searchProducts(query: String): List<Product> {
        return SupabaseProductService.searchProducts(query)
    }
    
    /**
     * Filters products by type and/or gender.
     * 
     * @param type Product type (e.g., "Vestido", "Camisa")
     * @param gender Product gender (e.g., "Hombre", "Mujer")
     * @return List of matching products
     */
    suspend fun filterProducts(type: String? = null, gender: String? = null): List<Product> {
        return SupabaseProductService.filterProducts(type, gender)
    }
    
    /**
     * Updates an existing product.
     * 
     * @param product Product with updated data
     * @return Updated product
     */
    suspend fun updateProduct(product: Product): Product {
        return SupabaseProductService.updateProduct(product)
    }
    
    /**
     * Updates product stock.
     * 
     * @param uuid Product UUID
     * @param newStock New stock quantity
     */
    suspend fun updateStock(uuid: String, newStock: Int) {
        SupabaseProductService.updateStock(uuid, newStock)
    }
    
    /**
     * Deletes a product.
     * 
     * @param uuid Product UUID
     */
    suspend fun deleteProduct(uuid: String) {
        SupabaseProductService.deleteProduct(uuid)
    }
    
    /**
     * Clears the entire catalog (for testing only).
     * 
     * ⚠️ WARNING: This will delete ALL products in the database!
     */
    suspend fun clearCatalog() {
        SupabaseProductService.deleteAllProducts()
    }
}
