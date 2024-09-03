package com.ite.sws.domain.member.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.databinding.FragmentSignupBinding
import com.ite.sws.domain.member.api.repository.MemberRepository
import com.ite.sws.util.hideBottomNavigation

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

}