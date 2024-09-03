package com.ite.sws.domain.cart.view.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
 * </pre>
 */
class ScanViewModel : ViewModel() {

    private val cartRepository = CartRepository()

    // 서버 요청 결과를 저장하는 LiveData
    private val _scanResult = MutableLiveData<Unit>()
    val scanResult: LiveData<Unit> = _scanResult

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

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
                _scanResult.postValue(Unit)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    /**
     * 장바구니 아이템 조회
     */
    fun findCartItemList(cartId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val items = cartRepository.findCartItemList(cartId)
                _cartItems.postValue(items)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

}