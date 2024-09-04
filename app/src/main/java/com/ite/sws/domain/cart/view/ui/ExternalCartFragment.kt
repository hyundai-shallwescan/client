package com.ite.sws.domain.cart.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ite.sws.databinding.FragmentExternalCartBinding
import com.ite.sws.domain.cart.view.adapter.ExternalCartRecyclerAdapter
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.SwipeHelperCallback

/**
 * 외부일행 장바구니 프래그먼트
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  	김민정       최초 생성
 * 2024.09.04  	김민정       장바구니 아이템 조회
 * 2024.09.04  	김민정       장바구니 아이템 수량 변경
 * 2024.09.04  	김민정       장바구니 아이템 삭제
 * </pre>
 */
class ExternalCartFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentExternalCartBinding? = null
    private val binding get() = _binding!!

    // ViewModel 객체
    private lateinit var viewModel: ExternalCartViewModel
    private lateinit var recyclerAdapter: ExternalCartRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExternalCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(ExternalCartViewModel::class.java)

        // 리사이클러뷰 설정
        setupRecyclerView(viewModel)

        // 장바구니 아이템 가져오기
        viewModel.findCartItemList(SharedPreferencesUtil.getCartId())

        // ViewModel에서 발생한 이벤트를 관찰
        observeViewModel()
    }

    /**
     * RecyclerView 설정
     */
    private fun setupRecyclerView(viewModel : ExternalCartViewModel) {
        recyclerAdapter = ExternalCartRecyclerAdapter(viewModel)
        binding.recyclerviewCart.apply {
            layoutManager = LinearLayoutManager(requireContext()) // LayoutManager 설정
            adapter = recyclerAdapter
        }
        itemTouchHelperSetting()
    }

    /**
     * ItemTouchHelper 설정
     * : 스와이프 동작 추가
     */
    private fun itemTouchHelperSetting() {
        val swipeHelperCallback = SwipeHelperCallback(recyclerAdapter, viewModel)
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewCart)
    }

    /**
     * ViewModel의 LiveData 관찰
     */
    private fun observeViewModel() {

        // 장바구니 아이템 조회 결과 관찰
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            if (items.isNotEmpty()) {
                binding.recyclerviewCart.visibility = View.VISIBLE
                binding.layoutCartNotfound.visibility = View.GONE
                recyclerAdapter.submitList(items)
            } else {
                binding.recyclerviewCart.visibility = View.GONE
                binding.layoutCartNotfound.visibility = View.VISIBLE
            }
        }

        // 에러 상태 관찰
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
