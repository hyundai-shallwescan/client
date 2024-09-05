package com.ite.sws.domain.member.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
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
 * </pre>
 */
class MyPaymentFragment : Fragment() {

    private var _binding: FragmentMyPaymentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPaymentViewModel by viewModels()

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
}