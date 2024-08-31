package com.ite.sws.domain.member.data

/**
 * JWT 토큰 DTO
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
data class JwtToken (
    val grantType: String,
    val accessToken: String,
    val refreshToken: String
)