package com.elitecouture.app.domain.usecase

import com.elitecouture.app.data.repository.ProductRepository
import com.elitecouture.app.domain.model.Product

class GetProductCatalogUseCase(private val productRepository: ProductRepository) {
    operator fun invoke(includeGuestHidden: Boolean): List<Product> {
        return productRepository.getCatalog(includeGuestHidden)
    }
}
