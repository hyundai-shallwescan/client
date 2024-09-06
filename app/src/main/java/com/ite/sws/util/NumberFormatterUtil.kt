package com.ite.sws.util

import java.text.DecimalFormat

/**
 * 숫자와 금액 포맷팅
 * @author 김민정
 * @since 2024.09.05
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.05  김민정       최초 생성
 * 2024.09.05  김민정       숫자 천 단위로 쉼표 구분
 * 2024.09.05  김민정       금액에 '원' 단위 붙이기
 * </pre>
 */
object NumberFormatterUtil {

    // 숫자를 천 단위로 쉼표로 구분하여 반환
    fun formatWithComma(number: Int): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(number)
    }

    // 금액을 천 단위로 쉼표로 구분하고 "원" 단위를 붙여 반환
    fun formatCurrencyWithCommas(amount: Int): String {
        return "${formatWithComma(amount)}원"
    }
}