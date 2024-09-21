package com.ite.sws.domain.review.data

/**
 * 리뷰를 얻을 수 있는 DTO
 * @author 구지웅
 * @since 2024.09.06
 * @version 1.0
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06   구지웅        최초 생성

 */
data class GetReviewRes(
    val shortFormId: Long,
    val productId: Long,
    val shortFormUrl: String,
    val thumbnailImage: String
)
