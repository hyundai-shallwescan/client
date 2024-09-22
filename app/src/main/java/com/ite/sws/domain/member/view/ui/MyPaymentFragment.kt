package com.ite.sws.domain.member.view.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ite.sws.MainActivity
import com.ite.sws.databinding.FragmentMyPaymentBinding
import com.ite.sws.domain.member.data.GetMemberPaymentRes
import com.ite.sws.domain.member.view.adapter.MyPaymentRecyclerViewAdapter
import com.ite.sws.util.hideBottomNavigation
import setupToolbar

/**
 * 회원 구매 내역 프래그먼트
 * @author 정은지
 * @since 2024.09.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자       수정내용
 * ----------  --------    ---------------------------
 * 2024.09.05   정은지       최초 생성
 * 2024.09.22   김민정       뒤로가기 버튼 커스텀 처리
 * </pre>
 */
class MyPaymentFragment : Fragment() {

    private var _binding: FragmentMyPaymentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MemberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPaymentBinding.inflate(inflater, container, false)

        // 내비게이션바 숨김
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        // 툴바 설정
        setupToolbar(binding.toolbar.toolbar, "구매 내역", true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 불러오기
        loadPaymentItemList()

        // 뒤로가기 버튼 커스텀 처리
        settingBackPress()
    }

    private fun loadPaymentItemList() {
        viewModel.findPaymentItemList(
            onSuccess = { paymentItems ->
                if (paymentItems.isNotEmpty()) {
                    displayPaymentItems(paymentItems)
                } else {
                    showEmptyView()
                }
            },
            onFailure = { errorMsg ->
                showError(errorMsg)
            }
        )
    }

    // 구매 내역이 있을 경우 호출되는 함수
    private fun displayPaymentItems(items: List<GetMemberPaymentRes>) {
        binding.rvPayment.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE

        binding.rvPayment.layoutManager = LinearLayoutManager(context)
        val adapter = MyPaymentRecyclerViewAdapter(items)
        binding.rvPayment.adapter = adapter
    }

    // 구매 내역이 없을 경우 호출되는 함수
    private fun showEmptyView() {
        binding.rvPayment.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // 뒤로가기 버튼 커스텀 처리
    private fun settingBackPress(){
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            val fragmentManager = requireActivity().supportFragmentManager
//
//            // 백스택을 모두 제거
//            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//
//            // MyPageFragment로 이동
//            replaceFragmentWithAnimation(R.id.container_main, MypageFragment(), false)
//        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // MainActivity로 이동
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            requireActivity().finish() // 현재 액티비티 종료
        }
    }
}