package com.ite.sws.domain.cart.data

/**
 * 장바구니 아이템 DTO
 * @author 김민정
 * @since 2024.09.02
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02  김민정       최초 생성
 * </pre>
 */
data class CartItem(
    val productId: Long,
    val quantity: Int,
    val productName: String,
    val productPrice: Int,
    val productThumbnail: String
)