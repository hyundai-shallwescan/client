package com.ite.sws.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ite.sws.R
import com.ite.sws.databinding.DialogText1Btn1Binding
import com.ite.sws.databinding.DialogText1Btn2Binding
import com.ite.sws.databinding.DialogText2Btn2Binding

/**
 * 커스텀 다이얼로그
 *
 * dialog.show(manager: FragmentManager, tag: String) 호출 시, manager로 넘겨줄 값
 * (1) activity ➡ this.supportFragmentManager
 * (2) fragment ➡ activity?.supportFragmentManager!!
 *
 * @author 김민정
 * @since 2024.09.02
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02  김민정       최초 생성
 * </pre>
 */
class CustomDialog(
    private val layoutId: Int,
    private val title: String,
    private val message: String? = null,
    private val confirmText: String = "확인",
    private val cancelText: String? = null,
    private val onConfirm: () -> Unit,
    private val onCancel: (() -> Unit)? = null
) : DialogFragment() {

    private var _bindingText2Btn2: DialogText2Btn2Binding? = null
    private var _bindingText1Btn2: DialogText1Btn2Binding? = null
    private var _bindingText1Btn1: DialogText1Btn1Binding? = null

    private val bindingText2Btn2 get() = _bindingText2Btn2!!
    private val bindingText1Btn2 get() = _bindingText1Btn2!!
    private val bindingText1Btn1 get() = _bindingText1Btn1!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return when (layoutId) {
            R.layout.dialog_text2_btn2 -> {
                _bindingText2Btn2 = DialogText2Btn2Binding.inflate(inflater, container, false)
                bindingText2Btn2.root
            }
            R.layout.dialog_text1_btn2 -> {
                _bindingText1Btn2 = DialogText1Btn2Binding.inflate(inflater, container, false)
                bindingText1Btn2.root
            }
            R.layout.dialog_text1_btn1 -> {
                _bindingText1Btn1 = DialogText1Btn1Binding.inflate(inflater, container, false)
                bindingText1Btn1.root
            }
            else -> throw IllegalArgumentException("Invalid layout ID")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        when (layoutId) {
            R.layout.dialog_text2_btn2 -> {
                bindingText2Btn2.textTitle.text = title
                bindingText2Btn2.textMessage.text = message
                bindingText2Btn2.textConfirm.text = confirmText
                bindingText2Btn2.textCancel.text = cancelText

                bindingText2Btn2.btnConfirm.setOnClickListener {
                    onConfirm.invoke()
                    dismiss()
                }

                bindingText2Btn2.btnCancel.setOnClickListener {
                    onCancel?.invoke()
                    dismiss()
                }
            }
            R.layout.dialog_text1_btn2 -> {
                bindingText1Btn2.textTitle.text = title
                bindingText1Btn2.textConfirm.text = confirmText
                bindingText1Btn2.textCancel.text = cancelText

                bindingText1Btn2.btnConfirm.setOnClickListener {
                    onConfirm.invoke()
                    dismiss()
                }

                bindingText1Btn2.btnCancel.setOnClickListener {
                    onCancel?.invoke()
                    dismiss()
                }
            }
            R.layout.dialog_text1_btn1 -> {
                bindingText1Btn1.textTitle.text = title
                bindingText1Btn1.textConfirm.text = confirmText

                bindingText1Btn1.btnConfirm.setOnClickListener {
                    onConfirm.invoke()
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindingText2Btn2 = null
        _bindingText1Btn2 = null
        _bindingText1Btn1 = null
    }
}
