package com.ite.sws.domain.chatbot.api.service

import com.ite.sws.domain.chatbot.data.GetChatGptRes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 챗봇 서비스 인터페이스
 * @author 정은지
 * @since 2024.09.08
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.08   정은지        최초 생성
 * </pre>
 */
interface ChatBotService {

    @GET("/chatbot")
    fun getChatResponse(
        @Query("query") query: String
    ): Call<GetChatGptRes>
}