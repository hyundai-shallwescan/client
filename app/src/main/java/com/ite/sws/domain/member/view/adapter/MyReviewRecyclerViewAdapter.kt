package com.ite.sws.domain.member.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.ite.sws.databinding.ItemReviewBinding
import com.ite.sws.domain.member.data.GetMemberReviewRes
import com.ite.sws.domain.member.view.ui.MemberViewModel

/**
 * 작성 리뷰 조회 리사이클러 어댑터
 * @author 정은지
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일         수정자       수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06   정은지       최초 생성
 * 2024.09.14   정은지       리뷰 삭제 기능 추가
 * 2024.09.14   정은지       리뷰 상세 조회 기능 추가
 * </pre>
 */
class MyReviewRecyclerViewAdapter(
    private val viewModel: MemberViewModel,
    private val onItemClick: (GetMemberReviewRes) -> Unit
) : PagingDataAdapter<GetMemberReviewRes, MyReviewRecyclerViewAdapter.ReviewViewHolder>(ReviewDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = getItem(position)
        if (review != null) {
            holder.bind(review)
        }
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding)
        : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(review: GetMemberReviewRes) {
            Glide.with(binding.root.context).load(review.thumbnailImage)
                .into(binding.imgReviewThumbnail)

            // 이미지 클릭 시 상세 페이지로 이동
            binding.imgReviewThumbnail.setOnClickListener {
                onItemClick(review) // 클릭된 리뷰 데이터를 전달
            }

            // 삭제 버튼 클릭 리스너 설정
            binding.btnDelete.setOnClickListener {
                deleteReview(review.shortFormId, bindingAdapterPosition) // 리뷰 삭제 호출
            }
        }
    }

    private fun deleteReview(reviewId: Long, position: Int) {
        viewModel.deleteReview(reviewId,
            onSuccess = {
                this.refresh() // UI 새로고침
            },
            onFailure = { error ->
                // 실패 시 오류 처리
                error.printStackTrace()
            }
        )
    }

    /**
     * 리사이클러뷰 항목이 업데이트될 때 차이를 비교
     */
    companion object {
        private val ReviewDiffCallback = object : DiffUtil.ItemCallback<GetMemberReviewRes>() {
            override fun areItemsTheSame(oldItem: GetMemberReviewRes, newItem: GetMemberReviewRes): Boolean =
                oldItem.shortFormId == newItem.shortFormId

            override fun areContentsTheSame(oldItem: GetMemberReviewRes, newItem: GetMemberReviewRes): Boolean =
                oldItem == newItem
        }
    }
}
