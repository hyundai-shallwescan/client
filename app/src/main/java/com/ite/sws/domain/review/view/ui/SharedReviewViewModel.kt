package com.ite.sws.domain.review.view.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ite.sws.domain.member.data.GetMemberPaymentRes

class SharedReviewViewModel : ViewModel() {
    private val _selectedReviewItem = MutableLiveData<GetMemberPaymentRes.GetMemberPaymentItemRes>()
    val selectedReviewItem: LiveData<GetMemberPaymentRes.GetMemberPaymentItemRes> get() = _selectedReviewItem

    fun selectReviewItem(item: GetMemberPaymentRes.GetMemberPaymentItemRes) {
        _selectedReviewItem.value = item
    }
}