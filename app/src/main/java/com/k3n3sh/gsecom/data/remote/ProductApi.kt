package com.k3n3sh.gsecom.data.remote

import com.k3n3sh.gsecom.data.remote.dto.ProductsResponseDto
import kotlinx.serialization.json.Json
import retrofit2.http.GET

interface ProductApi {

    @GET("training/mock-product-responses/algolia-example-payload.json")
    suspend fun getProducts(): ProductsResponseDto
}

// Lenient parsing
val ApiJson: Json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}
