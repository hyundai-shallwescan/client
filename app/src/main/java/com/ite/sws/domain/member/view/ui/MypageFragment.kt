package com.ite.sws.domain.member.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentMypageBinding
import com.ite.sws.domain.member.api.repository.MemberRepository
import com.ite.sws.util.CustomDialog
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
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
 * 2024.09.03   정은지       버튼 클릭 리스너 설정
 * 2024.09.03   정은지       로그아웃 기능 추가
 * 2024.09.03   정은지       회원 탈퇴 기능 추가
 * 2024.09.04   정은지       회원 정보 수정 기능 추가
 * 2024.09.05   정은지       구매 내역 조회 기능 추가
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

        binding.tvName.text = SharedPreferencesUtil.getCartMemberName() + "님"

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
            replaceFragmentWithAnimation(R.id.container_main, MyReviewFragment(), true)
        }

        // 업데이트 버튼 클릭
        binding.btnUpdate.setOnClickListener {
            replaceFragmentWithAnimation(R.id.container_main, ModifyMemberFragment(), true)
        }

        // 구매 내역 버튼 클릭
        binding.btnPayment.setOnClickListener {
            replaceFragmentWithAnimation(R.id.container_main, MyPaymentFragment(), true)
        }

        // 로그아웃 버튼 클릭
        binding.btnLogout.setOnClickListener {
            CustomDialog(
                layoutId = R.layout.dialog_text1_btn2,
                title = "로그아웃 하시겠어요?",
                confirmText = "확인",
                cancelText = "취소",
                onConfirm = {
                    memberRepository.logout(
                        // 로그아웃 성공 시
                        onSuccess = {
                            CustomDialog (
                                layoutId = R.layout.dialog_text1_btn1,
                                title = "로그아웃 되었습니다.",
                                confirmText = "확인",
                                onConfirm = {
                                    // 로그인 화면으로 이동
                                    replaceFragmentWithAnimation(R.id.container_main, LoginFragment(), true)
                                }
                            ).show(activity?.supportFragmentManager!!, "CustomDialog")
                        },
                        // 로그아웃 실패 시
                        onFailure = { throwable ->
                            Toast.makeText(requireContext(), "로그아웃 실패: ${throwable.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onCancel = {}
            ).show(activity?.supportFragmentManager!!, "CustomDialog")
        }

        // 회원탈퇴 버튼 클릭
        binding.btnWithdraw.setOnClickListener {
            CustomDialog(
                layoutId = R.layout.dialog_text2_btn2,
                title = "정말로 탈퇴하시겠어요?",
                message = "탈퇴 시 작성한 리뷰는 모두 삭제됩니다.",
                confirmText = "탈퇴",
                cancelText = "취소",
                onConfirm = {
                    memberRepository.withdraw(
                        onSuccess = {
                            CustomDialog (
                                layoutId = R.layout.dialog_text1_btn1,
                                title = "탈퇴되었습니다.",
                                confirmText = "확인",
                                onConfirm = {
                                    // 로그인 화면으로 이동
                                    replaceFragmentWithAnimation(R.id.container_main, LoginFragment(), true)
                                }
                            ).show(activity?.supportFragmentManager!!, "CustomDialog")
                        },
                        onFailure = { errorRes ->
                            Toast.makeText(requireContext(), "회원 탈퇴 실패: ${errorRes.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onCancel = {}
            ).show(activity?.supportFragmentManager!!, "CustomDialog")
        }
    }
}