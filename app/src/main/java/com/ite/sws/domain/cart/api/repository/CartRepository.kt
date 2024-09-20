package com.ite.sws.domain.cart.api.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.ite.sws.common.BaseRepository
import com.ite.sws.common.RetrofitClient
import com.ite.sws.common.data.ErrorRes
import com.ite.sws.domain.cart.api.service.CartService
import com.ite.sws.domain.cart.data.CartItem
import com.ite.sws.domain.cart.data.PostCartLoginReq
import com.ite.sws.domain.cart.data.PutCartItemReq
import com.ite.sws.domain.member.data.JwtToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 장바구니 Repository
 * @author 남진수
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  남진수       최초 생성
 * 2024.08.31  김민정       장바구니 아이템 추가
 * 2024.09.02  김민정       장바구니 아이템 조회
 * 2024.09.03  김민정       장바구니 아이템 수량 변경
 * 2024.09.03  김민정       장바구니 아이템 삭제
 * 2024.09.10  남진수       FCM 토큰 발급 추가
 * </pre>
 */
class CartRepository : BaseRepository() {

    private val cartService =
        RetrofitClient.instance.create(CartService::class.java)

    /**
     * 로그인
     */
    fun login(
        postCartLoginReq: PostCartLoginReq,
        onResult: (Result<JwtToken>) -> Unit
    ) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onResult(Result.failure(Throwable("Fetching FCM registration token failed")))
                return@addOnCompleteListener
            }
            // 새로운 FCM Token 발급
            val fcmToken = task.result
            val call = cartService.cartLogin(fcmToken, postCartLoginReq)
            call.enqueue(object : Callback<JwtToken> {
                override fun onResponse(call: Call<JwtToken>, response: Response<JwtToken>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onResult(Result.success(it))
                        }
                    } else {
                        val errorBodyString = response.errorBody()?.string()
                        val errorRes = Gson().fromJson(errorBodyString, ErrorRes::class.java)
                        onResult(Result.failure(Throwable(errorRes.message)))
                    }
                }

                override fun onFailure(call: Call<JwtToken>, t: Throwable) {
                    onResult(Result.failure(t))
                }
            })
        }
    }

    /**
     * 장바구니 아이템 추가
     */
    suspend fun saveCartItem(request: PutCartItemReq) {
        try {
            val response = cartService.saveCartItem(request)
            handleResponse(response)
        } catch (e: Exception) {
            Log.d("CART ITEM", e.message.toString())
            TODO("등록되지 않은 상품 토스트 메시지 처리 throw handleNetworkException(e)")
        }
    }

    /**
     * 장바구니 아이템 조회
     */
    suspend fun findCartItemList(cartId: Long): List<CartItem> {
        return try {
            val response = cartService.findCartItemList(cartId)
            handleResponse(response)?.items ?: emptyList()
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }

    /**
     * 장바구니 아이템 수량 변경
     */
    suspend fun modifyCartItemQuantity(cartId: Long, productId: Long, delta: Int) {
        try {
            val response = cartService.modifyCartItemQuantity(cartId, productId, delta)
            handleResponse(response)
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }

    /**
     * 장바구니 아이템 삭제
     */
    suspend fun removeCartItem(cartId: Long, productId: Long) {
        try {
            val response = cartService.deleteCartItem(cartId, productId)
            handleResponse(response)
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }
}