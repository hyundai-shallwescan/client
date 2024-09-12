package com.ite.sws.domain.sharelist.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ite.sws.databinding.FragmentShareListBinding
import com.ite.sws.domain.sharelist.view.adapter.ShareListRecyclerAdapter
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.SwipeHelperCallbackShare

/**
 * 공유 체크 리스트 프래그먼트
 * @author 김민정
 * @since 2024.09.02
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02  김민정       최초 생성
 * </pre>
 */
class ShareListFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentShareListBinding? = null
    private val binding get() = _binding!!

    // ViewModel 객체
    private lateinit var viewModel: ShareListViewModel
    private lateinit var recyclerAdapter: ShareListRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShareListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(ShareListViewModel::class.java)

        // 리사이클러뷰 설정
        setupRecyclerView(viewModel)

        // 공유체크리스트 아이템 가져오기
        viewModel.findShareList(SharedPreferencesUtil.getCartId())

        // ViewModel에서 발생한 이벤트 관찰
        observeViewModel()

        // 공유체크리스트 아이템 변경 사항 관찰
        observeShareListUpdate()
    }

    /**
     * RecyclerView 설정
     */
    private fun setupRecyclerView(viewModel : ShareListViewModel) {
        recyclerAdapter = ShareListRecyclerAdapter(viewModel)
        binding.recyclerviewSharelist.apply {
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
        val swipeHelperCallback = SwipeHelperCallbackShare(recyclerAdapter, viewModel)
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewSharelist)
    }

    /**
     * ViewModel의 LiveData 관찰
     */
    private fun observeViewModel() {
        // 공유체크리스트 아이템 조회 결과 관찰
        viewModel.shareListItems.observe(viewLifecycleOwner) { items ->
            if (items.isNotEmpty()) {
                binding.recyclerviewSharelist.visibility = View.VISIBLE
                binding.layoutSharelistNotfound.visibility = View.GONE
                recyclerAdapter.submitList(items)
            } else {
                binding.recyclerviewSharelist.visibility = View.GONE
                binding.layoutSharelistNotfound.visibility = View.VISIBLE
            }
        }

        // 에러 상태 관찰
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 웹소켓을 통해 실시간으로 공유 체크리스트 아이템 변경 사항 구독
     */
    private fun observeShareListUpdate() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}