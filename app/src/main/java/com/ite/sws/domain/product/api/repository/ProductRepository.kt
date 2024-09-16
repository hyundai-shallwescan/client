package com.ite.sws.domain.product.api.repository

import com.ite.sws.common.BaseRepository
import com.ite.sws.common.RetrofitClient
import com.ite.sws.domain.product.api.service.ProductService
import com.ite.sws.domain.product.data.GetProductReviewRes
import com.ite.sws.domain.product.data.GetProductRes

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
</pre> *
 */
class ProductRepository : BaseRepository() {

    private val productService =
        RetrofitClient.instance.create(ProductService::class.java)


  suspend fun getProductReviews(productId: Long, page: Int, size: Int): List<GetProductReviewRes>? {
        return try {
            val response = productService.getProductReviews(productId, page, size)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
        
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
}
