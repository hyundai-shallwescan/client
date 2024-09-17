package com.ite.sws.domain.member.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.ite.sws.R
import com.ite.sws.databinding.FragmentMyReviewWrittenBinding
import com.ite.sws.domain.member.data.GetMemberReviewRes
import com.ite.sws.domain.member.view.adapter.MyReviewRecyclerViewAdapter
import com.ite.sws.domain.review.view.ui.ReviewDetailFragment
import com.ite.sws.util.replaceFragmentWithAnimation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 작성 리뷰 조회 프래그먼트
 * @author 정은지
 * @since 2024.09.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	정은지       최초 생성
 * 2024.09.14   정은지       썸네일 이미지 클릭 시 리뷰 상세 화면으로 이동
 * 2024.09.18   정은지       작성 리뷰가 존재하지 않을 경우 화면 처리
 * </pre>
 */
class WrittenReviewFragment : Fragment() {

    private var _binding: FragmentMyReviewWrittenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MemberViewModel by viewModels()
    private lateinit var adapter: MyReviewRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyReviewWrittenBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadMemberReviews()

        return binding.root
    }

    /**
     * 리사이클러뷰 설정
     */
    private fun setupRecyclerView() {
        adapter = MyReviewRecyclerViewAdapter(viewModel) { selectedReview ->
            navigateToReviewDetail(selectedReview)
        }
        binding.rvWrittenReviews.layoutManager = GridLayoutManager(context, 2)  // 2열 그리드
        binding.rvWrittenReviews.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            val isListEmpty = adapter.itemCount == 0
            if (isListEmpty && loadState.refresh is LoadState.NotLoading) {
                showEmptyView()
            } else {
                displayReviews()
            }
        }
    }

    /**
     * 리뷰 불러오기
     */
    private fun loadMemberReviews() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPagedMemberReviews().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    /**
     * 작성 리뷰가 존재할 경우
     */
    private fun displayReviews() {
        binding.rvWrittenReviews.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
    }

    /**
     * 작성 리뷰가 존재하지 않을 경우
     */
    private fun showEmptyView() {
        binding.rvWrittenReviews.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
    }

    /**
     * 리뷰 상세 화면으로 이동
     */
    private fun navigateToReviewDetail(review: GetMemberReviewRes) {
        val fragment = ReviewDetailFragment()
        val bundle = Bundle().apply {
            putSerializable("shortFormId", review.shortFormId) 
        }
        fragment.arguments = bundle

        replaceFragmentWithAnimation(R.id.container_main, fragment, true, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
