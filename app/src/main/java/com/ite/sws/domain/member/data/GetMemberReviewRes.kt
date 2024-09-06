package com.ite.sws.domain.member.data

/**
 * 회원 작성 리뷰 조회 Response DTO
 * @author 정은지
 * @since 2024.09.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.05  	정은지        최초 생성
 * </pre>
 */
data class GetMemberReviewRes(
    val shortFormId: Long,
    val shortFormUrl: String,
    val thumbnailImage: String
)
