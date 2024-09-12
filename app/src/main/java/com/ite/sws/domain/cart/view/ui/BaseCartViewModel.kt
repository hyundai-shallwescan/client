package com.ite.sws.domain.cart.view.ui

import androidx.lifecycle.ViewModel
import com.ite.sws.domain.cart.data.CartItem

/**
 * 장바구니 ViewModel 추상클래스
 * @author 김민정
 * @since 2024.09.04
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.04  김민정       최초 생성
 * </pre>
 */
abstract class BaseCartViewModel : ViewModel() {
    abstract fun modifyCartItemQuantity(cartId: Long, productId: Long, delta: Int)
    abstract fun removeCartItem(cartId: Long, productId: Long)
    abstract fun findCartItemList(cartId: Long)
    abstract fun updateCartItems(newItems: List<CartItem>)
}