package com.ite.sws.domain.payment.api.repository

import com.ite.sws.common.BaseRepository
import com.ite.sws.common.RetrofitClient
import com.ite.sws.domain.cart.data.GetCartItemRes
import com.ite.sws.domain.payment.api.service.PaymentService
import com.ite.sws.domain.payment.data.GetRecommendRes
import com.ite.sws.domain.payment.data.PostPaymentReq

/**
 * 상품 결제 Repository
 * @author 김민정
 * @since 2024.09.09
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.09   김민정       최초 생성
 * 2024.09.09   김민정       결제를 위한 장바구니 아이템 조회
 * 2024.09.10   김민정       추가 결제 상품 추천
 * </pre>
 */
class PaymentRepository : BaseRepository() {

    private val paymentService =
        RetrofitClient.instance.create(PaymentService::class.java)

    /**
     * 결제를 위한 장바구니 아이템 조회
     */
    suspend fun findPaymentItemList(cartId: Long): GetCartItemRes {
        return try {
            val response = paymentService.findPaymentItemList(cartId)
            handleResponse(response) ?: throw Exception("Empty response")
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }

    /**
     * 추가 결제 상품 추천
     */
    suspend fun findRecommendProduct(cartId: Long, totalPrice: Int): GetRecommendRes? {
        return try {
            handleResponse(paymentService.findRecommendProduct(cartId, totalPrice))
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }

    /**
     * 결제 등록
     */
    suspend fun savePayment(postPaymentReq: PostPaymentReq): Void? {
        return try {
            handleResponse(paymentService.savePayment(postPaymentReq))
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }
}