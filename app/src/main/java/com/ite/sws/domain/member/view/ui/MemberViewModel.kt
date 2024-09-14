package com.ite.sws.domain.member.view.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ite.sws.domain.member.api.repository.MemberRepository
import com.ite.sws.domain.member.api.repository.ReviewPagingSource
import com.ite.sws.domain.member.data.GetMemberPaymentRes
import com.ite.sws.domain.member.data.GetMemberReviewRes
import com.ite.sws.domain.review.api.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 회원 ViewModel
 * @author 정은지
 * @since 2024.09.05
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.05  정은지        최초 생성
 * 2024.09.05  정은지        결제 내역 조회
 * 2024.09.06  정은지        작성 리뷰 조회
 * </pre>
 */
class MemberViewModel : ViewModel() {

    private val memberRepository = MemberRepository()
    private val reviewRepository = ReviewRepository()

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
     * 작성 리뷰 조회
     */
    fun getPagedMemberReviews(): Flow<PagingData<GetMemberReviewRes>> {
        return Pager(
            config = PagingConfig(
                pageSize = 6,  // 페이지당 항목 개수
                enablePlaceholders = false  // 항목 자리 표시 비활성화
            ),
            pagingSourceFactory = { ReviewPagingSource(memberRepository) }
        ).flow.cachedIn(viewModelScope)  // ViewModel 범위 내에서 데이터 캐시
    }

    /**
     * 리뷰 삭제
     */
    fun deleteReview(reviewId: Long, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit
    ) {
        reviewRepository.deleteReview(
            reviewId = reviewId,
            onSuccess = {
                onSuccess()
            },
            onFailure = { error ->
                onFailure(error)
            }
        )
    }

    /**
     * 날짜 변환 함수
     */
    private fun formatDate(originalDate: String): String {
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