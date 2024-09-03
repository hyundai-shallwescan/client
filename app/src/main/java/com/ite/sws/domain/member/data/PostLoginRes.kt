package com.ite.sws.domain.member.data

/**
 * 로그인 Response DTO
 * @author 정은지
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03   정은지        최초 생성
 * </pre>
 */
data class PostLoginRes (
    val cartId: Long,
    val token: String? = null
)