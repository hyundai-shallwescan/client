package com.ite.sws.domain.chat.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.ite.sws.R
import com.ite.sws.domain.chat.data.CartMessagePayload
import com.ite.sws.domain.chat.data.ChatMessageDTO

/**
 * 채팅 어댑터
 * @author 남진수
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	남진수       최초 생성
 * 2024.09.06  	남진수       채팅 메시지 어댑터 구현
 * 2024.09.12  	남진수       뷰타입에 따른 뷰홀더 생성(장바구니, 요청리스트)
 * </pre>
 */
class ChatAdapter(private val messages: List<ChatMessageDTO>, private val currentUserName: String?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT_NORMAL = 1
    private val VIEW_TYPE_RECEIVED_NORMAL = 2
    private val VIEW_TYPE_SENT_CART = 3
    private val VIEW_TYPE_RECEIVED_CART = 4
    private val VIEW_TYPE_SENT_CHECK = 5
    private val VIEW_TYPE_RECEIVED_CHECK = 6

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT_NORMAL -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_sent, parent, false)
                SentMessageViewHolder(view)
            }
            VIEW_TYPE_RECEIVED_NORMAL -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_received, parent, false)
                ReceivedMessageViewHolder(view)
            }
            VIEW_TYPE_SENT_CART, VIEW_TYPE_SENT_CHECK -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_cart_check_sent, parent, false)
                if (viewType == VIEW_TYPE_SENT_CART) SentCartMessageViewHolder(view) else SentCheckMessageViewHolder(view)
            }
            VIEW_TYPE_RECEIVED_CART, VIEW_TYPE_RECEIVED_CHECK -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_cart_check_received, parent, false)
                if (viewType == VIEW_TYPE_RECEIVED_CART) ReceivedCartMessageViewHolder(view) else ReceivedCheckMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("ERROR")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
            is SentCartMessageViewHolder -> holder.bind(message)
            is ReceivedCartMessageViewHolder -> holder.bind(message)
            is SentCheckMessageViewHolder -> holder.bind(message)
            is ReceivedCheckMessageViewHolder -> holder.bind(message)
        }
    }

    /*
     * RecyclerView 표시할 아이템의 개수
     */
    override fun getItemCount(): Int = messages.size

    /**
     * 뷰타입을 구분하여 뷰홀더를 생성
     */
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when (message.status) {
            "NORMAL" -> if (message.name == currentUserName) VIEW_TYPE_SENT_NORMAL else VIEW_TYPE_RECEIVED_NORMAL
            "CART" -> if (message.name == currentUserName) VIEW_TYPE_SENT_CART else VIEW_TYPE_RECEIVED_CART
            "CHECK" -> if (message.name == currentUserName) VIEW_TYPE_SENT_CHECK else VIEW_TYPE_RECEIVED_CHECK
            else -> VIEW_TYPE_RECEIVED_NORMAL
        }
    }

    /**
     * 보낸 메시지 뷰홀더
     */
    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textMessageSent)

        fun bind(message: ChatMessageDTO) {
            messageText.text = message.payload
        }
    }

    /**
     * 받은 메시지 뷰홀더
     */
    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.textName)
        private val messageText: TextView = itemView.findViewById(R.id.textMessageReceived)

        fun bind(message: ChatMessageDTO) {
            nameText.text = message.name
            messageText.text = message.payload
        }
    }

    /**
     * 보낸 장바구니 메시지 뷰홀더
     */
    class SentCartMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textMessageSent)
        private val productThumbnail: ImageView = itemView.findViewById(R.id.productThumbnail)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val quantityText: TextView = itemView.findViewById(R.id.quantity)

        fun bind(message: ChatMessageDTO) {
            val cartPayload = Gson().fromJson(message.payload, CartMessagePayload::class.java)
            messageText.text = when (cartPayload.action) {
                "create" -> "장바구니에 상품이 추가되었습니다.\n${cartPayload.productName}"
                "delete" -> "장바구니의 상품이 삭제되었습니다.\n${cartPayload.productName}"
                "increase", "decrease" -> "장바구니 상품의 수량이 변경되었습니다.\n${cartPayload.productName} (${cartPayload.quantity}개)"
                else -> "장바구니에 변동사항이 있습니다."
            }
            productName.text = cartPayload.productName
            quantityText.text = cartPayload.quantity?.toString() ?: ""

            // Glide를 사용하여 이미지 로드
            Glide.with(itemView.context)
                .load(cartPayload.productThumbnail)
//                .placeholder(R.drawable.placeholder_image) // 이미지 로드 전 보여줄 플레이스홀더
//                .error(R.drawable.error_image) // 이미지 로드 실패 시 보여줄 이미지
                .into(productThumbnail)
        }
    }

    /**
     * 받은 장바구니 메시지 뷰홀더
     */
    class ReceivedCartMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.textName)
        private val messageText: TextView = itemView.findViewById(R.id.textMessageReceived)
        private val productThumbnail: ImageView = itemView.findViewById(R.id.productThumbnail)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val quantityText: TextView = itemView.findViewById(R.id.quantity)

        fun bind(message: ChatMessageDTO) {
            nameText.text = message.name
            val cartPayload = Gson().fromJson(message.payload, CartMessagePayload::class.java)
            messageText.text = when (cartPayload.action) {
                "create" -> "장바구니에 상품이 추가되었습니다."
                "delete" -> "장바구니의 상품이 삭제되었습니다."
                "increase", "decrease" -> "장바구니 상품의 수량이 변경되었습니다."
                else -> "장바군니에 변동사항이 있습니다."
            }
            productName.text = cartPayload.productName
            quantityText.text = cartPayload.quantity?.toString() ?: ""
            // Glide를 사용하여 이미지 로드
            Glide.with(itemView.context)
                .load(cartPayload.productThumbnail)
//                .placeholder(R.drawable.placeholder_image) // 이미지 로드 전 보여줄 플레이스홀더
//                .error(R.drawable.error_image) // 이미지 로드 실패 시 보여줄 이미지
                .into(productThumbnail)
        }
    }

    /**
     * 보낸 요청 리스트 뷰홀더
     */
    class SentCheckMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textMessageSent)
        private val productThumbnail: ImageView = itemView.findViewById(R.id.productThumbnail)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val quantityText: TextView = itemView.findViewById(R.id.quantity)

        fun bind(message: ChatMessageDTO) {
            val checkPayload = Gson().fromJson(message.payload, CartMessagePayload::class.java)
            messageText.text = when (checkPayload.action) {
                "create" -> "요청리스트에 상품이 추가되었습니다.\n${checkPayload.productName}"
                "delete" -> "요청리스트의 상품이 삭제되었습니다.\n${checkPayload.productName}"
                "increase", "decrease" -> "요청리스트 상품의 수량이 변경되었습니다.\n${checkPayload.productName} (${checkPayload.quantity}개)"
                else -> "요청리스트에 변동사항이 있습니다."
            }
            productName.text = checkPayload.productName
            quantityText.text = checkPayload.quantity?.toString() ?: ""

            // Glide를 사용하여 이미지 로드
            Glide.with(itemView.context)
                .load(checkPayload.productThumbnail)
//                .placeholder(R.drawable.placeholder_image) // 이미지 로드 전 보여줄 플레이스홀더
//                .error(R.drawable.error_image) // 이미지 로드 실패 시 보여줄 이미지
                .into(productThumbnail)
        }
    }

    /**
     * 받은 요청 리스트 뷰홀더
     */
    class ReceivedCheckMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.textName)
        private val messageText: TextView = itemView.findViewById(R.id.textMessageReceived)
        private val productThumbnail: ImageView = itemView.findViewById(R.id.productThumbnail)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val quantityText: TextView = itemView.findViewById(R.id.quantity)

        fun bind(message: ChatMessageDTO) {
            nameText.text = message.name
            val checkPayload = Gson().fromJson(message.payload, CartMessagePayload::class.java)
            messageText.text = when (checkPayload.action) {
                "create" -> "요청리스트에 상품이 추가되었습니다."
                "delete" -> "요청리스트의 상품이 삭제되었습니다."
                "increase", "decrease" -> "요청리스트 상품의 수량이 변경되었습니다."
                else -> "요청리스트에 변동사항이 있습니다."
            }
            productName.text = checkPayload.productName
            quantityText.text = checkPayload.quantity?.toString() ?: ""

            // Glide를 사용하여 이미지 로드
            Glide.with(itemView.context)
                .load(checkPayload.productThumbnail)
//                .placeholder(R.drawable.placeholder_image) // 이미지 로드 전 보여줄 플레이스홀더
//                .error(R.drawable.error_image) // 이미지 로드 실패 시 보여줄 이미지
                .into(productThumbnail)
        }
    }
}
