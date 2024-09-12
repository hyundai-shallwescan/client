package com.ite.sws.domain.sharelist.api.service

import com.ite.sws.domain.sharelist.data.GetShareListRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 공유 체크 리스트 서비스 인터페이스
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  김민정       최초 생성
 * 2024.09.12  김민정       공유체크리스트 조회 API 호출
 * </pre>
 */
interface ShareListService {

    /**
     * 공유체크리스트 조회 API
     */
    @GET("share-checklist/{cartId}")
    suspend fun findShareList(@Path("cartId") cartId: Long): Response<GetShareListRes>
}