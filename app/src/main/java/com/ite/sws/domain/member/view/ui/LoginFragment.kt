package com.ite.sws.domain.member.view.ui

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
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
import setupToolbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var memberRepository: MemberRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        memberRepository = MemberRepository(requireContext())

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
//            replaceFragmentWithAnimation(R.id.container_main, SignUpFragment(), true)
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

        memberRepository.login(postLoginReq,
            onSuccess = {
                // TODO 이전 화면으로 돌아가기
                val token = SharedPreferencesUtil.getString(requireContext(), "jwt_token")
                Toast.makeText(requireContext(), "로그인 성공 $token", Toast.LENGTH_SHORT).show()
            },
            onFailure = { errorRes ->
                // TODO 로그인 실패 모달
                Toast.makeText(requireContext(), "로그인 실패: ${errorRes.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

}