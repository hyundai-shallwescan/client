package com.ite.sws.domain.payment.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentPaymentQRBinding
import com.ite.sws.domain.member.view.ui.MyPaymentFragment
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import setupToolbar

/**
 * QR(결제 인증) 프래그먼트
 * @author 김민정
 * @since 2024.09.11
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.11   김민정       최초 생성
 * </pre>
 */
class PaymentQRFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentPaymentQRBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentQRBinding.inflate(inflater, container, false)

        // 바텀 네비게이션 바 숨김
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        // 툴바 타이틀 설정
        setupToolbar(binding.toolbar.toolbar, "결제 완료", false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // QR 이미지 설정
        qrSettings()

        // 버튼 설정
        btnSettings()
    }

    /**
     * QR(결제 인증) 이미지 설정
     */
    private fun qrSettings() {
        val qrUrl = arguments?.getString("qrUrl") ?: return
        Glide.with(binding.root.context)
            .load(qrUrl)
            .into(binding.imgQr)
    }

    /**
     * 버튼 이벤트 설정
     */
    private fun btnSettings() {
        // 결제 내역 확인 버튼
        binding.btnPayList.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                replaceFragmentWithAnimation(
                    R.id.container_main,
                    MyPaymentFragment(),
                    false,
                    false
                )
            }
        }
    }
}