package com.elitecouture.app.data.repository

import com.elitecouture.app.data.local.dao.ProductDao
import com.elitecouture.app.data.local.entity.ProductEntity
import com.elitecouture.app.domain.model.Product
import java.util.UUID

/** Provides catalog access hiding the persistence details. */
class ProductRepository(private val productDao: ProductDao) {
    fun seedCatalog(products: List<Product>) {
        products.forEach { product ->
            val normalized = if (product.uuid.isBlank()) {
                product.copy(uuid = UUID.randomUUID().toString())
            } else {
                product
            }
            productDao.insertOrReplace(ProductEntity.fromDomain(normalized))
        }
    }

    fun getCatalog(includeGuestHidden: Boolean): List<Product> {
        return productDao.fetchAll(includeGuestHidden).map { it.toDomain() }
    }

    fun clearCatalog() {
        productDao.deleteAll()
    }
}
