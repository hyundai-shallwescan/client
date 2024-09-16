package com.ite.sws.domain.payment.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ite.sws.R
import com.ite.sws.databinding.BottomSheetPaymentShareBinding
import com.ite.sws.util.ClipboardUtil
import com.ite.sws.util.SharedPreferencesUtil


/**
 * 결제 요청 바텀 시트
 *
 * @author 김민정
 * @since 2024.09.17
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.17  김민정       최초 생성
 * 2024.09.17  김민정       URL 버튼 클릭 시, 딥링크 주소 클립보드에 저장
 * </pre>
 */
class PaymentShareBottomSheet : BottomSheetDialogFragment() {

    // ViewBinding 객체 선언
    private var _binding: BottomSheetPaymentShareBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ViewBinding 객체 초기화
        _binding = BottomSheetPaymentShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 버튼 설정
        setupButtons()
    }

    /**
     * 버튼 이벤트 설정
     */
    private fun setupButtons() {
        // 카카오톡 버튼 리스너 설정
        binding.btnKakao.setOnClickListener {
            Toast.makeText(requireContext(), "카카오톡 버튼 클릭됨", Toast.LENGTH_SHORT).show()
        }

        // URL 버튼 리스너 설정
        binding.btnUrl.setOnClickListener {
            val cartId = SharedPreferencesUtil.getCartId()
            val address = "scanandgo://deeplink?cartId=" + cartId

            ClipboardUtil.copyTextToClipboard(
                requireContext(),
                address,
                "결제 요청 주소가 클립보드에 복사되었습니다.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}