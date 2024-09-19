package com.ite.sws.domain.chatbot.view.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.R
import com.ite.sws.databinding.ItemChatbotProductItemBinding
import com.ite.sws.domain.chatbot.data.GetChatGptRes
import com.ite.sws.domain.checklist.data.PostCheckListReq
import com.ite.sws.domain.checklist.view.ui.CheckListViewModel
import com.ite.sws.util.CustomDialog
import com.ite.sws.util.NumberFormatterUtil

/**
 * 젤뽀 챗봇 상품 메세지 리사이클러 어댑터
 * @author 정은지
 * @since 2024.09.19
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.19  정은지        최초 생성
 * </pre>
 */
class ChatBotProductRecyclerViewAdapter(
    private val products: List<GetChatGptRes.Product>,
    private val fragmentManager: FragmentManager,
    private val viewModel: CheckListViewModel
    ) : RecyclerView.Adapter<ChatBotProductRecyclerViewAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemChatbotProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: GetChatGptRes.Product) {
            binding.tvProductName.text = product.title
            binding.tvProductPrice.text = NumberFormatterUtil.formatCurrencyWithCommas(product.price)
            Glide.with(binding.root.context).load(product.thumbnailImage).into(binding.imgProductThumbnail)

            binding.btnAddChecklist.setOnClickListener {
                CustomDialog(
                    layoutId = R.layout.dialog_text1_btn2,
                    title = "체크리스트에 추가하시겠어요?",
                    confirmText = "확인",
                    cancelText = "취소",
                    onConfirm = {
                        // CheckList 추가 로직
                        val productTitle = product.title
                        viewModel.addCheckListItem(PostCheckListReq(productTitle))

                        CustomDialog(
                            layoutId = R.layout.dialog_text1_btn1,
                            title = "추가되었습니다.",
                            confirmText = "확인",
                            onConfirm = {}
                        ).show(fragmentManager, "AddToCheckListDialog")
                    }
                ).show(fragmentManager, "AddToChecklistDialog")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemChatbotProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}
