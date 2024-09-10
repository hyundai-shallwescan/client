package com.ite.sws.domain.payment.data

/**
 * 상품 추천 Response DTO
 * @author 김민정
 * @since 2024.09.10
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.10  김민정       최초 생성
 * </pre>
 */
data class GetRecommendRes (
    val message: String,
    val remainingParkingFee: Int,
    val productId: Long,
    val productName: String,
    val productPrice: Int,
    val thumbnailImage: String,
)