package com.ite.sws.domain.payment.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ite.sws.R
import com.ite.sws.databinding.BottomSheetProductRecommendBinding

/**
 * 추가 결제 상품 추천 바텀 시트
 *
 * @author 김민정
 * @since 2024.09.10
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.10  김민정       최초 생성
 * 2024.09.10  김민정       추천된 상품 정보 표시
 * </pre>
 */
class ProductRecommendBottomSheet(
    private val productThumbnail: String?,
    private val productName: String?,
    private val productPrice: String?,
    private val onPayClickListener: () -> Unit
) : BottomSheetDialogFragment() {

    // ViewBinding 객체 선언
    private var _binding: BottomSheetProductRecommendBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ViewBinding 객체 초기화
        _binding = BottomSheetProductRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 추천된 상품 정보 표시
        setupProductInfo()

        // 버튼 설정
        setupButtons()
    }

    /**
     * 추천된 상품 정보를 View에 설정
     */
    private fun setupProductInfo() {
        binding.apply {
            Glide.with(binding.root.context)
                .load(productThumbnail)
                .into(binding.imgProductThumbnail)
            tvProductName.text = productName ?: "상품 이름 없음"
            tvProductPrice.text = productPrice ?: "0원"
        }
    }

    /**
     * 버튼 이벤트 설정
     */
    private fun setupButtons() {
        // 결제 버튼 설정
        binding.btnPay.setOnClickListener {
            onPayClickListener.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}