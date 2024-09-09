package com.ite.sws.domain.review.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.domain.member.data.GetMemberPaymentRes
import com.ite.sws.databinding.ItemPaymentBinding
import com.ite.sws.domain.review.view.ui.ReviewListItemRecyclerViewAdapter

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
class WriteReviewRecyclerViewAdapter(
    private val paymentList: List<GetMemberPaymentRes>,
    private val onCreateReviewClick: (GetMemberPaymentRes.GetMemberPaymentItemRes) -> Unit
) : RecyclerView.Adapter<WriteReviewRecyclerViewAdapter.PaymentViewHolder>() {

    private var expandedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val paymentItem = paymentList[position]
        holder.bind(paymentItem, position)
    }

    override fun getItemCount(): Int = paymentList.size

    inner class PaymentViewHolder(private val binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(paymentItem: GetMemberPaymentRes, position: Int) {

            binding.tvPaymentDate.text = paymentItem.createdAt


            val isExpanded = position == expandedPosition || position == 0


            binding.rvPaymentItems.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.dividerTop.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.dividerBottom.visibility = if (isExpanded) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                expandedPosition = if (isExpanded) RecyclerView.NO_POSITION else position
                notifyItemChanged(position)
            }


            setupDetailRecyclerView(paymentItem.items)
        }

        private fun setupDetailRecyclerView(items: List<GetMemberPaymentRes.GetMemberPaymentItemRes>) {
            val detailAdapter = ReviewListItemRecyclerViewAdapter(items, onCreateReviewClick)
            binding.rvPaymentItems.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvPaymentItems.adapter = detailAdapter
        }
    }
}
