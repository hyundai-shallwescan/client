package com.ite.sws.domain.checklist.data

import com.ite.sws.R

/**
 * 체크리스트 카테고리 ENUM
 * @author 정은지
 * @since 2024.09.07
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.07  	정은지        최초 생성
 * </pre>
 */
enum class CheckListCategory(val id: Long, val categoryName: String, val iconId: Int) {
    FRUIT(1, "과일", R.drawable.ic_fruit);

    companion object {
        /**
         * 카테고리 ID로 enum을 찾는 함수
         * @param id 카테고리 ID
         * @return CheckListCategory enum 값
         */
        // TODO 기본 값 바꾸기
        fun fromId(id: Long): CheckListCategory {
            return values().firstOrNull { it.id == id } ?: FRUIT
        }
    }
}