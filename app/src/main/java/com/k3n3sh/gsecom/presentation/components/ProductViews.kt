package com.k3n3sh.gsecom.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.k3n3sh.gsecom.R
import com.k3n3sh.gsecom.domain.model.ProductLabel
import java.text.NumberFormat
import java.util.Locale

// Grey tile while loading, tap to retry on error
@Composable
fun ProductImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    showRetryText: Boolean = false,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl != null) {
            var attempt by remember(imageUrl) { mutableIntStateOf(0) }
            var failed by remember(imageUrl) { mutableStateOf(false) }

            // New key, new request
            key(attempt) {
                AsyncImage(
                    model = imageUrl,
                    // Decorative
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    onError = { failed = true },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (failed) {
                val retry: () -> Unit = {
                    failed = false
                    attempt++
                }
                if (showRetryText) {
                    TextButton(onClick = retry) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.image_failed_retry))
                    }
                } else {
                    IconButton(onClick = retry) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.action_retry_image),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductLabels(
    labels: List<ProductLabel>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        labels.forEach { label ->
            ProductLabelBadge(label = label)
        }
    }
}

@Composable
fun ProductLabelBadge(
    label: ProductLabel,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    // Rust for urgency
    val containerColor: Color = when (label) {
        ProductLabel.GoingFast -> colors.tertiary
        ProductLabel.RecycledNylon, ProductLabel.RecycledPolyester -> colors.secondaryContainer
        else -> colors.primary
    }
    val contentColor: Color = when (label) {
        ProductLabel.GoingFast -> colors.onTertiary
        ProductLabel.RecycledNylon, ProductLabel.RecycledPolyester -> colors.onSecondaryContainer
        else -> colors.onPrimary
    }
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Text(
            text = stringResource(label.textRes()),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
        )
    }
}

@StringRes
private fun ProductLabel.textRes(): Int {
    return when (this) {
        ProductLabel.New -> R.string.label_new
        ProductLabel.GoingFast -> R.string.label_going_fast
        ProductLabel.LimitedEdition -> R.string.label_limited_edition
        ProductLabel.Popular -> R.string.label_popular
        ProductLabel.RecycledNylon -> R.string.label_recycled_nylon
        ProductLabel.RecycledPolyester -> R.string.label_recycled_polyester
    }
}

// TODO: currency from the API (assumes GBP)
fun formatPrice(pounds: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale.UK)
    format.maximumFractionDigits = 0
    return format.format(pounds)
}

// Two or more line breaks, with any spaces between
private val ExtraLineBreaks = Regex("[\\s\\u00A0]*\\n[\\s\\u00A0]*\\n[\\s\\u00A0]*")

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
) {
    val linkColor = MaterialTheme.colorScheme.primary
    val text = remember(html, linkColor) {
        val linkStyles = TextLinkStyles(
            style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
        )
        AnnotatedString.fromHtml(html, linkStyles = linkStyles).tidyLineBreaks()
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
    )
}

// At most one blank line
private fun AnnotatedString.tidyLineBreaks(): AnnotatedString {
    val source = this
    val tidied = buildAnnotatedString {
        var start = 0
        ExtraLineBreaks.findAll(source.text).forEach { match ->
            append(source.subSequence(start, match.range.first))
            append("\n\n")
            start = match.range.last + 1
        }
        append(source.subSequence(start, source.length))
    }
    val first = tidied.text.indexOfFirst { !it.isWhitespace() }
    if (first == -1) return AnnotatedString("")
    val last = tidied.text.indexOfLast { !it.isWhitespace() }
    return tidied.subSequence(first, last + 1)
}
