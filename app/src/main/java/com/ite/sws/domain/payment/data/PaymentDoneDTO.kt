package com.ite.sws.domain.payment.data

/**
 * 결제 완료 DTO
 * @author 김민정
 * @since 2024.09.11
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.11  김민정       최초 생성
 * </pre>
 */
data class PaymentDoneDTO(
    val paymentId: Long,
    val oldCartId: Long,
    val newCartId: Long,
    val cartOwnerName: String,
    val qrUrl: String
)