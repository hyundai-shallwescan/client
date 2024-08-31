package com.ite.sws.common.data

data class ErrorRes(
    val status: Int,
    val errorCode: String,
    val message: String
)

class ErrorResponseException(val errorRes: ErrorRes) : Throwable(errorRes.message)