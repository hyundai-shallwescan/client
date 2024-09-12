package com.ite.sws.domain.sharelist.data

import com.ite.sws.common.data.CheckStatus

/**
 * 공유체크리스트 아이템 DTO
 * @author 김민정
 * @since 2024.09.12
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.12  김민정       최초 생성
 * </pre>
 */
data class ShareListItem(
    val productId: Long,
    val productName: String,
    val productPrice: Int,
    val productThumbnail: String,
    val status: CheckStatus
)