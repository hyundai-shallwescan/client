package com.ite.sws.domain.sharelist.view.adapter

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.domain.sharelist.data.ShareListItem
import com.ite.sws.domain.sharelist.data.ShareListMessageDTO
import com.ite.sws.domain.sharelist.view.ui.BaseShareListViewModel

/**
 * 공유체크리스트 리사이클러 어댑터 추상 클래스
 * @author 김민정
 * @since 2024.09.12
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.12  김민정       최초 생성
 * 2024.09.12  김민정       리사이클러뷰 아이템 추가
 * 2024.09.12  김민정       리사이클러뷰 아이템 체크 상태 변경
 * 2024.09.12  김민정       리사이클러뷰 아이템 제거
 * </pre>
 */
abstract class BaseShareListAdapter <ViewHolder : RecyclerView.ViewHolder, ViewModel : BaseShareListViewModel>(
    private val viewModel: ViewModel
) : ListAdapter<ShareListItem, ViewHolder>(ShareListDiffCallback()) {

    /**
     * 리사이클러뷰에서 아이템 제거
     */
    fun removeItem(message: ShareListMessageDTO) {
        val currentList = currentList.toMutableList()
        val item = currentList.find { it.productId == message.productId }
        item?.let {
            currentList.remove(it)
            submitList(currentList)

            if (currentList.size == 0) {
                viewModel.updateShareListItems(currentList)
            }
        }
    }
}