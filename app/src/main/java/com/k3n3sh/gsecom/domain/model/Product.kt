package com.k3n3sh.gsecom.domain.model

data class Product(
    val id: Long,
    val title: String,
    val colour: String,
    // Whole pounds
    val price: Int,
    val imageUrl: String?,
    val descriptionHtml: String,
    val labels: List<ProductLabel>,
    val sizes: List<ProductSize>,
    val inStock: Boolean,
    val fit: String?,
)

data class ProductSize(
    val name: String,
    val inStock: Boolean,
)

enum class ProductLabel {
    New,
    GoingFast,
    LimitedEdition,
    Popular,
    RecycledNylon,
    RecycledPolyester,
}
