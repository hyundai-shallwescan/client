package com.ite.sws.domain.cart.api.service

import com.ite.sws.domain.cart.data.GetCartItemRes
import com.ite.sws.domain.cart.data.PostCartLoginReq
import com.ite.sws.domain.cart.data.PutCartItemReq
import com.ite.sws.domain.member.data.JwtToken
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 장바구니 서비스 인터페이스
 * @author 남진수
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  남진수       최초 생성
 * 2024.08.31  김민정       장바구니 아이템 추가 API 호출
 * 2024.09.02  김민정       장바구니 아이템 조회 API 호출
 * 2024.09.03  김민정       장바구니 아이템 수량 변경 API 호출
 * </pre>
 */
interface CartService {

    @POST("carts/login")
    fun cartLogin(@Body request: PostCartLoginReq
    ): Call<JwtToken>

    /**
     * 장바구니 아이템 추가 API
     */
    @PUT("carts")
    suspend fun saveCartItem(@Body request: PutCartItemReq)

    /**
     * 장바구니 아이템 조회 API
     */
    @GET("carts/{cartId}")
    suspend fun findCartItemList(@Path("cartId") cartId: Long): GetCartItemRes

    /**
     * 장바구니 아이템 수량 변경 API
     */
    @PATCH("carts/{cartId}/products/{productId}")
    suspend fun modifyCartItemQuantity(
        @Path("cartId") cartId: Long,
        @Path("productId") productId: Long,
        @Query("delta") delta: Int)
}