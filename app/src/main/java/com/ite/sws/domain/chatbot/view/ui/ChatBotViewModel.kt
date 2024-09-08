package com.ite.sws.domain.chatbot.view.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ite.sws.domain.chatbot.api.repository.ChatBotRepository
import com.ite.sws.domain.chatbot.data.GetChatGptRes
import com.ite.sws.util.SharedPreferencesUtil

/**
 * 젤뽀 챗봇 ViewModel
 * @author 정은지
 * @since 2024.09.08
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.08  정은지        최초 생성
 * </pre>
 */
class ChatBotViewModel : ViewModel() {

    private val chatBotRepository = ChatBotRepository()

    private val _chatMessages = MutableLiveData<List<GetChatGptRes>>()
    val chatMessages: LiveData<List<GetChatGptRes>> get() = _chatMessages

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val messageList = mutableListOf<GetChatGptRes>()

    init {
        addWelcomeMessage()
    }

    /**
     * 고정 메시지
     */
    private fun addWelcomeMessage() {
        val userName = SharedPreferencesUtil.getCartMemberName()
        val welcomeMessage = GetChatGptRes(
            message = "반가워요, ${userName}님!\nAI 쇼핑메이트 젤뽀예요 ❤️\n\n" +
                    "쇼핑 중 궁금하신 내용을 채팅창에 입력해보세요!",
            isUserMessage = false
        )
        // 고정 메시지를 리스트에 추가
        messageList.add(welcomeMessage)
        _chatMessages.value = messageList
    }

    /**
     * 챗봇 응답
     */
    fun fetchChatResponse(query: String) {
        chatBotRepository.getChatResponse(query,
            onSuccess = { response ->
                // 새로운 메시지를 리스트에 추가 및 LiveData 업데이트
                messageList.add(response)
                _chatMessages.value = messageList
            },
            onFailure = { errorRes ->
                // 에러 발생 시 에러 메시지를 LiveData에 업데이트
                _error.value = errorRes.message
            }
        )
    }

    /**
     * 사용자 메시지를 리스트에 추가
     */
    fun addUserMessage(userMessage: GetChatGptRes) {
        messageList.add(userMessage)
        _chatMessages.value = messageList
    }
}