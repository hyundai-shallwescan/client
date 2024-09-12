package com.ite.sws.domain.chatbot.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ite.sws.MainActivity
import com.ite.sws.databinding.FragmentChatbotBinding
import com.ite.sws.domain.chatbot.data.GetChatGptRes
import com.ite.sws.domain.chatbot.view.adpater.ChatBotRecyclerViewAdapter
import com.ite.sws.domain.checklist.view.ui.CheckListViewModel
import com.ite.sws.util.hideBottomNavigation
import setupToolbar

/**
 * 젤뽀 챗봇 프래그먼트
 * @author 정은지
 * @since 2024.09.08
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자       수정내용
 * ----------  --------    ---------------------------
 * 2024.09.08   정은지       최초 생성
 * </pre>
 */
class ChatBotFragment : Fragment() {

    private var _binding: FragmentChatbotBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ChatBotRecyclerViewAdapter
    private val chatViewModel: ChatBotViewModel by viewModels()
    private val checkListModel: CheckListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)

        // 툴바 설정
        setupToolbar(binding.toolbar.toolbar, "젤뽀 챗봇", true)

        // 내비게이션 바 숨김
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        adapter = ChatBotRecyclerViewAdapter(mutableListOf(), parentFragmentManager, checkListModel)
        binding.rvChatbot.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatbot.adapter = adapter

        // 메시지 업데이트 시 UI 갱신
        chatViewModel.chatMessages.observe(viewLifecycleOwner, Observer { messages ->
            adapter.setMessages(messages)
            binding.rvChatbot.scrollToPosition(messages.size - 1) // 최신 메시지로 스크롤
        })

        // 에러 메시지
        chatViewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        })

        // Send 버튼 클릭
        binding.btnChatSend.setOnClickListener {
            val userMessageText = binding.edtMessage.text.toString()
            if (userMessageText.isNotEmpty()) {
                // 사용자 메시지 추가
                val userMessage = GetChatGptRes(message = userMessageText, isUserMessage = true)
                chatViewModel.addUserMessage(userMessage)

                // 입력 필드 초기화
                binding.edtMessage.setText("")

                // ChatGPT API 요청
                chatViewModel.fetchChatResponse(userMessageText)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
