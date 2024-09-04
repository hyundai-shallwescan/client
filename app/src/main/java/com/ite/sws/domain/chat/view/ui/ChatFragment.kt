package com.ite.sws.domain.chat.view.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.ite.sws.MainActivity
import com.ite.sws.common.WebSocketClient
import com.ite.sws.databinding.FragmentChatBinding
import com.ite.sws.domain.chat.data.ChatMessageDTO
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.hideBottomNavigation

/**
 * 채팅 프래그먼트
 * @author 남진수
 * @since 2024.08.30
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.30  	남진수       최초 생성
 * 2024.08.30  	남진수       WebSocket 연결, 메시지 전송, 구독 기능 확인
 * 2024.09.03  	남진수       메시지 수신 기능 추가
 * </pre>
 */
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        (activity as MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button2.setOnClickListener {
            val message = binding.editTextMessage.text.toString()
            if (message.isNotBlank()) {
                Log.d("STOMP", "Message to send: $message")
                sendMessage(message, "NORMAL")

                binding.editTextMessage.text.clear()
                Log.d("STOMP", "Message sent: $message")
            } else {
                Log.d("STOMP", "Message is empty, not sending.")
            }
        }
        receiveMessage()
    }

    /**
     * 메시지 전송
     */
    @SuppressLint("CheckResult")
    private fun sendMessage(payload: String, status: String) {
        val cartId = SharedPreferencesUtil.getCartId()
        val name = SharedPreferencesUtil.getCartMemberName()
        val chatMessageDTO = ChatMessageDTO(payload = payload, status = status, cartId = cartId, name = name)
        val data = Gson().toJson(chatMessageDTO)

        WebSocketClient.sendMessage("/pub/chat/message", data, {
            Log.d("STOMP", "Message sent successfully: $payload")
        }, { error ->
            Log.e("STOMP", "Failed to send message: $payload", error)
        })
    }

    /**
     * 메시지 수신
     */
    private fun receiveMessage() {
        val cartId = SharedPreferencesUtil.getCartId()
        val subscriptionPath = "/sub/chat/$cartId"

        WebSocketClient.subscribe(subscriptionPath) { message ->
            Log.d("STOMP", "Received message: $message")

            val chatMessageDTO = Gson().fromJson(message, ChatMessageDTO::class.java)

            activity?.runOnUiThread {
                addMessageToChatHistory("User: ${chatMessageDTO.payload}")
            }
        }
    }

    /**
     * 채팅 내역에 메시지 추가
     */
    private fun addMessageToChatHistory(message: String) {
        val currentHistory = binding.chatHistory.text.toString()
        val updatedHistory = "$currentHistory\n$message"

        binding.chatHistory.text = updatedHistory

        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        WebSocketClient.disconnect()
        _binding = null
    }
}
