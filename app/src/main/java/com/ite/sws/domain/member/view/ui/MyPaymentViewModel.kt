package com.ite.sws.domain.member.view.ui

import androidx.lifecycle.ViewModel
import com.ite.sws.domain.member.api.repository.MemberRepository
import com.ite.sws.domain.member.data.GetMemberPaymentRes
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 회원 구매 내역 ViewModel
 * @author 정은지
 * @since 2024.09.05
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.05  정은지        최초 생성
 * </pre>
 */
class MyPaymentViewModel : ViewModel() {

    private val memberRepository = MemberRepository()

    /**
     * 결제 내역 조회
     */
    fun findPaymentItemList(
        onSuccess: (List<GetMemberPaymentRes>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        memberRepository.findPaymentItemList(
            onSuccess = { items ->
                val modifiedItems = items.map { item ->
                    // 날짜 형식 변환
                    item.copy(createdAt = formatDate(item.createdAt))
                }
                onSuccess(modifiedItems)
            },
            onFailure = { errorRes ->
                val errorMessage = errorRes.message
                onFailure(errorMessage!!)
            }
        )
    }

    /**
     * 날짜 변환 함수
     */
    private fun formatDate(originalDate: String): String {
        return try {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val desiredFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val date = originalFormat.parse(originalDate)
            desiredFormat.format(date)
        } catch (e: Exception) {
            originalDate
        }
    }
}