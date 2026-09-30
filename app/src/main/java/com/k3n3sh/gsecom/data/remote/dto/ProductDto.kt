package com.k3n3sh.gsecom.data.remote.dto

import com.k3n3sh.gsecom.domain.model.Product
import com.k3n3sh.gsecom.domain.model.ProductLabel
import com.k3n3sh.gsecom.domain.model.ProductSize
import kotlinx.serialization.Serializable

// All optional, checked in the mapper

@Serializable
data class ProductsResponseDto(
    val hits: List<ProductDto> = emptyList(),
)

@Serializable
data class ProductDto(
    val id: Long? = null,
    val title: String? = null,
    val colour: String? = null,
    val price: Int? = null,
    val description: String? = null,
    val labels: List<String>? = null,
    val featuredMedia: MediaDto? = null,
    val availableSizes: List<SizeDto>? = null,
    val inStock: Boolean? = null,
    val fit: String? = null,
)

@Serializable
data class MediaDto(
    val src: String? = null,
)

@Serializable
data class SizeDto(
    val size: String? = null,
    val inStock: Boolean? = null,
)

// Null if id, title or price is missing
fun ProductDto.toDomain(): Product? {
    val productId = id ?: return null
    val productTitle = title ?: return null
    val productPrice = price ?: return null

    val productSizes = availableSizes.orEmpty().mapNotNull { it.toDomain() }
    return Product(
        id = productId,
        title = productTitle,
        colour = colour.orEmpty(),
        price = productPrice,
        imageUrl = featuredMedia?.src?.toImageUrl(),
        descriptionHtml = description.orEmpty(),
        labels = labels.orEmpty().mapNotNull { it.toProductLabel() },
        sizes = productSizes,
        inStock = inStock ?: productSizes.any { it.inStock },
        fit = fit,
    )
}

private fun SizeDto.toDomain(): ProductSize? {
    val name = size ?: return null
    return ProductSize(name = name.uppercase(), inStock = inStock == true)
}

// Unknown labels are dropped
private fun String.toProductLabel(): ProductLabel? {
    return when (this) {
        "new" -> ProductLabel.New
        "going-fast" -> ProductLabel.GoingFast
        "limited-edition" -> ProductLabel.LimitedEdition
        "popular" -> ProductLabel.Popular
        "recycled-nylon" -> ProductLabel.RecycledNylon
        "recycled-polyester" -> ProductLabel.RecycledPolyester
        else -> null
    }
}

// Android blocks plain http
private fun String.toImageUrl(): String? {
    return takeIf { it.startsWith("https://") }
}
