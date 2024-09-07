package com.ite.sws.domain.checklist.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.common.data.CheckStatus
import com.ite.sws.databinding.ItemChecklistBinding
import com.ite.sws.domain.checklist.data.CheckListCategory
import com.ite.sws.domain.checklist.data.GetCheckListRes

/**
 * 체크리스트 아이템 리사이클러 어댑터
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
class CheckListRecyclerViewAdapter (
    private val items: List<GetCheckListRes>,
    private val onItemChecked: (GetCheckListRes, Boolean) -> Unit
) : RecyclerView.Adapter<CheckListRecyclerViewAdapter.CheckListViewHolder>()
    {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder {
        val binding =
            ItemChecklistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CheckListViewHolder(private val binding: ItemChecklistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetCheckListRes) {
            binding.tvItemName.text = item.itemName

            // 카테고리 아이콘 설정
            val category = CheckListCategory.fromId(item.myCheckListCategoryId)
            binding.imgIcon.setImageResource(category.iconId)

            // 체크박스 상태 설정
            binding.checkboxStatus.isChecked = item.status == CheckStatus.CHECKED
            binding.checkboxStatus.setOnCheckedChangeListener { _, isChecked ->
                onItemChecked(item, isChecked)
            }
        }
    }
}
