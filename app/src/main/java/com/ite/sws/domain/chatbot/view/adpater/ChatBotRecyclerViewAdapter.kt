package com.ite.sws.domain.chatbot.view.adpater

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.databinding.ItemChatbotMessageBinding
import com.ite.sws.databinding.ItemChatbotProductBinding
import com.ite.sws.databinding.ItemChatbotUserBinding
import com.ite.sws.domain.chatbot.data.GetChatGptRes
import com.ite.sws.domain.checklist.view.ui.CheckListViewModel

/**
 * 젤뽀 챗봇 채팅 리사이클러 어댑터
 * @author 정은지
 * @since 2024.09.08
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.08  정은지        최초 생성
 * 2024.09.09  정은지        상품 메세지에서 체크리스트 추가
 * </pre>
 */
class ChatBotRecyclerViewAdapter(
    private val messages: MutableList<GetChatGptRes>,
    private val fragmentManager: FragmentManager,
    private val viewModel: CheckListViewModel,
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_GPT = 2
    private val VIEW_TYPE_PRODUCT = 3

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.isUserMessage) VIEW_TYPE_USER else if (message.product.isNullOrEmpty()) VIEW_TYPE_GPT else VIEW_TYPE_PRODUCT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding = ItemChatbotUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UserMessageViewHolder(binding)
            }
            VIEW_TYPE_PRODUCT -> {
                val binding = ItemChatbotProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProductMessageViewHolder(binding)
            }
            else -> {
                val binding = ItemChatbotMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GptMessageViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is GptMessageViewHolder -> holder.bind(message)
            is ProductMessageViewHolder -> {
                // 메시지 먼저 바인딩
                holder.bindMessage(message)
                // 상품 리스트 바인딩
                message.product?.let { holder.bind(it) }
            }
        }
    }

    fun setMessages(newMessages: List<GetChatGptRes>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    // 사용자 메세지 ViewHolder
    class UserMessageViewHolder(private val binding: ItemChatbotUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: GetChatGptRes) {
            binding.tvUserMessage.text = message.message
        }
    }

    // ChatGPT 메세지 ViewHolder
    class GptMessageViewHolder(private val binding: ItemChatbotMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: GetChatGptRes) {
            binding.tvMessage.text = message.message
        }
    }

    // 상품 메세지 ViewHolder
    inner class ProductMessageViewHolder(
        private val binding: ItemChatbotProductBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(products: List<GetChatGptRes.Product>) {
            binding.rvProduct.removeAllViews()

            binding.rvProduct.layoutManager = LinearLayoutManager(binding.root.context)

            val productAdapter = ChatBotProductRecyclerViewAdapter(products, fragmentManager, viewModel)
            binding.rvProduct.adapter = productAdapter
        }

        // 메시지 바인딩
        fun bindMessage(message: GetChatGptRes) {
            binding.tvMessage.text = message.message
        }
    }
}