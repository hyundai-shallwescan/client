package com.ite.sws.domain.member.view.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ite.sws.databinding.FragmentMypageBinding
import com.ite.sws.domain.member.api.repository.MemberRepository

/**
 * 마이페이지 프래그먼트
 * @author 정은지
 * @since 2024.08.24
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.24  	정은지       최초 생성
 * </pre>
 */
class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private val memberRepository = MemberRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        // 툴바 타이틀 설정
        val toolbar = binding.toolbar.toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.toolbarTitle.text = "마이페이지"

        // 업데이트 버튼 클릭
        setupButtonClickListener(binding.btnUpdate, UpdateProfileActivity::class.java)

        // 리뷰 관리 버튼 클릭
        setupButtonClickListener(binding.btnReview, MyReviewActivity::class.java)

        // 구매 내역 버튼 클릭
        setupButtonClickListener(binding.btnPayment, MyPurchaseHistoryActivity::class.java)

        // 로그아웃 버튼 클릭
        binding.btnLogout.setOnClickListener {

        }

        // 회원탈퇴 버튼 클릭
        binding.btnWithdraw.setOnClickListener {

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 버튼 클릭 시 액티비티 이동 메서드
     */
    fun <T> setupButtonClickListener(button: View, targetActivity: Class<T>) {
        button.setOnClickListener {
            val intent = Intent(activity, targetActivity)
            startActivity(intent)
        }
    }
}