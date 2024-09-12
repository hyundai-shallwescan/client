package com.ite.sws.domain.checklist.view.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.ite.sws.databinding.DialogChecklistBinding

/**
 * 체크리스트 항목 추가 다이얼로그
 * @author 정은지
 * @since 2024.09.08
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.08  	정은지       최초 생성
 * 2024.09.08   정은지       체크리스트 항목 추가
 * </pre>
 */
class CheckListAddDialog : DialogFragment() {

    private var _binding: DialogChecklistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogChecklistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnAdd.setOnClickListener {
            val newItem = binding.edtItem.text.toString()
            if (newItem.isNotEmpty()) {
                val result = Bundle()
                result.putString("newItem", newItem)
                parentFragmentManager.setFragmentResult("requestKey", result)
                dismiss()  // 다이얼로그 닫기

            } else {
                Toast.makeText(context, "항목을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
