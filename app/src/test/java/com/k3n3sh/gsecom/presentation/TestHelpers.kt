package com.k3n3sh.gsecom.presentation

import com.k3n3sh.gsecom.domain.model.Outcome
import com.k3n3sh.gsecom.domain.model.Product
import com.k3n3sh.gsecom.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// Runs viewModelScope on a test dispatcher
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

// Tests set the outcome they need
class FakeProductRepository : ProductRepository {

    var productsOutcome: Outcome<List<Product>> = Outcome.Success(emptyList())

    override suspend fun getProducts(forceRefresh: Boolean): Outcome<List<Product>> {
        return productsOutcome
    }

    override suspend fun getProduct(id: Long): Outcome<Product> {
        val products = (productsOutcome as Outcome.Success).data
        return Outcome.Success(products.first { it.id == id })
    }
}

fun testProduct(id: Long): Product {
    return Product(
        id = id,
        title = "Product $id",
        colour = "Black",
        price = 50,
        imageUrl = "https://example.com/$id.jpg",
        descriptionHtml = "<p>Description $id</p>",
        labels = emptyList(),
        sizes = emptyList(),
        inStock = true,
        fit = null,
    )
}
