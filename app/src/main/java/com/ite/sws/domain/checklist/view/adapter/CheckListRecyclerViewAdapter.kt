package com.ite.sws.domain.checklist.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.common.data.CheckStatus
import com.ite.sws.databinding.ItemChecklistBinding
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
    private var items: MutableList<GetCheckListRes>,
    private val onItemChecked: (GetCheckListRes, Boolean) -> Unit,
    private val onItemEdited: (GetCheckListRes, String) -> Unit,
    private val onItemRemoved: (Long) -> Unit
) : RecyclerView.Adapter<CheckListRecyclerViewAdapter.CheckListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder {
        val binding =
            ItemChecklistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    /**
     * 새로운 아이템 목록을 설정하고 리사이클러뷰 갱신
     */
    fun setItems(newItems: MutableList<GetCheckListRes>) {
        items = newItems
        notifyDataSetChanged()
    }

    /**
     * 특정 위치에 있는 아이템의 ID를 반환
     */
    fun getItemIdAt(position: Int): Long {
        return items[position].myCheckListItemId
    }

    /**
     * 아이템 개수를 반환
     */
    override fun getItemCount(): Int = items.size

    /**
     * ViewHolder 클래스
     */
    inner class CheckListViewHolder(private val binding: ItemChecklistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetCheckListRes) {

            // 아이템명 설정
            binding.tvItemName.text = item.itemName
            binding.edtItemName.setText(item.itemName)

            binding.checkboxStatus.setOnCheckedChangeListener(null) // 기존 리스너 제거

            // 체크박스 상태 설정
            binding.checkboxStatus.isChecked = item.status == CheckStatus.CHECKED

            binding.checkboxStatus.setOnCheckedChangeListener { _, isChecked ->
                onItemChecked(item, isChecked)
            }

            // 수정 버튼 클릭 시
            binding.btnUpdate.setOnClickListener {
                binding.tvItemName.visibility = View.GONE   // TextView 숨김
                binding.edtItemName.visibility = View.VISIBLE  // EditText 표시
                binding.edtItemName.requestFocus()  // EditText에 포커스 설정

                // 키보드 노출
                val imm = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edtItemName, InputMethodManager.SHOW_IMPLICIT)
            }

            binding.edtItemName.apply {
                // EditText가 포커스를 잃을 시
                setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        handleEditCompleted(item)
                    }
                }

                // 엔터키가 눌렸을 시
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        handleEditCompleted(item)
                        true
                    } else {
                        false
                    }
                }
            }
        }

        /**
         * 항목 수정 완료시 호출되는 메서드
         */
        private fun handleEditCompleted(item: GetCheckListRes) {
            val newText = binding.edtItemName.text.toString()
            binding.tvItemName.text = newText  // TextView 수정
            binding.tvItemName.visibility = View.VISIBLE  // TextView 표시
            binding.edtItemName.visibility = View.GONE  // EditText 숨김

            // 키보드 숨김
            val imm = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.edtItemName.windowToken, 0)

            onItemEdited(item, newText) // 수정 내용 전달
        }
    }

    /**
     * 아이템 삭제
     */
    fun removeItem(checkListId: Long) {
        val position = items.indexOfFirst { it.myCheckListItemId == checkListId }
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
            onItemRemoved(checkListId) // 서버에 삭제 요청
        }
    }
}
