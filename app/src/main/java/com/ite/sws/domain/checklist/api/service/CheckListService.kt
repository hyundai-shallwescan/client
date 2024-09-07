package com.ite.sws.domain.checklist.api.service

import com.ite.sws.domain.checklist.data.GetCheckListRes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

/**
 * 체크리스트 서비스 인터페이스
 * @author 정은지
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  정은지        최초 생성
 * 2024.09.06  정은지        체크리스트 조회 API 호출
 * 2024.09.07  정은지        체크리스트 상태 변경 API 호출
 * </pre>
 */
interface CheckListService {

    /**
     * 체크리스트 조회
     */
    @GET("/my-checklist")
    fun findMyCheckList(): Call<List<GetCheckListRes>>

    /**
     * 체크리스트 상태 변경
     */
    @PATCH("/my-checklist/{myCheckListItemId}")
    fun modifyCheckListItemStatus(
        @Path("myCheckListItemId") myCheckListItemId: Long
    ): Call<Void>

}