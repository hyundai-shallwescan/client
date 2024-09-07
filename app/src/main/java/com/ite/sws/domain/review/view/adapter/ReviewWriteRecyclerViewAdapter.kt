package com.ite.sws.domain.review.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.databinding.ItemReviewWriteBinding
import com.ite.sws.domain.review.data.GetMemberPaymentProductReviewRes

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
class ReviewWriteRecyclerViewAdapter(
    private val items: List<GetMemberPaymentProductReviewRes>,
    private val onCreateReviewClick: (GetMemberPaymentProductReviewRes) -> Unit
) : RecyclerView.Adapter<ReviewWriteRecyclerViewAdapter.ReviewWriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewWriteViewHolder {
        val binding = ItemReviewWriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewWriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewWriteViewHolder, position: Int) {
        holder.bind(items[position], onCreateReviewClick)
    }

    override fun getItemCount() = items.size

    inner class ReviewWriteViewHolder(private val binding: ItemReviewWriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetMemberPaymentProductReviewRes, onCreateReviewClick: (GetMemberPaymentProductReviewRes) -> Unit) {
            Glide.with(binding.root.context).load(item.thumbnailImage).into(binding.imgReviewThumbnail)
            binding.reviewProductName.text = item.name

            if (item.isReviewWritten == 'N') {
                binding.createReviewButton.visibility = View.VISIBLE
                binding.reviewCheckIcon.visibility = View.GONE
                binding.createReviewButton.setOnClickListener {
                    onCreateReviewClick(item)
                }
            } else {
                binding.createReviewButton.visibility = View.GONE
                binding.reviewCheckIcon.visibility = View.VISIBLE;
            }
        }
    }
}
