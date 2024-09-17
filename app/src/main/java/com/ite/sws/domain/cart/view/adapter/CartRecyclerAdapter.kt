package com.ite.sws.domain.cart.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.databinding.ItemCartBinding
import com.ite.sws.domain.cart.data.CartItem
import com.ite.sws.domain.cart.view.ui.ScanViewModel
import com.ite.sws.util.NumberFormatterUtil.formatCurrencyWithCommas
import com.ite.sws.util.SharedPreferencesUtil

/**
 * 장바구니 아이템 목록 리사이클러 어댑터
 * @author 김민정
 * @since 2024.09.02
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02  김민정       최초 생성
 * 2024.09.02  김민정       장바구니 아이템 조회
 * 2024.09.03  김민정       장바구니 아이템 수량 변경
 * 2024.09.03  김민정       장바구니 아이템 삭제
 * 2024.09.17  김민정       상품 상세 페이지 이동
 * </pre>
 */
class CartRecyclerAdapter(private val viewModel: ScanViewModel) :
    BaseCartAdapter<CartRecyclerAdapter.CartViewHolder, ScanViewModel>(viewModel) {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.textCartProductName.text = item.productName
            binding.textCartProductPrice.text = "${formatCurrencyWithCommas(item.productPrice)}"
            binding.textCartCount.text = item.quantity.toString()

            // 썸네일 이미지 로드
            Glide.with(binding.root.context)
                .load(item.productThumbnail)
                .into(binding.imgCartProduct)

            // 수량 증가 버튼 클릭 리스너
            binding.btnCartPlus.setOnClickListener {
                // 서버에 수량 증가 요청
                viewModel.modifyCartItemQuantity(
                    SharedPreferencesUtil.getCartId(),
                    item.productId,
                    1
                )
            }

            // 수량 감소 버튼 클릭 리스너
            binding.btnCartMinus.setOnClickListener {
                // 수량이 1 이상일 때만 감소 가능
                if (item.quantity > 1) {
                    // 서버에 수량 감소 요청
                    viewModel.modifyCartItemQuantity(
                        SharedPreferencesUtil.getCartId(),
                        item.productId,
                        -1
                    )
                }
            }

            // 아이템 터치 리싀너
            binding.root.setOnClickListener {
                onViewDetail?.invoke(item)
            }
        }
    }

    /**
     *  ViewHolder 생성
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    /**
     * ViewHolder에 데이터를 바인딩
     */
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
