package com.ite.sws.domain.cart.api.service

import com.ite.sws.domain.cart.data.PostCartLoginReq
import com.ite.sws.domain.member.data.JwtToken
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 장바구니 서비스
 * @author 남진수
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	남진수       최초 생성
 * </pre>
 */
interface CartService {

    @POST("carts/login")
    fun cartLogin(@Body request: PostCartLoginReq
    ): Call<JwtToken>
}