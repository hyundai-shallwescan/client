package com.ite.sws.domain.product.api.service

import com.ite.sws.domain.product.data.GetProductReviewRes
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.ite.sws.domain.product.data.GetProductRes
import retrofit2.Response

/**
 * 상품 서비스 인터페이스
 * @author 김민정
 * @since 2024.09.13
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.13  김민정       최초 생성
 * 2024.09.13  김민정       최초 생성
 * 2024.09.13  구지웅       상품 리뷰 조회 API 호출
 * 2024.09.13  김민정       상품 이름으로 조회 API 호출
</pre> *
 */
interface ProductService {

    /**
     * 상품 이름으로 조회 API
     */
    @GET("products")
    suspend fun findProductListByName(@Query("name") name: String): Response<List<GetProductRes>>
  
     @GET("products/{productId}/reviews")
    suspend fun getProductReviews(
        @Path("productId") productId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<List<GetProductReviewRes>>
}
