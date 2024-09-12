package com.ite.sws.domain.checklist.data

/**
 * 체크리스트 항목 추가 Request DTO
 * @author 정은지
 * @since 2024.09.07
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.07  	정은지        최초 생성
 * </pre>
 */
data class PostCheckListReq (
    val item: String
)