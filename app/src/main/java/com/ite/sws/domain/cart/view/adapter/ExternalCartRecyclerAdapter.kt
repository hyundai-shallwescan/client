package com.ite.sws.domain.cart.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.databinding.ItemCartBinding
import com.ite.sws.domain.cart.data.CartItem
import com.ite.sws.domain.cart.view.ui.ExternalCartViewModel
import com.ite.sws.util.SharedPreferencesUtil

/**
 * 외부일행 장바구니 아이템 목록 리사이클러 어댑터
 * @author 김민정
 * @since 2024.09.02
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.04  김민정       최초 생성
 * 2024.09.04  김민정       장바구니 아이템 조회
 * 2024.09.04  김민정       장바구니 아이템 수량 변경
 * 2024.09.04  김민정       장바구니 아이템 삭제
 * </pre>
 */
class ExternalCartRecyclerAdapter(private val viewModel: ExternalCartViewModel) :
    BaseCartAdapter<ExternalCartRecyclerAdapter.ExternalCartViewHolder, ExternalCartViewModel>(viewModel) {

    inner class ExternalCartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.textCartProductName.text = item.productName
            binding.textCartProductPrice.text = "${item.productPrice}원"
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
                // 어댑터에서 수량 증가
                updateItemQuantity(item, 1)
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
                    // 어댑터에서 수량 감소
                    updateItemQuantity(item, -1)
                }
            }

            binding.btnCartDetail.setOnClickListener {
                onViewDetail?.invoke(item)
            }
        }
    }

    /**
     *  ViewHolder 생성
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExternalCartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExternalCartViewHolder(binding)
    }

    /**
     * ViewHolder에 데이터를 바인딩
     */
    override fun onBindViewHolder(holder: ExternalCartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
