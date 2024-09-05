package com.ite.sws.domain.member.view.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentSignupBinding
import com.ite.sws.domain.member.api.repository.MemberRepository
import com.ite.sws.domain.member.data.PostMemberReq
import com.ite.sws.util.CustomDialog
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation

/**
 * 회원가입 프래그먼트
 * @author 정은지
 * @since 2024.09.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자       수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02   정은지       최초 생성
 * </pre>
 */
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val memberRepository =  MemberRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        // 내비게이션 바 숨김
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkId.setOnClickListener {
            val loginId = binding.edtId.text.toString().trim()

            if (loginId.isEmpty()) {
                Toast.makeText(requireContext(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                isLoginIdAvailable(loginId)
            }
        }

        // 회원가입 버튼 클릭
        binding.btnSignup.setOnClickListener {
            performSignup()
        }
    }

    /**
     * 로그인 아이디 중복 체크
     */
    private fun isLoginIdAvailable(loginId: String) {
        Log.d("SignUpFragment", loginId)

        memberRepository.isLoginIdAvailable(
            loginId = loginId,
            onSuccess = {
                CustomDialog(
                    layoutId = R.layout.dialog_text1_btn1,
                    title = "사용 가능한 아이디입니다.",
                    confirmText = "확인",
                    onConfirm = {}
                ).show(activity?.supportFragmentManager!!, "CustomDialog")
            },
            onFailure = { ErrorRes ->
                CustomDialog(
                    layoutId = R.layout.dialog_text1_btn1,
                    title = ErrorRes.message,
                    confirmText = "확인",
                    onConfirm = {}
                ).show(activity?.supportFragmentManager!!, "CustomDialog")
            }
        )
    }

    /**
     * 회원가입 처리
     */
    private fun performSignup() {

        // 입력된 데이터 가져옴
        val loginId = binding.edtId.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val checkPassword = binding.edtCheckPassword.text.toString().trim()
        val name = binding.edtName.text.toString().trim()
        val phoneNumber = binding.edtPhone.text.toString().trim()
        val carNumberInput = binding.edtCar.text.toString().trim()
        val carNumber = if (carNumberInput.isEmpty()) null else carNumberInput
        val gender = if (binding.rbMale.isChecked) 'M' else 'F'
        val age = binding.edtAge.text.toString().trim().toInt()

        // 비밀번호 일치 여부 확인
        if (password != checkPassword) {
            CustomDialog(
                layoutId = R.layout.dialog_text1_btn1,
                title = "비밀번호가 일치하지 않습니다.",
                confirmText = "확인",
                onConfirm = {}
            ).show(activity?.supportFragmentManager!!, "CustomDialog")
            return
        }

        // 회원가입 요청 데이터 생성
        val signupRequest = PostMemberReq(
            loginId = loginId,
            password = password,
            name = name,
            gender = gender,
            age = age,
            phoneNumber = phoneNumber,
            carNumber = carNumber
        )

        // 회원가입 API 호출
        memberRepository.signup(signupRequest,
            onSuccess = {
                CustomDialog(
                    layoutId = R.layout.dialog_text1_btn1,
                    title = "가입되었습니다.",
                    confirmText = "확인",
                    onConfirm = {
                        replaceFragmentWithAnimation(R.id.container_main, LoginFragment(), true)
                    }
                ).show(activity?.supportFragmentManager!!, "CustomDialog")
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "회원가입 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}