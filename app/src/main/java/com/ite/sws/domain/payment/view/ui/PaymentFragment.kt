package com.ite.sws.domain.payment.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentPaymentBinding
import com.ite.sws.util.hideBottomNavigation
import setupToolbar

/**
 * 상품 결제 프래그먼트
 * @author 김민정
 * @since 2024.09.09
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.09   김민정       최초 생성
 * </pre>
 */
class PaymentFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    // ViewModel 객체
    private lateinit var viewModel: PaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)

        // 바텀 네비게이션 바 숨김
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        // 툴바 타이틀 설정
        setupToolbar(binding.toolbar.toolbar, "결제", true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)

        // 스피너 설정
        spinnerSettings()
    }

    private fun spinnerSettings() {
        // Spinner에 표시될 결제 수단 배열
        val paymentMethods = arrayOf("토스페이먼츠", "네이버페이", "카카오페이")

        // Spinner 설정
        val spinner: Spinner = binding.spinnerPaymentMethod
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner_payment_method,  // 각 항목의 레이아웃
            paymentMethods                        // 항목 데이터
        )
        spinner.adapter = adapter
    }

}