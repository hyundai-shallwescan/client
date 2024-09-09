package com.ite.sws.domain.review.data

/**
 * Review PostCreate Dto class
 * @author 구지웅
 * @since 2024.09.06
 * @version 1.0
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06   구지웅        최초 생성
 */
data class PostCreateReviewReq(
    val paymentId: Long,
    val productId: Long
)
