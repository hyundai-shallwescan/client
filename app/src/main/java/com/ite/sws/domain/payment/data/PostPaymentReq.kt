package com.ite.sws.domain.payment.data


/**
 * 상품 등록 Request DTO
 * @author 김민정
 * @since 2024.09.10
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.10  김민정       최초 생성
 * </pre>
 */
data class PostPaymentReq (
    val cartId: Long,
    val totalPrice: Int,
    val card: String,
    val paymentKey: String,
    val paymentTime: String,
    val items: List<PaymentItemDto>
)
