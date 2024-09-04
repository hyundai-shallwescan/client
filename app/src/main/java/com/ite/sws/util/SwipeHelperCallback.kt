package com.ite.sws.util

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.R
import com.ite.sws.domain.cart.view.adapter.CartRecyclerAdapter
import com.ite.sws.domain.cart.view.ui.ScanViewModel

/**
 * SwipeHelperCallback
 * : RecyclerView의 아이템을 스와이프하여 삭제하는 기능을 지원하는 클래스
 *
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  김민정       최초 생성
 * </pre>
 */
class SwipeHelperCallback(
    private val adapter: CartRecyclerAdapter,
    private val viewModel: ScanViewModel
) : ItemTouchHelper.Callback() {

    /**
     * 스와이프 방향 설정
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    /**
     * 아이템 이동을 처리
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    /**
     * 아이템이 스와이프되었을 때 호출
     * - 아이템을 뷰모델과 어댑터에서 제거
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 스와이프 시 아이템 삭제
        val position = viewHolder.adapterPosition
        val deletedItem = adapter.currentList[position]
        viewModel.removeCartItem(SharedPreferencesUtil.getCartId(), deletedItem.productId)
        adapter.removeItem(position)
    }

    /**
     * 아이템이 스와이프되는 동안 호출됨
     * - layoutCartItem만 스와이프되도록 설정
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // layoutCartItem만 스와이프되도록 설정
            val layoutCartItem = viewHolder.itemView.findViewById<View>(R.id.layout_cart_item)
            getDefaultUIUtil().onDraw(c, recyclerView, layoutCartItem, dX, dY, actionState, isCurrentlyActive)
        }
    }

    /**
     * 스와이프 작업이 종료되었을 때 호출
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val layoutCartItem = viewHolder.itemView.findViewById<View>(R.id.layout_cart_item)
        getDefaultUIUtil().clearView(layoutCartItem)
    }

    /**
     * 아이템이 선택되었을 때 호출
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            val layoutCartItem = it.itemView.findViewById<View>(R.id.layout_cart_item)
            getDefaultUIUtil().onSelected(layoutCartItem)
        }
    }
}

