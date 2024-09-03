package com.ite.sws.domain.member.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ite.sws.R
import com.ite.sws.databinding.FragmentExampleBinding
import com.ite.sws.databinding.FragmentMypageBinding
import com.ite.sws.domain.member.api.repository.MemberRepository
import com.ite.sws.domain.member.data.PostLoginReq

/**
 * 샘플 프래그먼트
 * @author 정은지
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	정은지       최초 생성
 * </pre>
 */
class ExampleFragment : Fragment() {

    private var _binding: FragmentExampleBinding? = null
    private val binding get() = _binding!!
//    private val memberRepository = MemberRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExampleBinding.inflate(inflater, container, false)

        // 툴바 타이틀 설정
        val toolbar = binding.toolbar.toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.toolbarTitle.text = "마이페이지"

        binding.btnLogin.setOnClickListener {
            performLogin()
        }
        return binding.root
    }

    private fun performLogin() {
        val loginId = binding.edtLoginId.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (loginId.isEmpty() || password.isEmpty()) {
            binding.tvLoginResult.text = "로그인 ID와 비밀번호를 입력하세요."
            return
        }

        val postLoginReq = PostLoginReq(loginId, password)

//        memberRepository.login(postLoginReq,
//            onSuccess = { jwtToken ->
//                binding.tvLoginResult.text = "로그인 성공 AccessToken: ${jwtToken.accessToken}"
//            },
//            onFailure = { errorRes ->
//                binding.tvLoginResult.text = "로그인 실패: ${errorRes.message}"
//            }
//        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}