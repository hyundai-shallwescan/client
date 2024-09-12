package com.ite.sws.domain.payment.data

/**
 * 결제 아이템 DTO
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
data class PaymentItemDto(
    val productId: Long,
    val quantity: Int
)
