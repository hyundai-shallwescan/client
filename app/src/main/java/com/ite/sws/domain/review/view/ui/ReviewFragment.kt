package com.ite.sws.domain.review.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.domain.review.api.repository.ReviewRepository
import com.ite.sws.domain.review.data.GetReviewRes
import com.ite.sws.domain.review.view.adapter.ReviewAdapter
import com.ite.sws.databinding.FragmentReviewBinding
import com.ite.sws.domain.product.view.ui.ProductFragment
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
import setupToolbar

/**
 * 리뷰 디테일 프래그먼트
 * @author 구지웅
 * @since 2024.09.06
 * @version 1.0
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	구지웅       최초 생성
 * 2024.09.21  	구지웅       한 번에 한 개의 비디오 뷰가 보이게 수정
 * 2024.09.21  	구지웅       snapHelper 적용
 * 2024.09.22  	구지웅       auto-play로 수정
 * </pre>
 */


class ReviewFragment : Fragment() {
    private var currentProductId: Long? = null
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var reviews: MutableList<GetReviewRes>
    private var currentPlayingViewHolder: ReviewAdapter.ReviewViewHolder? = null
    private var currentPage = 0
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)

        setupToolbar(binding.toolbar.toolbar, "리뷰", true)
        setupRecyclerView()

        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, false)
        }

        loadReviews(currentPage)
        handleAutoPlayVideo()

        binding.productDetailBtn.setOnClickListener {
            navigateToProductDetail()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerViewReviews.layoutManager = layoutManager
        reviews = mutableListOf()
        reviewAdapter = ReviewAdapter(reviews)
        binding.recyclerViewReviews.adapter = reviewAdapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerViewReviews)

        binding.recyclerViewReviews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    handleAutoPlayVideo()
                }
            }
        })
    }

    private fun loadReviews(page: Int) {
        isLoading = true

        ReviewRepository().getReviews(page = page, size = pageSize, onSuccess = { fetchedReviews ->
            if (fetchedReviews.isNotEmpty()) {
                reviews.addAll(fetchedReviews)
                reviewAdapter.notifyDataSetChanged()

                if (fetchedReviews.size < pageSize) {
                    isLastPage = true
                }

                autoPlayFirstVideo()

            } else {
                isLastPage = true
            }
            isLoading = false
        }, onFailure = { throwable ->
            Toast.makeText(requireContext(), "리뷰 영상 보기 실패: ${throwable.message}", Toast.LENGTH_SHORT).show()
            isLoading = false
        })
    }

    private fun autoPlayFirstVideo() {
        binding.recyclerViewReviews.post {
            val layoutManager = binding.recyclerViewReviews.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
                val viewHolder = binding.recyclerViewReviews.findViewHolderForAdapterPosition(firstVisibleItemPosition) as? ReviewAdapter.ReviewViewHolder
                viewHolder?.let {
                    currentPlayingViewHolder = viewHolder
                    viewHolder.playVideo()
                }
            }
        }
    }


    private fun handleAutoPlayVideo() {
        val layoutManager = binding.recyclerViewReviews.layoutManager as LinearLayoutManager
        val visiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()

        if (visiblePosition != RecyclerView.NO_POSITION) {
            val viewHolder = binding.recyclerViewReviews.findViewHolderForAdapterPosition(visiblePosition) as? ReviewAdapter.ReviewViewHolder
            viewHolder?.let {
                currentPlayingViewHolder?.stopVideo()
                currentPlayingViewHolder = viewHolder
                viewHolder.playVideo()
            }
        }
    }

    private fun navigateToProductDetail() {
        val productFragment = ProductFragment()
        val bundle = Bundle().apply {
            currentProductId?.let { value -> putLong("productId", value) }
        }
        productFragment.arguments = bundle
        replaceFragmentWithAnimation(R.id.container_main, productFragment, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
