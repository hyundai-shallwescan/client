package com.ite.sws.domain.chat.data

/**
 * 채팅 메시지 페이로드
 * @author 남진수
 * @since 2024.09.12
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.12  	남진수       최초 생성
 * </pre>
 */
data class CartMessagePayload(
    val action: String,
    val productName: String,
    val productThumbnail: String,
    val quantity: Int?
)
