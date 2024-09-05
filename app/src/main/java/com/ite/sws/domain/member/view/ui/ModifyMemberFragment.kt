package com.ite.sws.domain.member.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentModifyMemberBinding
import com.ite.sws.domain.member.api.repository.MemberRepository
import com.ite.sws.domain.member.data.PatchMemberReq
import com.ite.sws.util.CustomDialog
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
import setupToolbar

/**
 * 회원 정보 수정 프래그먼트
 * @author 정은지
 * @since 2024.09.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자       수정내용
 * ----------  --------    ---------------------------
 * 2024.09.04  	정은지       최초 생성
 * </pre>
 */
class ModifyMemberFragment : Fragment() {

    private var _binding: FragmentModifyMemberBinding? = null
    private val binding get() = _binding!!
    private val memberRepository = MemberRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentModifyMemberBinding.inflate(inflater, container, false)

        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        setupToolbar(binding.toolbar.toolbar, "회원 정보 수정", true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 서버에서 회원 정보를 가져오는 API 호출
        getMemberInfo()

        // 회원가입 버튼 클릭 이벤트
        binding.btnUpdate.setOnClickListener {
            modifyMember()
        }
    }
    private fun getMemberInfo() {
        memberRepository.getMyPageInfo(
            onSuccess = { member ->
                binding.edtId.setText(member.loginId)
                binding.edtName.setText(member.name)
                binding.edtAge.setText(member.age.toString())
                binding.edtPhone.setText(member.phoneNumber)
                binding.edtCar.setText(member.carNumber)

                if (member.gender == 'M') {
                    binding.rbMale.isChecked = true
                } else {
                    binding.rbFemale.isChecked = true
                }

                // 수정 불가능한 필드 비활성화
                binding.edtId.isEnabled = false
                binding.edtName.isEnabled = false
                binding.edtAge.isEnabled = false
                binding.rbMale.isEnabled = false
                binding.rbFemale.isEnabled = false
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun modifyMember() {
        val password = binding.edtPassword.text.toString().trim()
        val checkPassword = binding.edtCheckPassword.text.toString().trim()
        val phoneNumber = binding.edtPhone.text.toString().trim()
        val carNumber = binding.edtCar.text.toString().trim()

        if (password.isEmpty() || checkPassword.isEmpty()) {
            Toast.makeText(requireContext(), "변경할 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != checkPassword) {
            Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val updateRequest = PatchMemberReq(password, phoneNumber, carNumber)

        memberRepository.modifyMember(
            updateRequest,
            onSuccess = {
                CustomDialog (
                    layoutId = R.layout.dialog_text1_btn1,
                    title = "수정되었습니다",
                    confirmText = "확인",
                    onConfirm = {
                        // 마이페이지 화면으로 이동
                        replaceFragmentWithAnimation(R.id.container_main, MypageFragment(), true)
                    }
                ).show(activity?.supportFragmentManager!!, "CustomDialog")
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "회원 정보 수정 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
