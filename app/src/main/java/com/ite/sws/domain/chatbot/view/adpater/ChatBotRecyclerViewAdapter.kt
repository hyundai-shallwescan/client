package com.ite.sws.domain.chatbot.view.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.databinding.ItemChatbotMessageBinding
import com.ite.sws.databinding.ItemChatbotProductBinding
import com.ite.sws.databinding.ItemChatbotProductItemBinding
import com.ite.sws.databinding.ItemChatbotUserBinding
import com.ite.sws.domain.chatbot.data.GetChatGptRes
import com.ite.sws.util.NumberFormatterUtil

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
 * </pre>
 */
class ChatBotRecyclerViewAdapter(private val messages: MutableList<GetChatGptRes>)
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
    class ProductMessageViewHolder(private val binding: ItemChatbotProductBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(products: List<GetChatGptRes.Product>) {
            // 기존 레이아웃에서 모든 뷰 제거 후 새로 추가 (중복 방지)
            binding.productLayout.removeAllViews()

            // 상품 바인딩
            products.forEach { product ->
                val productBinding = ItemChatbotProductItemBinding.inflate(LayoutInflater.from(binding.root.context), binding.productLayout, false)

                productBinding.tvProductName.text = product.title
                productBinding.tvProductPrice.text = NumberFormatterUtil.formatCurrencyWithCommas(product.price)
                Glide.with(binding.root.context).load(product.thumbnailImage).into(productBinding.imgProductThumbnail)

                binding.productLayout.addView(productBinding.root) // 상품 레이아웃을 추가
            }
        }

        // 메시지 바인딩
        fun bindMessage(message: GetChatGptRes) {
            binding.tvMessage.text = message.message
        }
    }
}