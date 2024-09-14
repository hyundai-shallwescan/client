package com.ite.sws.domain.sharelist.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.databinding.ItemProductBinding
import com.ite.sws.domain.product.data.GetProductRes

/**
 * 상품 검색 목록 리사이클러 어댑터
 * @author 김민정
 * @since 2024.09.13
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.13  김민정       최초 생성
 * 2024.09.13  김민정       상품 아이템 클릭 이벤트 설정
 * </pre>
 */
class ProductSearchAdapter (private val onItemClick: (GetProductRes) -> Unit) :
    RecyclerView.Adapter<ProductSearchAdapter.ProductViewHolder>() {

    private var products: List<GetProductRes> = listOf()

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: GetProductRes, position: Int) {
            binding.productName.text = product.name
            binding.productNum.text = position.toString()
            binding.root.setOnClickListener {
                onItemClick(product) // 아이템 클릭 시 콜백 호출
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position], position + 1)
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<GetProductRes>) {
        products = newProducts
        notifyDataSetChanged()
    }
}