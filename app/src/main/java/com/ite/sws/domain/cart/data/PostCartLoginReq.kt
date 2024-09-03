package com.ite.sws.domain.cart.data

/**
 * 장바구니 로그인 요청 DTO
 * @author 남진수
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	남진수       최초 생성
 * </pre>
 */
data class PostCartLoginReq(
    val loginId: String,
    val password: String
)
