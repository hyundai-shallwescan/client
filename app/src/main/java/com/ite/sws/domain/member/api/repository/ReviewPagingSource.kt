package com.ite.sws.domain.member.api.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ite.sws.domain.member.data.GetMemberReviewRes


/**
 * 리뷰 작성 목록 페이징 처리 클래스
 * @author 정은지
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06   정은지        최초 생성
 * </pre>
 */
class ReviewPagingSource(
    private val memberRepository: MemberRepository
) : PagingSource<Int, GetMemberReviewRes>() {

    /**
     * 페이지 데이터 로드
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GetMemberReviewRes> {
        return try {
            // 현재 페이지
            val page = params.key ?: 0
            // 로드할 항목 수
            val size = params.loadSize

            Log.d("ReviewPagingSource", "페이지: $page 사이즈: $size")

            val reviews = memberRepository.findReviewList(page, size)

            LoadResult.Page(
                data = reviews,
                prevKey = if (page == 0) null else page - 1, // 이전 페이지가 없으면 null
                nextKey = if (reviews.isEmpty()) null else page + 1 // 다음 페이지가 없으면 null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /**
     * 새로고침 시 사용할 키 반환
     */
    override fun getRefreshKey(state: PagingState<Int, GetMemberReviewRes>): Int? {
        // 현재 목록에서 앵커 위치를 기준으로 새로고침 키 반환
        return state.anchorPosition
    }
}