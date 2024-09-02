package com.ite.sws.domain.member.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.common.data.replaceFragmentWithAnimation
import com.ite.sws.databinding.FragmentMypageBinding
import com.ite.sws.domain.member.api.repository.MemberRepository
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.showCustomDialog
import setupToolbar

/**
 * 마이페이지 프래그먼트
 * @author 정은지
 * @since 2024.08.24
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자       수정내용
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

        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, false)
        }

        // 툴바 타이틀 설정
        setupToolbar(binding.toolbar.toolbar, "마이페이지", false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 리뷰 관리 버튼 클릭
        binding.btnReview.setOnClickListener {
            // TODO 리뷰 관리 화면 이동
            replaceFragmentWithAnimation(R.id.container_main, MyReviewFragment(), true)
        }

        // 업데이트 버튼 클릭
        binding.btnUpdate.setOnClickListener {
            // TODO 회원 정보 수정 화면 이동
        }

        // 구매 내역 버튼 클릭
        binding.btnPayment.setOnClickListener {
            // TODO 구매 내역 화면 이동
        }

        // 로그아웃 버튼 클릭
        binding.btnLogout.setOnClickListener {
            // TODO 로그아웃 API
        }

        // 회원탈퇴 버튼 클릭
        binding.btnWithdraw.setOnClickListener {
            showCustomDialog(
                context = requireContext(),
                title = "정말로 탈퇴하시겠어요?",
                message = "탈퇴 시 작성한 리뷰는 모두 삭제됩니다.",
                layoutId = R.layout.dialog_title_message_twobtn,
                confirmText = "탈퇴",
                cancelText = "취소",
                onConfirm = {
                    // TODO 탈퇴 처리 API
                },
                onCancel = {}
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}