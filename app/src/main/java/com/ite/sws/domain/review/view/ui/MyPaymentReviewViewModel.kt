package com.ite.sws.domain.review.view.ui

import androidx.lifecycle.ViewModel
import com.ite.sws.domain.member.api.repository.MemberRepository
import com.ite.sws.domain.member.data.GetMemberPaymentRes
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 리뷰 작성 프래그먼트
 * @author 구지웅
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	구지웅       최초 생성
 * </pre>
 */
class MyPaymentReviewViewModel : ViewModel() {

    private val memberRepository = MemberRepository()

    fun findPaymentItemList(
        onSuccess: (List<GetMemberPaymentRes>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        memberRepository.findPaymentItemList(
            onSuccess = { items ->
                val modifiedItems = items.map { item ->
                    item.copy(createdAt = getTimeWithoutSecond(item.createdAt))
                }
                onSuccess(modifiedItems)
            },
            onFailure = { errorRes ->
                val errorMessage = errorRes.message
                onFailure(errorMessage!!)
            }
        )
    }

    private fun getTimeWithoutSecond(originalDate: String): String {
        return try {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val desiredFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
            val date = originalFormat.parse(originalDate)
            desiredFormat.format(date)
        } catch (e: Exception) {
            originalDate
        }
    }


}