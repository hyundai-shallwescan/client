package com.ite.sws.domain.payment.data

/**
 * 주차 정산 결제 요청 DTO
 * @author 남진수
 * @since 2024.09.18
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.18  남진수       최초 생성
 * </pre>
 */
data class PostParkingPaymentsReq(
    val parkingHistoryId: Long,
    val paymentId: Long,
    val amount: Long,
    val paymentKey: String,
    val paymentCard: String
)
