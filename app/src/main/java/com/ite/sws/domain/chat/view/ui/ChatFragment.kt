package com.ite.sws.domain.chat.view.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ite.sws.MainActivity
import com.ite.sws.common.WebSocketClient
import com.ite.sws.databinding.FragmentChatBinding
import com.ite.sws.domain.chat.api.repository.ChatRepository
import com.ite.sws.domain.chat.data.ChatMessageDTO
import com.ite.sws.domain.chat.view.adapter.ChatAdapter
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.hideBottomNavigation
import kotlinx.coroutines.launch
import setupToolbar

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
 * 2024.09.06  	남진수       메시지 불러오기 기능 추가
 * </pre>
 */
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatMessages = mutableListOf<ChatMessageDTO>()
    private lateinit var chatAdapter: ChatAdapter

    private val chatRepository = ChatRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        (activity as MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }
        setupToolbar(binding.toolbar.toolbar, "채팅", true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onclickRefreshBtn()

        val currentUserName = SharedPreferencesUtil.getCartMemberName()
        chatAdapter = ChatAdapter(chatMessages, currentUserName)

        // 채팅 메시지 전송 화면 구성
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        // 채팅 메시지 전송 버튼 클릭 이벤트
        binding.btnChatSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString()
            if (message.isNotBlank()) {
                sendMessage(message, "NORMAL")
                binding.editTextMessage.text.clear()
            }
        }
        // 채팅 메시지 불러오기
        loadChat()
        // 채팅 메시지 수신
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
            Log.d("STOMP", "채팅 전송 성공: $payload")
        }, { error ->
            Log.e("STOMP", "채팅 전송 실패: $payload", error)
        })
    }

    /**
     * 메시지 수신
     */
    private fun receiveMessage() {
        val cartId = SharedPreferencesUtil.getCartId()
        val subscriptionPath = "/sub/chat/$cartId"

        WebSocketClient.subscribe(subscriptionPath) { message ->
            Log.d("STOMP", "받은 메시지: $message")

            val chatMessageDTO = Gson().fromJson(message, ChatMessageDTO::class.java)

            activity?.runOnUiThread {
                chatMessages.add(chatMessageDTO)
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                binding.recyclerViewChat.scrollToPosition(chatMessages.size - 1)
            }
        }
    }

    /**
     * 채팅 메시지 불러오기
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun loadChat() {
        val cartId = SharedPreferencesUtil.getCartId()

        chatRepository.findChatMessageList(cartId) { messages, error ->
            if (messages != null) {
                activity?.runOnUiThread {
                    chatMessages.clear()
                    chatMessages.addAll(messages)
                    chatAdapter.notifyDataSetChanged()
                    binding.recyclerViewChat.scrollToPosition(chatMessages.size - 1)
                }
            } else {
                Log.e("ChatFragment", "채팅 메시지 로드 실패", error)
            }
        }
    }

    private fun onclickRefreshBtn(){
        binding.btnChatRefresh.setOnClickListener{
            loadChat()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        WebSocketClient.disconnect()
        _binding = null
    }
}