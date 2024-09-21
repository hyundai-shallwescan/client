package com.ite.sws.domain.review.view.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.databinding.ItemReviewWriteBinding
import com.ite.sws.domain.member.data.GetMemberPaymentRes


/**
 * 리뷰 리스트 리사이클러뷰 어댑터
 * @author 구지웅
 * @since 2024.09.06
 * @version 1.0
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	구지웅       최초 생성
 * </pre>
 */
class ReviewListItemRecyclerViewAdapter(
    private val items: List<GetMemberPaymentRes.GetMemberPaymentItemRes>,
    private val onCreateReviewClick: (GetMemberPaymentRes.GetMemberPaymentItemRes) -> Unit 
) : RecyclerView.Adapter<ReviewListItemRecyclerViewAdapter.ReviewDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewDetailViewHolder {
        val binding = ItemReviewWriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewDetailViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ReviewDetailViewHolder(private val binding: ItemReviewWriteBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetMemberPaymentRes.GetMemberPaymentItemRes) {
            Glide.with(binding.root.context)
                .load(item.thumbnailImage)
                .into(binding.imgReviewThumbnail)

            binding.reviewProductName.text = item.name

            if (item.isReviewWritten == 'N') {
                binding.createReviewButton.visibility = View.VISIBLE
                binding.reviewCheckIcon.visibility = View.GONE

                binding.createReviewButton.setOnClickListener {
                    onCreateReviewClick(item)
                }
            } else {
                binding.createReviewButton.visibility = View.GONE
                binding.reviewCheckIcon.visibility = View.VISIBLE
            }
        }
    }
}
