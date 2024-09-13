package com.ite.sws.domain.product.api.service

import com.ite.sws.domain.product.data.GetProductRes
import retrofit2.http.GET
import retrofit2.http.Query
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
 * 2024.09.13  김민정       상품 이름으로 조회 API 호출
</pre> *
 */
interface ProductService {

    /**
     * 상품 이름으로 조회 API
     */
    @GET("prodcuts")
    suspend fun findProductListByName(@Query("name") name: String): Response<List<GetProductRes>>
}