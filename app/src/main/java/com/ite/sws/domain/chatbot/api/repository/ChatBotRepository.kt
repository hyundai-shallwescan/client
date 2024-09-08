package com.ite.sws.domain.chatbot.api.repository

import android.util.Log
import com.google.gson.Gson
import com.ite.sws.common.RetrofitClient
import com.ite.sws.common.data.ErrorRes
import com.ite.sws.domain.chatbot.api.service.ChatBotService
import com.ite.sws.domain.chatbot.data.GetChatGptRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 챗봇 Repository
 * @author 정은지
 * @since 2024.09.08
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.08   정은지        최초생성
 * </pre>
 */
class ChatBotRepository {

    private val chatBotService = RetrofitClient.instance.create(ChatBotService::class.java)

    /**
     * 챗봇 응답 조회
     */
    fun getChatResponse(
        query: String,
        onSuccess: (GetChatGptRes) -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        chatBotService.getChatResponse(query).enqueue(object : Callback<GetChatGptRes> {
            override fun onResponse(call: Call<GetChatGptRes>, response: Response<GetChatGptRes>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("ChatBotRepository", response.body().toString())
                        onSuccess(it)
                    }
                } else {
                    val errorRes = Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<GetChatGptRes>, t: Throwable) {
                handleFailure(call, t, onFailure)
            }
        })
    }

    /**
     * 공통 처리 함수
     */
    private fun <T> handleFailure(call: Call<T>, t: Throwable, onFailure: (ErrorRes) -> Unit) {
        val networkError = ErrorRes(
            status = 0,
            errorCode = "NETWORK_ERROR",
            message = t.localizedMessage ?: "Unknown network error"
        )
        onFailure(networkError)
    }
}