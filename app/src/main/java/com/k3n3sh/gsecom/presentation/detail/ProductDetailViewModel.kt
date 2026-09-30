package com.k3n3sh.gsecom.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k3n3sh.gsecom.domain.model.DataError
import com.k3n3sh.gsecom.domain.model.Outcome
import com.k3n3sh.gsecom.domain.model.Product
import com.k3n3sh.gsecom.domain.usecase.GetProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: DataError? = null,
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProductUseCase: GetProductUseCase,
) : ViewModel() {

    // From the nav route, survives process death
    private val productId: Long = checkNotNull(savedStateHandle[PRODUCT_ID_KEY]) {
        "ProductDetailViewModel needs a $PRODUCT_ID_KEY"
    }

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    fun loadProduct() {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true, error = null) }
            when (val outcome = getProductUseCase.execute(productId)) {
                is Outcome.Success -> _uiState.update { state ->
                    state.copy(product = outcome.data, isLoading = false)
                }
                is Outcome.Failure -> _uiState.update { state ->
                    state.copy(isLoading = false, error = outcome.error)
                }
            }
        }
    }

    companion object {
        // Same name as the route property
        const val PRODUCT_ID_KEY: String = "productId"
    }
}
