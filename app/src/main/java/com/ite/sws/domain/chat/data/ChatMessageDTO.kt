package com.ite.sws.domain.chat.data

/**
 * 채팅 메시지 DTO
 * @author 남진수
 * @since 2024.08.30
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.30  	남진수       최초 생성
 * </pre>
 */
data class ChatMessageDTO (
    val cartMemberId: Long?,
    val cartId: Long?,
    val payload: String?,
    val status: String?
)