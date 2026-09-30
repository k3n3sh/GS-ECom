package com.k3n3sh.gsecom.domain.repository

import com.k3n3sh.gsecom.domain.model.Outcome
import com.k3n3sh.gsecom.domain.model.Product

interface ProductRepository {

    // Cached unless forceRefresh
    suspend fun getProducts(forceRefresh: Boolean): Outcome<List<Product>>

    suspend fun getProduct(id: Long): Outcome<Product>
}
