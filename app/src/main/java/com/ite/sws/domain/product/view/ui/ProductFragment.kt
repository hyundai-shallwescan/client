package com.ite.sws.domain.product.view.ui

import android.R.attr.text
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.databinding.FragmentProductDetailBinding
import com.ite.sws.domain.product.api.repository.ProductRepository
import com.ite.sws.domain.product.view.adatper.ProductImageAdapter
import kotlinx.coroutines.launch
import setupToolbar

class ProductFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var productImageAdapter: ProductImageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var productRepository: ProductRepository
    private var productId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        setupToolbar(binding.toolbar.toolbar, "상품", true)

        productId = arguments?.getLong("productId") ?: 0

        recyclerView = binding.recyclerViewProductReviews
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        productImageAdapter = ProductImageAdapter(listOf())
        recyclerView.adapter = productImageAdapter

        fetchProductReviews()

        return view
    }

    private fun fetchProductReviews(page: Int = 0, size: Int = 10) {
        lifecycleScope.launch {
            val reviews = productRepository.getProductReviews(productId, page, size)
            reviews?.let {
                productImageAdapter.updateImages(it.map { review -> review.shortFormThumbnailImage })
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
