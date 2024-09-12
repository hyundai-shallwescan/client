package com.ite.sws.domain.member.view.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentLoginBinding
import com.ite.sws.domain.member.api.repository.MemberRepository
import com.ite.sws.domain.member.data.PostLoginReq
import com.ite.sws.util.CustomDialog
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation

/**
 * 로그인 프래그먼트
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
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val memberRepository = MemberRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // 내비게이션 바 숨김
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // 회원가입 버튼 클릭
        binding.tvSignup.setOnClickListener {
            replaceFragmentWithAnimation(R.id.container_main, SignUpFragment(), true)
        }
    }

    /**
     * 로그인
     */
    private fun performLogin() {
        val loginId = binding.edtId.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (loginId.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "로그인 ID와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val postLoginReq = PostLoginReq(loginId, password)

        memberRepository.login(
            context = requireContext(),
            postLoginReq,
            // 성공 시 메인 화면으로 이동
            onSuccess = {
                // MainActivity 재시작
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            },

            // 실패 시 로그인 실패 모달
            onFailure = { errorRes ->
                CustomDialog(
                    layoutId = R.layout.dialog_text1_btn1,
                    title = "${errorRes.message}",
                    confirmText = "확인",
                    onConfirm = {}
                ).show(activity?.supportFragmentManager!!, "CustomDialog")
            }
        )
    }
}