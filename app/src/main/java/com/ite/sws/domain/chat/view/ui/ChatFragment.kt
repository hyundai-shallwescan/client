package com.ite.sws.domain.chat.view.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.ite.sws.R
import com.ite.sws.common.SharedPreferencesUtil
import com.ite.sws.common.WebSocketClient
import com.ite.sws.databinding.FragmentChatBinding
import com.ite.sws.domain.cart.view.ui.CartLoginFragment
import com.ite.sws.domain.chat.data.ChatMessageDTO
import ua.naiksoftware.stomp.dto.LifecycleEvent

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

        // JWT 토큰을 가져오는 로직 (예: SharedPreferences 등에서 가져오기)
        val jwtToken = SharedPreferencesUtil.getString(requireContext(), "jwt_token")

        if (jwtToken.isNullOrEmpty() || !isTokenValid(jwtToken)) {
            findNavController().navigate(R.id.action_chatFragment_to_cartLoginFragment)
            return
        }

        // WebSocket 연결
        WebSocketClient.connect(jwtToken) { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("STOMP", "WebSocket opened")
                    // 연결이 열리면 특정 장바구니에 구독
                    subscribeToCart(1)
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d("STOMP", "WebSocket closed")
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e("STOMP", "WebSocket error", event.exception)
                }
                else -> {
                    Log.d("STOMP", "WebSocket event: ${event.message}")
                }
            }
        }

        // 채팅방 입장 메시지 전송
        enterCart(1)

        // 메시지 전송
        binding.button2.setOnClickListener {
            val payload = binding.editTextMessage.text.toString().trim()
            if (payload.isNotEmpty()) {
                sendMessage(123, payload, "SENT", 1)
                Log.d("ChatFragment", "Button clicked and message sent: $payload")
                binding.editTextMessage.text.clear()
            }
        }
    }

    private fun isTokenValid(token: String): Boolean {

        return true
    }

    private fun navigateToLoginFragment() {
        val cartLoginFragment = CartLoginFragment()

        // Fragment 전환을 위한 Transaction 시작
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, cartLoginFragment)
            addToBackStack(null) // 뒤로 가기 버튼을 누르면 이전 프래그먼트로 돌아가게 하려면 추가합니다.
            commit() // 트랜잭션 커밋
        }
    }

    private fun subscribeToCart(cartId: Long) {
        WebSocketClient.subscribe("/sub/chat/room/$cartId") { message ->
            Log.i("STOMP", "Received: $message")
            activity?.runOnUiThread {
                binding.chatHistory.append("Received: $message\n")
            }
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

    @SuppressLint("CheckResult")
    private fun enterCart(cartId: Long) {
        val chatMessageDTO = ChatMessageDTO(cartMemberId = 0, payload = "", status = "ENTER", cartId = cartId)
        val data = Gson().toJson(chatMessageDTO)

        WebSocketClient.sendMessage("/pub/chat/enter", data, {
            Log.d("STOMP", "Entered chat room successfully")
        }, { error ->
            Log.e("STOMP", "Failed to enter chat room", error)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        WebSocketClient.disconnect()
        _binding = null
    }
}
