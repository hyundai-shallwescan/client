package com.ite.sws.domain.product.data

/**
 * 상품 리뷰 조회 DTO
 * @author 구지웅
 * @since 2024.09.13
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.13  구지웅       최초 생성
 * </pre>
 */
data class GetProductReviewRes(
    val shortFormId: Long,
    val productId: Long,
    val shortFormUrl: String,
    val shortFormThumbnailImage: String,
    val createdAt: String,
    val price: Long,
    val name: String,
    val productThumbnailImage: String,
    val descriptionImage: String,
    val barcode: String,
    val description: String
)
