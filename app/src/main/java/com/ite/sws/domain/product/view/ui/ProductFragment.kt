package com.ite.sws.domain.product.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ite.sws.MainActivity
import com.ite.sws.databinding.FragmentProductDetailBinding
import com.ite.sws.domain.product.api.repository.ProductRepository
import com.ite.sws.domain.product.view.adatper.ProductImageAdapter
import com.ite.sws.util.hideBottomNavigation
import kotlinx.coroutines.launch
import setupToolbar
import java.text.NumberFormat

class ProductFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var productImageAdapter: ProductImageAdapter
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

        val recyclerView = binding.recyclerViewProductReviews
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        productImageAdapter = ProductImageAdapter(listOf())
        recyclerView.adapter = productImageAdapter

        fetchProductDetailsAndReviews()

        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }
        return view
    }

    private fun fetchProductDetailsAndReviews(page: Int = 0, size: Int = 10) {
        lifecycleScope.launch {
            productRepository = ProductRepository()
            val reviews = productRepository.getProductReviews(productId, page, size)
            reviews?.let {
                if (it.isNotEmpty()) {

                    val firstReview = it.first()
                    binding.productName.text = firstReview.name
                    binding.productPrice.text = formatPrice(firstReview.price)

                    Glide.with(this@ProductFragment)
                        .load(firstReview.productThumbnailImage)
                        .into(binding.productThumbnail)

                    Glide.with(this@ProductFragment)
                        .load(firstReview.descriptionImage)
                        .into(binding.productInfoImage)

                        Glide.with(this@ProductFragment)
                            .load(firstReview.descriptionImage)
                            .into(binding.productInfoImage)

                    productImageAdapter.updateImages(it.map { review -> review.shortFormThumbnailImage })
                }
            }
        }
    }

    private fun formatPrice(price: Long): String {
        val numberFormat = NumberFormat.getCurrencyInstance()
        return numberFormat.format(price)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
