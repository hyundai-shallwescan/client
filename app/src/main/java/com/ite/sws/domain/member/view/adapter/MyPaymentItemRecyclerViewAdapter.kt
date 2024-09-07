package com.ite.sws.domain.member.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.databinding.ItemPaymentItemBinding
import com.ite.sws.domain.member.data.GetMemberPaymentRes
import com.ite.sws.util.NumberFormatterUtil.formatCurrencyWithCommas

/**
 * 회원 구매 내역 아이템 리사이클러 어댑터
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
class MyPaymentItemRecyclerViewAdapter(
    private val items: List<GetMemberPaymentRes.GetMemberPaymentItemRes>
) : RecyclerView.Adapter<MyPaymentItemRecyclerViewAdapter.PaymentDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentDetailViewHolder {
        val binding = ItemPaymentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentDetailViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class PaymentDetailViewHolder(private val binding: ItemPaymentItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetMemberPaymentRes.GetMemberPaymentItemRes) {
            binding.tvProductName.text = item.name
            binding.tvProductQuantity.text = "${item.quantity}개"
            binding.tvProductPrice.text = formatCurrencyWithCommas(item.price)

            // 썸네일 이미지 로드
            Glide.with(binding.root.context)
                .load(item.thumbnailImage)
                .into(binding.imgProductThumbnail)
        }
    }
}