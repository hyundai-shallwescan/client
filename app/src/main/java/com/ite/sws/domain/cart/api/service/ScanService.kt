package com.ite.sws.domain.cart.api.service

import com.ite.sws.domain.cart.data.GetCartItemRes
import com.ite.sws.domain.cart.data.PutCartItemReq
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * 스캔앤고 서비스 인터페이스
 * @author 김민정
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  김민정       최초 생성
 * 2024.08.31  김민정       장바구니 아이템 추가 API 호출
 * 2024.09.02  김민정       장바구니 아이템 조회 API 호출
 * </pre>
 */
interface ScanService {

    /**
     * 장바구니 아이템 추가 API 호출
     */
    @PUT("/carts")
    suspend fun saveCartItem(@Body request: PutCartItemReq)

    /**
     * 장바구니 아이템 조회 API 호출
     */
    @GET("carts/{cartId}")
    suspend fun findCartItemList(@Path("cartId") cartId: Int): GetCartItemRes
}