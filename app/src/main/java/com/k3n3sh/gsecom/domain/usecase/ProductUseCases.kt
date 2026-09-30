package com.k3n3sh.gsecom.domain.usecase

import com.k3n3sh.gsecom.domain.model.Outcome
import com.k3n3sh.gsecom.domain.model.Product
import com.k3n3sh.gsecom.domain.repository.ProductRepository
import javax.inject.Inject

// TODO: filtering and sorting
class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    suspend fun execute(forceRefresh: Boolean): Outcome<List<Product>> {
        return repository.getProducts(forceRefresh)
    }
}

class GetProductUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    suspend fun execute(id: Long): Outcome<Product> {
        return repository.getProduct(id)
    }
}
