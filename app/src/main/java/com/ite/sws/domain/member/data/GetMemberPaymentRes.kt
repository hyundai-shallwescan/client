package com.ite.sws.domain.member.data


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 회원 구매 내역 조회 Response DTO
 * @author 정은지
 * @since 2024.09.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.05  	정은지        최초 생성
 * </pre>
 */
@Parcelize
data class GetMemberPaymentRes(
    val paymentId: Long,
    val createdAt: String,
    val amount: Int,
    val items: List<GetMemberPaymentItemRes>
) : Parcelable {
    @Parcelize
    data class GetMemberPaymentItemRes(
        val paymentItemId: Long,
        val productId: Long,
        val name: String,
        val price: Int,
        val quantity: Int,
        val thumbnailImage: String,
        val isReviewWritten: Char
    ) : Parcelable
}