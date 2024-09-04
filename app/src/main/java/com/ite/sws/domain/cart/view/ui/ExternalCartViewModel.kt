package com.ite.sws.domain.cart.view.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ite.sws.domain.cart.api.repository.CartRepository
import com.ite.sws.domain.cart.data.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 외부일행 장바구니 ViewModel
 * @author 김민정
 * @since 2024.09.04
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
class ExternalCartViewModel : BaseCartViewModel()  {

    private val cartRepository = CartRepository()

    // 서버 요청 결과를 저장하는 LiveData
    private val _scanResult = MutableLiveData<Unit>()
    val scanResult: LiveData<Unit> = _scanResult

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * 장바구니 아이템 조회
     */
    override fun findCartItemList(cartId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val items = cartRepository.findCartItemList(cartId)
                _cartItems.postValue(items)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    /**
     * 장바구니 아이템 수량 조절
     */
    override fun modifyCartItemQuantity(cartId: Long, productId: Long, delta: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cartRepository.modifyCartItemQuantity(cartId, productId, delta)
                _scanResult.postValue(Unit)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    /**
     * 장바구니 아이템 삭제
     */
    override fun removeCartItem(cartId: Long, productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cartRepository.removeCartItem(cartId, productId)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}
