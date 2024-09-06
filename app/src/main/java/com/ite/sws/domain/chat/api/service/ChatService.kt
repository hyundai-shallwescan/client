package com.ite.sws.domain.chat.api.service

import com.ite.sws.domain.chat.data.ChatMessageDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 채팅 서비스 인터페이스
 * @author 남진수
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	남진수       최초 생성
 * 2024.09.06  	남진수       채팅 메시지 목록 조회 API 추가
 * </pre>
 */
interface ChatService {

    /**
     * 채팅 메시지 목록 조회
     */
    @GET("carts/{cartId}/chats")
    fun findChatMessageList(
        @Path("cartId") cartId: Long
    ): Call<List<ChatMessageDTO>>
}