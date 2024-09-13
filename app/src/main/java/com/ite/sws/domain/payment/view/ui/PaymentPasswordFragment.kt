package com.ite.sws.domain.payment.view.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ite.sws.MainActivity
import com.ite.sws.databinding.FragmentPaymentPasswordBinding
import com.ite.sws.domain.payment.data.PaymentItemDto
import com.ite.sws.domain.payment.data.PostPaymentReq
import com.ite.sws.util.NumberFormatterUtil.formatCurrencyWithCommas
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.hideBottomNavigation
import setupToolbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 결제 비밀번호 입력 프래그먼트
 * @author 김민정
 * @since 2024.09.10
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.10   김민정       최초 생성
 * 2024.09.10   김민정       결제 요청
 * 2024.09.13   남진수       비밀번호 입력 이벤트
 * </pre>
 */
class PaymentPasswordFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentPaymentPasswordBinding? = null
    private val binding get() = _binding!!

    // ViewModel 객체
    private lateinit var viewModel: PaymentViewModel

    // 비밀번호 입력을 저장할 리스트
    private val passwordInput = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentPasswordBinding.inflate(inflater, container, false)

        // 바텀 네비게이션 바 숨김
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        // Bundle에서 totalPrice 값을 가져와서 표시
        val totalPrice = arguments?.getInt("totalPrice") ?: 0
        binding.tvTotalPrice.text = formatCurrencyWithCommas(totalPrice)

        // 툴바 타이틀 설정
        setupToolbar(binding.toolbar.toolbar, "결제하기", true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)

        // ViewModel에서 발생한 이벤트를 관찰
        observeViewModel()

        // 버튼 설정
        setupKeyPad()
    }

    /**
     * 결제 요청 처리
     */
    private fun makePaymentRequest() {
        // Bundle로부터 결제 정보 가져오기
        val cartId = SharedPreferencesUtil.getCartId()
        val paymentType = arguments?.getString("paymentType") ?: return
        val totalPrice = arguments?.getInt("totalPrice") ?: return
        val productIds = arguments?.getLongArray("productIds") ?: return
        val quantities = arguments?.getIntArray("quantities") ?: return

        // 현재 시간 가져오기
        val paymentTime = getCurrentTime()

        // 결제 아이템 리스트 생성
        val items = productIds.zip(quantities.map { it.toLong() }).map { (productId, quantity) ->
            PaymentItemDto(productId, quantity.toInt())
        }

        // 결제 요청 DTO 생성
        val paymentRequestDto = PostPaymentReq(
            cartId = cartId,
            totalPrice = totalPrice,
            card = paymentType,
            paymentKey = "heendyheendy",  // 결제 키 정보
            paymentTime = paymentTime,
            items = items
        )

        // ViewModel을 통해 결제 요청
        viewModel.savePayment(paymentRequestDto, onSuccess = {
            Log.d("PAYMENT", "Payment Success")
        }, onFailure = { errorMessage ->
            Log.d("PAYMENT", "Payment Fail")
        })
    }

    /**
     * 현재 시간을 "yyyy-MM-dd HH:mm:ss" 형식으로 반환
     */
    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * ViewModel의 LiveData 관찰
     */
    private fun observeViewModel() {
        // 에러 상태 관찰
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 키패드 이벤트 설정
     */
    private fun setupKeyPad() {
        val keypadButtons = listOf(
            binding.paymentKeypad0, binding.paymentKeypad1, binding.paymentKeypad2,
            binding.paymentKeypad3, binding.paymentKeypad4, binding.paymentKeypad5,
            binding.paymentKeypad6, binding.paymentKeypad7, binding.paymentKeypad8,
            binding.paymentKeypad9
        )

        for ((index, button) in keypadButtons.withIndex()) {
            button.setOnClickListener {
                if (passwordInput.size < 6) {
                    passwordInput.add(index.toString())
                    updatePasswordFields()
                }
            }
        }

        binding.paymentKeypadDeleteAll.setOnClickListener {
            passwordInput.clear()
            updatePasswordFields()
        }

        binding.paymentKeypadBackspace.setOnClickListener {
            if (passwordInput.isNotEmpty()) {
                passwordInput.removeAt(passwordInput.size - 1)
                updatePasswordFields()
            }
        }
    }

    /**
     * 비밀번호 입력란 업데이트
     */
    private fun updatePasswordFields() {
        val passwordFields = listOf(
            binding.paymentPassword1, binding.paymentPassword2, binding.paymentPassword3,
            binding.paymentPassword4, binding.paymentPassword5, binding.paymentPassword6
        )

        passwordFields.forEachIndexed { index, textView ->
            textView.text = if (index < passwordInput.size) "*" else ""
        }

        if (passwordInput.size == 6) {
            Handler(Looper.getMainLooper()).postDelayed({
                makePaymentRequest()
            }, 1000) // 1초 딜레이 후 결제 요청
        }
    }
}
