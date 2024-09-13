package com.ite.sws.domain.product.api.repository

import com.ite.sws.common.RetrofitClient
import com.ite.sws.domain.product.api.service.ProductService
import com.ite.sws.domain.product.data.GetProductRes
import retrofit2.Response

/**
 * 상품 Repository
 * @author 김민정
 * @since 2024.09.13
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.13  김민정       최초 생성
 * 2024.09.13  김민정       상품 이름으로 조회
 * 2024.09.13  김민정       공통 응답 처리 함수
 * 2024.09.13  김민정       공통 네트워크 예외 처리 함수
</pre> *
 */
class ProductRepository {

    private val productService =
        RetrofitClient.instance.create(ProductService::class.java)

    /**
     * 상품 이름으로 조회
     */
    suspend fun findProductListByName(name: String): List<GetProductRes> {
        return try {
            val response = productService.findProductListByName(name)
            handleResponse(response) ?: emptyList()
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