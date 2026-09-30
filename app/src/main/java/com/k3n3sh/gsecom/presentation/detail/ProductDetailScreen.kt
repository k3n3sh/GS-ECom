package com.k3n3sh.gsecom.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.k3n3sh.gsecom.R
import com.k3n3sh.gsecom.domain.model.Product
import com.k3n3sh.gsecom.domain.model.ProductSize
import com.k3n3sh.gsecom.presentation.components.ErrorView
import com.k3n3sh.gsecom.presentation.components.HtmlText
import com.k3n3sh.gsecom.presentation.components.LoadingView
import com.k3n3sh.gsecom.presentation.components.ProductImage
import com.k3n3sh.gsecom.presentation.components.ProductLabels
import com.k3n3sh.gsecom.presentation.components.formatPrice
import com.k3n3sh.gsecom.presentation.components.toMessage

@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProductDetailContent(
        state = state,
        onBack = onBack,
        onRetry = viewModel::loadProduct,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(
    state: ProductDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when {
            state.product != null -> ProductDetails(product = state.product, modifier = contentModifier)
            state.error != null -> ErrorView(
                message = state.error.toMessage(),
                onRetry = onRetry,
                modifier = contentModifier,
            )
            else -> LoadingView(
                description = stringResource(R.string.loading_product),
                modifier = contentModifier,
            )
        }
    }
}

// TODO: two-pane layout for tablets
@Composable
private fun ProductDetails(
    product: Product,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // TODO: image gallery (API sends a media list)
        ProductImage(
            imageUrl = product.imageUrl,
            showRetryText = true,
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .aspectRatio(3f / 4f),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (product.labels.isNotEmpty()) {
                ProductLabels(labels = product.labels)
            }
            Text(
                text = product.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = formatPrice(product.price),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            if (product.colour.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.detail_colour, product.colour),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (product.fit != null) {
                Text(
                    text = stringResource(R.string.detail_fit, product.fit),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (!product.inStock) {
                Text(
                    text = stringResource(R.string.out_of_stock),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            if (product.sizes.isNotEmpty()) {
                SectionTitle(text = stringResource(R.string.detail_sizes))
                SizeChips(sizes = product.sizes)
            }
            if (product.descriptionHtml.isNotBlank()) {
                SectionTitle(text = stringResource(R.string.detail_description))
                HtmlText(html = product.descriptionHtml)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(top = 8.dp)
            .semantics { heading() },
    )
}

// TODO: size picker and add to bag
@Composable
private fun SizeChips(sizes: List<ProductSize>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        sizes.forEach { size ->
            val soldOutDescription = stringResource(R.string.size_sold_out, size.name)
            val colors = MaterialTheme.colorScheme
            Surface(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, if (size.inStock) colors.outline else colors.outlineVariant),
                color = colors.surface,
                modifier = if (size.inStock) {
                    Modifier
                } else {
                    Modifier.semantics { contentDescription = soldOutDescription }
                },
            ) {
                Text(
                    text = size.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (size.inStock) colors.onSurface else colors.onSurfaceVariant.copy(alpha = 0.5f),
                    textDecoration = if (size.inStock) null else TextDecoration.LineThrough,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
    }
}
