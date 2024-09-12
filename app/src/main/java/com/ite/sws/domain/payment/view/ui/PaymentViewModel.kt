package com.ite.sws.domain.payment.view.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ite.sws.domain.cart.data.GetCartItemRes
import com.ite.sws.domain.payment.api.repository.PaymentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 상품 결제 ViewModel
 * @author 김민정
 * @since 2024.09.09
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.09   김민정       최초 생성
 * </pre>
 */
class PaymentViewModel : ViewModel() {

    private val paymentRepository = PaymentRepository()

    // 서버 요청 결과를 저장하는 LiveData
    private val _cartItems = MutableLiveData<GetCartItemRes>()
    val cartItems: LiveData<GetCartItemRes> = _cartItems

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * 장바구니 아이템 조회
     */
    fun findCartItemList(cartId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cartResponse = paymentRepository.findPaymentItemList(cartId)
                _cartItems.postValue(cartResponse)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}