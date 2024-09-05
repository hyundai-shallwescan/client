package com.ite.sws.domain.member.data

/**
 * 회원 정보 수정 Request DTO
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
data class PatchMemberReq(
    val password: String,
    val phoneNumber: String,
    val carNumber: String? = null
)
