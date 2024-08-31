package com.ite.sws.common.data

/**
 * API 에러 응답 모델
 * @author 정은지
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	정은지       최초 생성
 * </pre>
 */

data class ErrorRes(
    val status: Int,
    val errorCode: String,
    val message: String
)