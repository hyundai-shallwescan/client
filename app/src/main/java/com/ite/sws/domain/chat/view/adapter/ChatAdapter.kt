package com.ite.sws.domain.chat.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.R
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
 * </pre>
 */
class ChatAdapter(private val messages: List<ChatMessageDTO>, private val currentUserName: String?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    /*
     * RecyclerView 표시할 아이템의 개수
     */
    override fun getItemCount(): Int = messages.size

    /**
     * 보낸 메시지와 받은 메시지를 구분하기 위한 뷰타입 (상대면 왼쪽, 본인이면 오른쪽에 표시)
     */
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.name == currentUserName) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
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
}
