package com.ite.sws.domain.payment.api.repository

import com.google.gson.Gson
import com.ite.sws.common.RetrofitClient
import com.ite.sws.common.data.ErrorRes
import com.ite.sws.domain.cart.data.GetCartItemRes
import com.ite.sws.domain.payment.api.service.PaymentService
import com.ite.sws.domain.payment.data.GetRecommendRes
import com.ite.sws.domain.payment.data.PostPaymentReq
import retrofit2.Response

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
 * 2024.09.08   김민정       공통 응답 처리 함수
 * 2024.09.08   김민정       공통 네트워크 예외 처리 함수
 * 2024.09.10   김민정       추가 결제 상품 추천
 * </pre>
 */
class PaymentRepository {

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

    /**
     * 공통 응답 처리 함수
     */
    private fun <T> handleResponse(response: Response<T>): T? {
        return if (response.isSuccessful) {
            response.body()
        } else {
            throw Exception(response.errorBody()?.string())
        }
    }

    /**
     * 공통 네트워크 예외 처리 함수
     */
    private fun handleNetworkException(e: Exception): Exception {
        return try {
            // 에러 메시지가 JSON 형식일 경우 ErrorRes로 파싱
            val errorRes = Gson().fromJson(e.message, ErrorRes::class.java)
            Exception(errorRes.message)
        } catch (jsonEx: Exception) {
            // JSON 파싱 실패 시, 일반 네트워크 에러로 처리
            Exception("Network error: ${e.localizedMessage}")
        }
    }
}