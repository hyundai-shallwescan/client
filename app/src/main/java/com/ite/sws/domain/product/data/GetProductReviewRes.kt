package com.ite.sws.domain.product.data

import java.time.LocalDateTime

data class GetProductReviewRes(
    val shortFormId: Long,
    val productId: Long,
    val shortFormUrl: String,
    val shortFormThumbnailImage: String,
    val createdAt: LocalDateTime,
    val price: Long,
    val name: String,
    val productThumbnailImage: String,
    val descriptionImage: String,
    val barcode: String,
    val description: String
)
