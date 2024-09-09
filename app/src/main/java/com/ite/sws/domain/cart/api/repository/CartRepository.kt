package com.ite.sws.domain.cart.api.repository

import com.google.gson.Gson
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
 * 2024.09.08  김민정       공통 응답 처리 함수
 * 2024.09.08  김민정       공통 네트워크 예외 처리 함수
 * </pre>
 */
class CartRepository {

    private val cartService =
        RetrofitClient.instance.create(CartService::class.java)

    /**
     * 로그인
     */
    fun login(
        postCartLoginReq: PostCartLoginReq,
        onSuccess: (JwtToken) -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        val call = cartService.cartLogin(postCartLoginReq)
        call.enqueue(object : Callback<JwtToken> {
            override fun onResponse(call: Call<JwtToken>, response: Response<JwtToken>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onSuccess(it)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    val errorRes = Gson().fromJson(errorBodyString, ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<JwtToken>, t: Throwable) {
                t.printStackTrace()
                val networkError = ErrorRes(
                    status = 0,
                    errorCode = "NETWORK_ERROR",
                    message = t.localizedMessage ?: "Unknown network error"
                )
                onFailure(networkError)
            }
        })
    }

    /**
     * 장바구니 아이템 추가
     */
    suspend fun saveCartItem(request: PutCartItemReq) {
        try {
            val response = cartService.saveCartItem(request)
            handleResponse(response)
        } catch (e: Exception) {
            throw handleNetworkException(e)
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

    /**
     * 공통 응답 처리 함수
     */
    private fun <T> handleResponse(response: Response<T>): T? {
        return if (response.isSuccessful) {
            response.body()
        } else {
            throw Exception("Error: ${response.errorBody()?.string()}")
        }
    }

    /**
     * 공통 네트워크 예외 처리 함수
     */
    private fun handleNetworkException(e: Exception): Exception {
        return Exception("Network error: ${e.localizedMessage}")
    }
}