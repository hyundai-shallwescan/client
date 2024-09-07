package com.ite.sws.domain.member.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.R
import com.ite.sws.databinding.ItemPaymentBinding
import com.ite.sws.domain.member.data.GetMemberPaymentRes
import com.ite.sws.util.NumberFormatterUtil.formatCurrencyWithCommas

/**
 * 회원 구매 내역 리사이클러 어댑터
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
class MyPaymentRecyclerViewAdapter(
    private val paymentList: List<GetMemberPaymentRes>
) : RecyclerView.Adapter<MyPaymentRecyclerViewAdapter.PaymentViewHolder>() {

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
            binding.tvTotalAmount.text = formatCurrencyWithCommas(paymentItem.amount)

            // 최신 내역은 확장된 상태로 처리
            val isExpanded = if (position == 0) {
                true
            } else {
                position == expandedPosition
            }

            // 아이템, 총 구매 금액
            binding.rvPaymentItems.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.containerTotalAmount.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // 구분선
            binding.dividerTop.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.dividerBottom.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // 아이콘 변경
            binding.imgExpandCollapse.setImageResource(
                if (isExpanded) R.drawable.ic_arrow_top else R.drawable.ic_arrow_bottom
            )

            // 아이템 클릭 시 확장/축소 처리
            binding.root.setOnClickListener {
                expandedPosition = if (isExpanded) RecyclerView.NO_POSITION else position
                notifyItemChanged(position) // 이 아이템만 업데이트하여 성능 최적화
            }

            // 세부 RecyclerView를 위한 어댑터 설정
            setupDetailRecyclerView(paymentItem.items)
        }

        private fun setupDetailRecyclerView(items: List<GetMemberPaymentRes.GetMemberPaymentItemRes>) {
            val detailAdapter = MyPaymentItemRecyclerViewAdapter(items)
            binding.rvPaymentItems.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvPaymentItems.adapter = detailAdapter
        }
    }
}
