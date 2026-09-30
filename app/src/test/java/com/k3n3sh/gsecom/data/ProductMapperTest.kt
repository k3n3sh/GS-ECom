package com.k3n3sh.gsecom.data

import com.k3n3sh.gsecom.data.remote.dto.ProductDto
import com.k3n3sh.gsecom.data.remote.dto.toDomain
import com.k3n3sh.gsecom.domain.model.ProductLabel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductMapperTest {

    // Label is mapped, missing image is null
    @Test
    fun productDto_mapsToProduct() {
        val dto = ProductDto(id = 1, title = "Speed Leggings", price = 50, labels = listOf("going-fast"))

        val product = dto.toDomain()

        assertEquals("label is mapped", listOf(ProductLabel.GoingFast), product?.labels)
        assertNull("no image gives null", product?.imageUrl)
    }
}
