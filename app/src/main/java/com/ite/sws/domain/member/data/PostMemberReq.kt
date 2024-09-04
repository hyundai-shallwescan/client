package com.ite.sws.domain.member.data

/**
 * 회원가입 Request DTO
 * @author 정은지
 * @since 2024.09.04
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.04   정은지        최초 생성
 * </pre>
 */
data class PostMemberReq (
    val loginId: String,
    val password: String,
    val name: String,
    val gender: Char,
    val age: Int,
    val phoneNumber: String,
    val carNumber: String?
)