package com.ite.sws.common.data

/**
 * 체크 상태 ENUM
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
enum class CheckStatus(val value: Int) {
    CHECKED(1),
    UNCHECKED(0);

    companion object {
        fun fromStatus(status: String): CheckStatus {
            return when (status) {
                "CHECKED" -> CHECKED
                "UNCHECKED" -> UNCHECKED
                else -> UNCHECKED
            }
        }

        fun toStatus(checkStatus: CheckStatus): String {
            return when (checkStatus) {
                CHECKED -> "CHECKED"
                UNCHECKED -> "UNCHECKED"
            }
        }
    }

    fun toIntValue(): Int {
        return value
    }
}