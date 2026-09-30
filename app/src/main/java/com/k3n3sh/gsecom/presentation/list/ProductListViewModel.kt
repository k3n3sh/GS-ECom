package com.k3n3sh.gsecom.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k3n3sh.gsecom.domain.model.DataError
import com.k3n3sh.gsecom.domain.model.Outcome
import com.k3n3sh.gsecom.domain.model.Product
import com.k3n3sh.gsecom.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    // Full screen if the list is empty, else a Retry row
    val error: DataError? = null,
)

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    // Once here, so rotation doesn't reload
    init {
        loadProducts()
    }

    // Uses the cache if there is one
    fun loadProducts() {
        load(forceRefresh = false)
    }

    // Refresh button and Retry row
    fun refresh() {
        load(forceRefresh = true)
    }

    private fun load(forceRefresh: Boolean) {
        // Already loading
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = state.products.isEmpty(),
                    isRefreshing = state.products.isNotEmpty(),
                    error = null,
                )
            }
            when (val outcome = getProductsUseCase.execute(forceRefresh)) {
                is Outcome.Success -> _uiState.update { state ->
                    state.copy(
                        products = outcome.data,
                        isLoading = false,
                        isRefreshing = false,
                    )
                }
                is Outcome.Failure -> _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = outcome.error,
                    )
                }
            }
        }
    }
}
