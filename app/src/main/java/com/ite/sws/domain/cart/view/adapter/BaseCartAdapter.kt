package com.ite.sws.domain.cart.view.adapter

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.domain.cart.data.CartItem
import com.ite.sws.domain.cart.data.CartItemDetail
import com.ite.sws.domain.cart.view.ui.BaseCartViewModel

/**
 * 장바구니 리사이클러 어댑터 추상 클래스
 * @author 김민정
 * @since 2024.09.02
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02  김민정       최초 생성
 * 2024.09.05  김민정       리사이클러뷰 아이템 추가
 * 2024.09.05  김민정       리사이클러뷰 아이템 수량 증가
 * 2024.09.05  김민정       리사이클러뷰 아이템 수량 감소
 * 2024.09.05  김민정       리사이클러뷰 아이템 제거
 * </pre>
 */
abstract class BaseCartAdapter<ViewHolder : RecyclerView.ViewHolder, ViewModel : BaseCartViewModel>(
    private val viewModel: ViewModel
) : ListAdapter<CartItem, ViewHolder>(CartDiffCallback()) {

    var onViewDetail: ((CartItem) -> Unit)? = null

    /**
     * 리사이클러뷰에 아이템 추가
     */
    fun addNewItem(cartItem: CartItemDetail) {
        val currentList = currentList.toMutableList()
        val newItem = CartItem(
            productId = cartItem.productId,
            productName = cartItem.productName,
            productPrice = cartItem.productPrice,
            productThumbnail = cartItem.productThumbnail,
            quantity = cartItem.quantity
        )
        currentList.add(0, newItem) // 리스트 맨 상단에 추가

        // 리사이클러뷰가 비어있을 때만 뷰모델 업데이트
        if (currentList.size == 1) {
            viewModel.updateCartItems(currentList)
        }
        submitList(currentList) // RecyclerView 업데이트
    }

    /**
     * 리사이클러뷰에서 아이템 수량 증가
     */
    fun increaseItemQuantity(cartItem: CartItemDetail) {
        val currentList = currentList.toMutableList()
        val item = currentList.find { it.productId == cartItem.productId }
        item?.let {
            val updatedItem = it.copy(quantity = it.quantity + 1)
            currentList[currentList.indexOf(it)] = updatedItem
            submitList(currentList)
        }
    }

    /**
     * 리사이클러뷰에서 아이템 수량 감소
     */
    fun decreaseItemQuantity(cartItem: CartItemDetail) {
        val currentList = currentList.toMutableList()
        val item = currentList.find { it.productId == cartItem.productId }
        item?.let {
            if (it.quantity > 1) {
                val updatedItem = it.copy(quantity = it.quantity - 1)
                currentList[currentList.indexOf(it)] = updatedItem
                submitList(currentList)
            }
        }
    }

    /**
     * 리사이클러뷰에서 아이템 제거
     */
    fun removeItem(cartItem: CartItemDetail) {
        val currentList = currentList.toMutableList()
        val item = currentList.find { it.productId == cartItem.productId }
        item?.let {
            currentList.remove(it)
            submitList(currentList)
        }
    }
}
