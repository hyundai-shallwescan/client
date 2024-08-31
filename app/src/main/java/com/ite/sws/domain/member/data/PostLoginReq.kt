package com.ite.sws.domain.member.data

/**
 * 로그인 Request DTO
 * @author 정은지
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31   정은지        최초 생성
 * </pre>
 */
data class PostLoginReq(
    val loginId: String,
    val password: String
)