package com.ite.sws.domain.chat.view.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.ite.sws.databinding.FragmentChatBinding
import com.ite.sws.domain.chat.data.ChatMessageDTO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
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

    // WebSocket 관련 변수들
    private lateinit var stompClient: StompClient
    private lateinit var stompConnection: Disposable
    private lateinit var topic: Disposable

    // WebSocket 설정
    private val url = "ws://10.0.2.2:8080/ws"
    private val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        stompClient.withClientHeartbeat(1000).withServerHeartbeat(1000)

        // WebSocket 연결
        connectStomp()

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

    // WebSocket 연결
    private fun connectStomp() {
        stompConnection = stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event ->
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
        stompClient.connect()
    }

    // 특정 장바구니에 구독
    private fun subscribeToCart(cartId: Long) {
        topic = stompClient.topic("/sub/chat/room/$cartId")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                Log.i("STOMP", "Received: ${message.payload}")
                activity?.runOnUiThread {
                    binding.chatHistory.append("Received: ${message.payload}\n")
                }
            }
    }

    // 메시지 전송
    @SuppressLint("CheckResult")
    private fun sendMessage(cartMemberId: Long, payload: String, status: String, cartId: Long) {
        val chatMessageDTO = ChatMessageDTO(cartMemberId = cartMemberId, payload = payload, status = status, cartId = cartId)
        val data = gson.toJson(chatMessageDTO)

        stompClient.send("/pub/chat/message", data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("STOMP", "Message sent successfully: $payload")
            }, { error ->
                Log.e("STOMP", "Failed to send message: $payload", error)
            })
    }

    // 장바구니 입장 메시지 전송
    @SuppressLint("CheckResult")
    private fun enterCart(cartId: Long) {
        val chatMessageDTO = ChatMessageDTO(cartMemberId = 0, payload = "", status = "ENTER", cartId = cartId)
        val data = gson.toJson(chatMessageDTO)

        stompClient.send("/pub/chat/enter", data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("STOMP", "Entered chat room successfully")
            }, { error ->
                Log.e("STOMP", "Failed to enter chat room", error)
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::topic.isInitialized) topic.dispose()
        if (::stompConnection.isInitialized) stompConnection.dispose()
        stompClient.disconnect()
        _binding = null
    }
}
