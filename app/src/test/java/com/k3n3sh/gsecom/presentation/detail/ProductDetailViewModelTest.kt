package com.k3n3sh.gsecom.presentation.detail

import androidx.lifecycle.SavedStateHandle
import com.k3n3sh.gsecom.domain.model.Outcome
import com.k3n3sh.gsecom.domain.usecase.GetProductUseCase
import com.k3n3sh.gsecom.presentation.FakeProductRepository
import com.k3n3sh.gsecom.presentation.MainDispatcherRule
import com.k3n3sh.gsecom.presentation.testProduct
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Saved id loads that product
    @Test
    fun openProduct_showsDetails() = runTest {
        val repository = FakeProductRepository()
        repository.productsOutcome = Outcome.Success(listOf(testProduct(1), testProduct(2)))
        val savedStateHandle = SavedStateHandle(mapOf(ProductDetailViewModel.PRODUCT_ID_KEY to 2L))

        val viewModel = ProductDetailViewModel(savedStateHandle, GetProductUseCase(repository))

        assertEquals("product 2 is shown", testProduct(2), viewModel.uiState.value.product)
    }
}
