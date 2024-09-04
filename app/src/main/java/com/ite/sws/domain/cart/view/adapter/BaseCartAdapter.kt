package com.ite.sws.domain.cart.view.adapter

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.domain.cart.data.CartItem
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
 * 2024.09.02  김민정       리사이클러뷰에서 아이템 수량 변경
 * 2024.09.03  김민정       리사이클러뷰에서 아이템 삭제
 * </pre>
 */
abstract class BaseCartAdapter<VH : RecyclerView.ViewHolder, VM : BaseCartViewModel>(
    private val viewModel: VM
) : ListAdapter<CartItem, VH>(CartDiffCallback()) {

    var onViewDetail: ((CartItem) -> Unit)? = null

    /**
     * 아이템의 수량을 업데이트하고 리사이클러뷰에 반영
     */
    fun updateItemQuantity(item: CartItem, delta: Int) {
        val currentList = currentList.toMutableList()
        val position = currentList.indexOf(item)
        if (position != -1) {
            val updatedItem = item.copy(quantity = item.quantity + delta)
            currentList[position] = updatedItem
            submitList(currentList)
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
}
