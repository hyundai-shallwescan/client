package com.ite.sws.domain.sharelist.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.common.data.CheckStatus
import com.ite.sws.databinding.ItemSharelistBinding
import com.ite.sws.domain.sharelist.data.ShareListItem
import com.ite.sws.domain.sharelist.view.ui.ShareListViewModel
import com.ite.sws.util.NumberFormatterUtil.formatCurrencyWithCommas
import com.ite.sws.util.SharedPreferencesUtil

/**
 * 공유체크리스트 아이템 목록 리사이클러 어댑터
 * @author 김민정
 * @since 2024.09.12
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.12  김민정       최초 생성
 * 2024.09.12  김민정       공유체크리스트 아이템 조회
 * 2024.09.12  김민정       공유체크리스트 아이템 체크 상태 변경
 * 2024.09.12  김민정       공유체크리스트 아이템 삭제
 * </pre>
 */
class ShareListRecyclerAdapter(private val viewModel: ShareListViewModel) :
    BaseShareListAdapter<ShareListRecyclerAdapter.ShareListViewHolder, ShareListViewModel>(viewModel) {

    inner class ShareListViewHolder(val binding: ItemSharelistBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShareListItem) {
            binding.textSharelistProductName.text = item.productName
            binding.textSharelistProductPrice.text = "${formatCurrencyWithCommas(item.productPrice)}"
            binding.checkboxStatus.isChecked = item.status == CheckStatus.CHECKED

            // 썸네일 이미지 로드
            Glide.with(binding.root.context)
                .load(item.productThumbnail)
                .into(binding.imgSharelistProduct)

            // 체크 박스 클릭 리스너
            binding.checkboxStatus.setOnClickListener {
                // 서버에 체크 상태 변경 요청
                viewModel.modifyShareListCheck(
                    SharedPreferencesUtil.getCartId(),
                    item.productId
                )
            }
        }
    }

    /**
     *  ViewHolder 생성
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareListViewHolder {
        val binding = ItemSharelistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShareListViewHolder(binding)
    }

    /**
     * ViewHolder에 데이터를 바인딩
     */
    override fun onBindViewHolder(holder: ShareListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}