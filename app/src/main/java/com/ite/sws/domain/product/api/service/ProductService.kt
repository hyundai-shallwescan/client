package com.ite.sws.domain.product.api.service

import com.ite.sws.domain.product.data.GetProductReviewRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
    @GET("products/{productId}/reviews")
    suspend fun getProductReviews(
        @Path("productId") productId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<List<GetProductReviewRes>>
}
