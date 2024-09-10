package com.ite.sws.domain.payment.view.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ite.sws.domain.cart.data.GetCartItemRes
import com.ite.sws.domain.payment.api.repository.PaymentRepository
import com.ite.sws.domain.payment.data.GetRecommendRes
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

    private val _recommendation = MutableLiveData<GetRecommendRes>()
    val recommendation: LiveData<GetRecommendRes> = _recommendation

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * 장바구니 아이템 조회
     */
    fun findPaymentItemList(cartId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cartResponse = paymentRepository.findPaymentItemList(cartId)
                _cartItems.postValue(cartResponse)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    /**
     * 결제 추천 요청
     */
    fun findRecommendProduct(cartId: Long, totalPrice: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = paymentRepository.findRecommendProduct(cartId, totalPrice)
                response?.let {
                    _recommendation.postValue(it)
                } ?: run {
                    _error.postValue("추천 상품을 찾을 수 없습니다.")  // null 처리
                }
            } catch (e: Exception) {
                _error.postValue(e.message)  // 에러 처리
            }
        }
    }
}