package com.ite.sws.domain.payment.api.service

import com.ite.sws.domain.cart.data.GetCartItemRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 상품 결제 서비스 인터페이스
 * @author 김민정
 * @since 2024.09.09
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.09   김민정       최초 생성
 * 2024.09.09   김민정       상품 결제 아이템 조회 API 호출
 * </pre>
 */
interface PaymentService {

    /**
     * 상품 결제 아이템 조회 API
     */
    @GET("carts/{cartId}")
    suspend fun findPaymentItemList(@Path("cartId") cartId: Long): Response<GetCartItemRes>
}