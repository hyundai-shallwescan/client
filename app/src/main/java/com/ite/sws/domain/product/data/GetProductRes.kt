package com.ite.sws.domain.product.data

/**
 * 상품 조회 DTO
 * @author 김민정
 * @since 2024.09.13
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.13  김민정       최초 생성
 * </pre>
 */
data class GetProductRes (
    val productId: Long,
    val price: Long,
    val name: String,
    val thumbnailImage: String,
    val descriptionImage: String,
    val barcode: String,
    val description: String,
    val isDeleted: Int,
    val createdAt: String,
    val updatedAt: String
)
