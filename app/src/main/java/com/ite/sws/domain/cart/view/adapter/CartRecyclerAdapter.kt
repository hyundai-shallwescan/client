package com.ite.sws.domain.cart.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.databinding.ItemCartBinding
import com.ite.sws.domain.cart.data.CartItem

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
 * </pre>
 */
class CartRecyclerAdapter :
    ListAdapter<CartItem, CartRecyclerAdapter.CartViewHolder>(CartDiffCallback()) {

    var onIncreaseQuantity: ((CartItem) -> Unit)? = null
    var onDecreaseQuantity: ((CartItem) -> Unit)? = null
    var onViewDetail: ((CartItem) -> Unit)? = null

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {

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
                onIncreaseQuantity?.invoke(item)
            }

            binding.btnCartMinus.setOnClickListener {
                onDecreaseQuantity?.invoke(item)
            }

            binding.btnCartDetail.setOnClickListener {
                onViewDetail?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}
