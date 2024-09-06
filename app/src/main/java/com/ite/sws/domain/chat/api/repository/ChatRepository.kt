package com.ite.sws.domain.chat.api.repository

import com.ite.sws.common.RetrofitClient
import com.ite.sws.domain.chat.api.service.ChatService
import com.ite.sws.domain.chat.data.ChatMessageDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 채팅 레포지토리
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
class ChatRepository{

    private val cartService = RetrofitClient.instance.create(ChatService::class.java)

    /**
     * 채팅 메시지 목록 조회
     */
    fun findChatMessageList(cartId: Long, callback: (List<ChatMessageDTO>?, Throwable?) -> Unit) {
        cartService.findChatMessageList(cartId).enqueue(object : Callback<List<ChatMessageDTO>> {
            override fun onResponse(call: Call<List<ChatMessageDTO>>, response: Response<List<ChatMessageDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, Throwable("Error: ${response.code()} ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<List<ChatMessageDTO>>, t: Throwable) {
                callback(null, t)
            }
        })
    }
}
