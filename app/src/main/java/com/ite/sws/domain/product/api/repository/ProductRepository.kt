package com.ite.sws.domain.product.api.repository

import com.ite.sws.domain.product.api.service.ProductService
import com.ite.sws.domain.product.data.GetProductReviewRes

class ProductRepository(private val productService: ProductService) {

    suspend fun getProductReviews(productId: Long, page: Int, size: Int): List<GetProductReviewRes>? {
        return try {
            val response = productService.getProductReviews(productId, page, size)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
