package com.ite.sws.domain.checklist.view.adapter

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.R

class SwipeToDeleteCallback(
    private val adapter: CheckListRecyclerViewAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val checkListId = adapter.getItemIdAt(position)
        adapter.removeItem(checkListId)
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
            val layoutCartItem = viewHolder.itemView.findViewById<View>(R.id.layout_item_checklist)
            getDefaultUIUtil().onDraw(c, recyclerView, layoutCartItem, dX, dY, actionState, isCurrentlyActive)
        }
    }

    /**
     * 스와이프 작업이 종료되었을 때 호출
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val layoutCartItem = viewHolder.itemView.findViewById<View>(R.id.layout_item_checklist)
        getDefaultUIUtil().clearView(layoutCartItem)
    }

    /**
     * 아이템이 선택되었을 때 호출
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            val layoutCartItem = it.itemView.findViewById<View>(R.id.layout_item_checklist)
            getDefaultUIUtil().onSelected(layoutCartItem)
        }
    }
}
