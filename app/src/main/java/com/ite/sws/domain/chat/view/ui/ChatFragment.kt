package com.ite.sws.domain.chat.view.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.ite.sws.common.WebSocketClient
import com.ite.sws.databinding.FragmentChatBinding
import com.ite.sws.domain.chat.data.ChatMessageDTO

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
 * </pre>
 */
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private var cartMemberId: Long = -1L
    private var cartId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button2.setOnClickListener {
            val message = binding.chatHistory.text.toString()
            sendMessage(cartMemberId, message, "NORMAL", cartId)
        }
    }

    @SuppressLint("CheckResult")
    private fun sendMessage(cartMemberId: Long, payload: String, status: String, cartId: Long) {
        val chatMessageDTO = ChatMessageDTO(cartMemberId = cartMemberId, payload = payload, status = status, cartId = cartId)
        val data = Gson().toJson(chatMessageDTO)

        WebSocketClient.sendMessage("/pub/chat/message", data, {
            Log.d("STOMP", "Message sent successfully: $payload")
        }, { error ->
            Log.e("STOMP", "Failed to send message: $payload", error)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        WebSocketClient.disconnect()
        _binding = null
    }
}
