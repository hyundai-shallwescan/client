package com.ite.sws.domain.cart.view.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ite.sws.domain.cart.api.repository.CartRepository
import com.ite.sws.domain.cart.data.CartItem
import com.ite.sws.domain.cart.data.PutCartItemReq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 스캔앤고 ViewModel
 * @author 김민정
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  김민정       최초 생성
 * 2024.08.31  김민정       장바구니 아이템 추가
 * 2024.09.02  김민정       장바구니 아이템 조회
 * 2024.09.03  김민정       장바구니 아이템 수량 변경
 * 2024.09.03  김민정       장바구니 아이템 삭제
 * </pre>
 */
class ScanViewModel : BaseCartViewModel() {

    private val cartRepository = CartRepository()

    // 바코드 스캔 성공을 처리
    private val _barcodeScanSuccess = MutableLiveData<Boolean>()
    val barcodeScanSuccess: LiveData<Boolean> = _barcodeScanSuccess

    // 바코드 스캔 오류를 처리
    private val _barcodeScanError = MutableLiveData<String?>()
    val barcodeScanError: LiveData<String?> = _barcodeScanError

    // 장바구니 아이템 변화를 처리
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    // 오류를 처리
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * 장바구니 아이템 추가
     * - 스캔한 바코드를 서버로 전송
     */
    fun saveCartItem(barcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cartRepository.saveCartItem(PutCartItemReq(barcode))
                _barcodeScanSuccess.postValue(true)
            } catch (e: Exception) {
                _barcodeScanError.postValue(e.message)
            }
        }
    }

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

    /**
     * 장바구니 아이템 업데이트
     */
    override fun updateCartItems(newItems: List<CartItem>) {
        _cartItems.value = newItems // _cartItems는 MutableLiveData
    }
}
