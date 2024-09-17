package com.ite.sws.domain.sharelist.view.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ite.sws.R
import com.ite.sws.common.WebSocketClient
import com.ite.sws.databinding.FragmentExternalShareListBinding
import com.ite.sws.domain.sharelist.data.ShareListMessageDTO
import com.ite.sws.domain.sharelist.view.adapter.ExternalShareListRecyclerAdapter
import com.ite.sws.domain.sharelist.view.adapter.ProductSearchAdapter
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.SwipeHelperCallbackShare

/**
 * 외부일행 공유 체크 리스트 프래그먼트
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  김민정       최초 생성
 * 2024.09.12  김민정       웹소켓을 통해 실시간으로 공유 체크리스트 아이템 변경 사항 구독
 * 2024.09.12  김민정       수신된 메시지에 따라 공유체크리스트 리사이클러뷰 업데이트
 * 2024.09.12  김민정       검색창 설정
 * </pre>
 */
class ExternalShareListFragment : Fragment() {
    // View Binding 객체
    private var _binding: FragmentExternalShareListBinding? = null
    private val binding get() = _binding!!

    // ViewModel 객체
    private lateinit var viewModel: ExternalShareListViewModel

    // 리사이클러뷰 어댑터
    private lateinit var recyclerAdapter: ExternalShareListRecyclerAdapter
    private lateinit var productSearchAdapter: ProductSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExternalShareListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(ExternalShareListViewModel::class.java)

        // 공유체크리스트 RecyclerView 설정
        setupRecyclerView(viewModel)

        // 검색 결과 RecyclerView 설정
        setupProductRecyclerView()

        // 공유체크리스트 아이템 가져오기
        viewModel.findShareList(SharedPreferencesUtil.getCartId())

        // ViewModel에서 발생한 이벤트 관찰
        observeViewModel()

        // 공유체크리스트 아이템 변경 사항 관찰
        observeShareListUpdate()

        // 검색창 설정
        setupSearchView()
    }

    /**
     * RecyclerView 설정
     */
    private fun setupRecyclerView(viewModel : ExternalShareListViewModel) {
        recyclerAdapter = ExternalShareListRecyclerAdapter(viewModel)
        binding.recyclerviewSharelist.apply {
            layoutManager = LinearLayoutManager(requireContext()) // LayoutManager 설정
            adapter = recyclerAdapter
        }
        itemTouchHelperSetting()
    }

    /**
     * 검색 결과 RecyclerView 설정
     */
    private fun setupProductRecyclerView() {
        // 검색 결과 RecyclerView 어댑터 초기화
        productSearchAdapter = ProductSearchAdapter { product ->
            // 상품이 선택되었을 때 공유체크리스트 아이템 추가
            viewModel.saveShareListItem(SharedPreferencesUtil.getCartId(), product.productId)
        }

        // 검색 결과 RecyclerView 설정
        binding.recyclerviewProduct.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productSearchAdapter
        }
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
        // 아이템 추가 요청 결과 관찰
        viewModel.saveItemResult.observe(viewLifecycleOwner) { success ->
            success?.let {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fragment_sharelist_success),
                    Toast.LENGTH_SHORT
                ).show()
            }}

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

        // 상품 검색 결과 관찰
        viewModel.productSearchResults.observe(viewLifecycleOwner) { products ->
            productSearchAdapter.updateProducts(products)
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
        val cartId = SharedPreferencesUtil.getCartId()
        val subscriptionPath = "/sub/cart/$cartId"

        WebSocketClient.subscribe(subscriptionPath) { message ->
            // JSON 메시지를 Map으로 파싱하여 type을 확인
            val messageMap = Gson().fromJson(message, Map::class.java)
            val messageType = messageMap["type"] as? String

            // 메시지 타입에 따라 처리
            when (messageType) {
                "shareCheck" -> {
                    val shareCheckMessage = Gson().fromJson(message, ShareListMessageDTO::class.java)
                    activity?.runOnUiThread {
                        updateShareListRecyclerView(shareCheckMessage)
                    }
                }
                else -> Log.e("STOMP SHARE CHECK LIST", "Unknown message type: $messageType")
            }
        }
    }

    /**
     * 수신된 메시지에 따라 공유체크리스트 리사이클러뷰 업데이트
     */
    private fun updateShareListRecyclerView(message: ShareListMessageDTO) {
        when (message.action) {
            "create" -> recyclerAdapter.addNewItem(message)
            "update" -> recyclerAdapter.modifyItemCheckStatus(message)
            "delete" -> recyclerAdapter.removeItem(message)
        }
    }

    /**
     * 검색창 설정
     */
    @SuppressLint("RestrictedApi")
    private fun setupSearchView() {
        val searchSrcTextView = binding.searchviewProduct.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)

        // 폰트 설정
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.happiness_sans_title)
        searchSrcTextView.typeface = typeface

        // 힌트 색상, 크기 등 설정
        searchSrcTextView.setHintTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        searchSrcTextView.textSize = 16f

        // 검색창 텍스트 리스너
        binding.searchviewProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.findProductListByName(it)  // 검색어로 상품 조회
                    binding.recyclerviewSharelist.visibility = View.GONE
                    binding.recyclerviewProduct.visibility = View.VISIBLE
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // 검색창에 포커스를 줄 때 키보드 자동으로 띄우기
        binding.searchviewProduct.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showKeyboard(view)  // 키보드 띄우기
                binding.recyclerviewSharelist.visibility = View.GONE
                binding.recyclerviewProduct.visibility = View.VISIBLE
            } else {
                hideKeyboard(view)  // 키보드 숨기기
                binding.recyclerviewSharelist.visibility = View.VISIBLE
                binding.recyclerviewProduct.visibility = View.GONE
            }
        }
    }

    /**
     * 키보드를 띄우는 메소드
     */
    private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * 키보드를 숨기는 메소드
     */
    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}