package com.ite.sws.domain.sharelist.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.ite.sws.domain.sharelist.data.ShareListItem

/**
 * DiffUtil.Callback 클래스
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
class ShareListDiffCallback : DiffUtil.ItemCallback<ShareListItem>(){

    /**
     * 두 아이템이 같은지 비교
     */
    override fun areItemsTheSame(oldItem: ShareListItem, newItem: ShareListItem): Boolean {
        return oldItem.productId == newItem.productId
    }

    /**
     * 두 아이템의 내용이 같은지 비교
     */
    override fun areContentsTheSame(oldItem: ShareListItem, newItem: ShareListItem): Boolean {
        return oldItem == newItem
    }
}