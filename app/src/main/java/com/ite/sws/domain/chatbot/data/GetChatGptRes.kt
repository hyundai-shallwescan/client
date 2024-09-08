package com.ite.sws.domain.chatbot.data

/**
 * ChatGPT Response DTO
 * @author 정은지
 * @since 2024.09.08
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.08  	정은지        최초 생성
 * </pre>
 */
data class GetChatGptRes(
    val message: String,
    val isUserMessage: Boolean,
    val product: List<Product>? = null
) {
    data class Product(
        val id: Long,
        val title: String,
        val price: Int,
        val thumbnailImage: String
    )
}