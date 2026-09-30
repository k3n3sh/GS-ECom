package com.k3n3sh.gsecom.presentation.list

import com.k3n3sh.gsecom.domain.model.DataError
import com.k3n3sh.gsecom.domain.model.Outcome
import com.k3n3sh.gsecom.domain.usecase.GetProductsUseCase
import com.k3n3sh.gsecom.presentation.FakeProductRepository
import com.k3n3sh.gsecom.presentation.MainDispatcherRule
import com.k3n3sh.gsecom.presentation.testProduct
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProductListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeProductRepository()

    // Products show on start
    @Test
    fun loadSucceeds_showsProducts() = runTest {
        repository.productsOutcome = Outcome.Success(listOf(testProduct(1)))

        val viewModel = ProductListViewModel(GetProductsUseCase(repository))

        assertEquals("products are shown", listOf(testProduct(1)), viewModel.uiState.value.products)
    }

    // Error shows when the first load fails
    @Test
    fun loadFails_showsError() = runTest {
        repository.productsOutcome = Outcome.Failure(DataError.NoInternet)

        val viewModel = ProductListViewModel(GetProductsUseCase(repository))

        assertEquals("error is shown", DataError.NoInternet, viewModel.uiState.value.error)
    }

    // Old list stays when a refresh fails
    @Test
    fun refreshFails_keepsProductsAndShowsRetry() = runTest {
        repository.productsOutcome = Outcome.Success(listOf(testProduct(1)))
        val viewModel = ProductListViewModel(GetProductsUseCase(repository))
        repository.productsOutcome = Outcome.Failure(DataError.Network)

        viewModel.refresh()

        assertEquals("old products are kept", listOf(testProduct(1)), viewModel.uiState.value.products)
        assertEquals("error is shown", DataError.Network, viewModel.uiState.value.error)
    }
}
