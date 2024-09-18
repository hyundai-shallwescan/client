package com.ite.sws.domain.product.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.MainActivity
import com.ite.sws.databinding.FragmentProductDetailBinding
import com.ite.sws.domain.product.api.repository.ProductRepository
import com.ite.sws.domain.product.view.adatper.ProductImageAdapter
import com.ite.sws.util.NumberFormatterUtil.formatCurrencyWithCommas
import com.ite.sws.util.hideBottomNavigation
import kotlinx.coroutines.launch
import setupToolbar

class ProductFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var productImageAdapter: ProductImageAdapter
    private lateinit var productRepository: ProductRepository
    private var productId: Long = 0
    private var currentPage = 0
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        setupToolbar(binding.toolbar.toolbar, "상품 상세보기", true)

        productId = arguments?.getLong("productId") ?: 0

        setupRecyclerView()

        fetchProductDetailsAndReviews()

        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }
        return view
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerViewProductReviews
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        productImageAdapter = ProductImageAdapter(listOf())
        recyclerView.adapter = productImageAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // Check if we have reached the last loaded item and not already loading
                if (!isLoading && !isLastPage && lastVisibleItem >= totalItemCount - 1) {
                    currentPage++
                    fetchProductDetailsAndReviews(currentPage, pageSize)
                }
            }
        })
    }

    private fun fetchProductDetailsAndReviews(page: Int = 0, size: Int = 10) {
        isLoading = true

        lifecycleScope.launch {
            productRepository = ProductRepository()
            val reviews = productRepository.getProductReviews(productId, page, size)
            reviews?.let {
                if (it.isNotEmpty()) {
                    val firstReview = it.first()

                    if (page == 0) {
                        binding.productName.text = firstReview.name
                        binding.productPrice.text = formatCurrencyWithCommas(firstReview.price.toInt())

                        Glide.with(this@ProductFragment)
                            .load(firstReview.productThumbnailImage)
                            .into(binding.productThumbnail)

                        Glide.with(this@ProductFragment)
                            .load(firstReview.descriptionImage)
                            .into(binding.productInfoImage)
                        Glide.with(this@ProductFragment)
                            .load(firstReview.productThumbnailImage)
                            .into(binding.productThumbnail)

                        Glide.with(this@ProductFragment)
                            .load(firstReview.descriptionImage)
                            .into(binding.productInfoImage)
                    }

                    productImageAdapter.updateImages(it.map { review -> review.shortFormThumbnailImage })

                    if (it.size < size) {
                        isLastPage = true
                    }
                } else {
                    isLastPage = true // No more reviews to load
                }
            }
            isLoading = false // Reset loading state
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
