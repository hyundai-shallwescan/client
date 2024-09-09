package com.ite.sws.domain.payment.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.databinding.ItemPaymentItemBinding
import com.ite.sws.domain.cart.data.CartItem
import com.ite.sws.util.NumberFormatterUtil.formatCurrencyWithCommas

/**
 * 상품 결제 목록 리사이클러 어댑터
 * @author 김민정
 * @since 2024.09.09
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.09  김민정       최초 생성
 * 2024.09.09  김민정       장바구니 아이템 조회
 * </pre>
 */
class PaymentRecyclerAdapter(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<PaymentRecyclerAdapter.PaymentRecyclerViewHolder>() {

    /**
     *  ViewHolder 생성
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentRecyclerViewHolder {
        val binding =
            ItemPaymentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentRecyclerViewHolder(binding)
    }

    /**
     * ViewHolder에 데이터를 바인딩
     */
    override fun onBindViewHolder(holder: PaymentRecyclerViewHolder, position: Int) {
        val item = cartItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class PaymentRecyclerViewHolder(val binding: ItemPaymentItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            // 썸네일 이미지 로드
            Glide.with(binding.root.context)
                .load(item.productThumbnail)
                .into(binding.imgProductThumbnail)

            binding.tvProductName.text = item.productName
            binding.tvProductQuantity.text = "${item.quantity}개"
            binding.tvProductPrice.text = "${formatCurrencyWithCommas(item.productPrice*item.quantity)}"
        }
    }
}