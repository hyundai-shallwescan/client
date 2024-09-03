package com.ite.sws.domain.cart.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.databinding.ItemCartBinding
import com.ite.sws.domain.cart.data.CartItem
import com.ite.sws.domain.cart.view.ui.ScanViewModel

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
 * </pre>
 */
class CartRecyclerAdapter(private val viewModel: ScanViewModel) :
    ListAdapter<CartItem, CartRecyclerAdapter.CartViewHolder>(CartDiffCallback()) {

    var onViewDetail: ((CartItem) -> Unit)? = null

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.textCartProductName.text = item.productName
            binding.textCartProductPrice.text = "${item.productPrice}원"
            binding.textCartCount.text = item.quantity.toString()

            // 썸네일 이미지 로드
            Glide.with(binding.root.context)
                .load(item.productThumbnail)
                .into(binding.imgCartProduct)

            // 버튼 클릭 리스너 설정
            binding.btnCartPlus.setOnClickListener {
                viewModel.modifyCartItemQuantity(item.productId, 1)
            }

            binding.btnCartMinus.setOnClickListener {
                viewModel.modifyCartItemQuantity(item.productId, -1)
            }

            binding.btnCartDetail.setOnClickListener {
                onViewDetail?.invoke(item)
            }
        }
    }

    /**
     * 리사이클러뷰에서 해당 포지션 아이템 삭제
     */
    fun removeItem(position: Int) {
        val currentList = currentList.toMutableList()
        currentList.removeAt(position)
        submitList(currentList)
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

/**
 * DiffUtil.Callback 클래스
 */
class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    /**
     * 두 아이템이 같은지 비교
     */
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.productId == newItem.productId
    }

    /**
     * 두 아이템의 내용이 같은지 비교
     */
    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}
