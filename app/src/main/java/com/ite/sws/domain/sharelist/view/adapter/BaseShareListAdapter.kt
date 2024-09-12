package com.ite.sws.domain.sharelist.view.adapter

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.domain.sharelist.data.ShareListItem
import com.ite.sws.domain.sharelist.view.ui.BaseShareListViewModel

abstract class BaseShareListAdapter <ViewHolder : RecyclerView.ViewHolder, ViewModel : BaseShareListViewModel>(
    private val viewModel: ViewModel
) : ListAdapter<ShareListItem, ViewHolder>(ShareListDiffCallback()) {


}