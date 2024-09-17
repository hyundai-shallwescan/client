package com.ite.sws.common

import com.google.gson.Gson
import com.ite.sws.common.data.ErrorRes
import retrofit2.Response

/**
 * 기본 Repository
 * : API 통신에서 발생하는 응답 및 예외 처리
 *
 * @author 김민정
 * @since 2024.09.08
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.08   김민정       최초 생성
 * 2024.09.08   김민정       공통 응답 처리 함수
 * 2024.09.08   김민정       공통 네트워크 예외 처리 함수
 * </pre>
 */
open class BaseRepository {

    /**
     * 공통 응답 처리 함수
     */
    protected fun <T> handleResponse(response: Response<T>): T? {
        return if (response.isSuccessful) {
            response.body()
        } else {
            throw Exception(response.errorBody()?.string())
        }
    }

    /**
     * 공통 네트워크 예외 처리 함수
     */
    protected fun handleNetworkException(e: Exception): Exception {
        return try {
            // 에러 메시지가 JSON 형식일 경우 ErrorRes로 파싱
            val errorRes = Gson().fromJson(e.message, ErrorRes::class.java)
            Exception(errorRes.message)
        } catch (jsonEx: Exception) {
            // JSON 파싱 실패 시, 일반 네트워크 에러로 처리
            Exception("Network error: ${e.localizedMessage}")
        }
    }
}