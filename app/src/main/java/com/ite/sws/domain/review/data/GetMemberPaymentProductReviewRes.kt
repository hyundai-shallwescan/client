package com.ite.sws.domain.review.data

import android.os.Parcel
import android.os.Parcelable


data class GetMemberPaymentProductReviewRes(
    val id: Int,
    val name: String,
    val thumbnailImage: String,
    val isReviewWritten: Char
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        (parcel.readString() ?: 'N') as Char
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(thumbnailImage)
        parcel.writeString(isReviewWritten.toString())
    }

    override fun describeContents() = 0
    override fun writeToParcel(dest: Parcel?, flags: Int) {
    }

    companion object CREATOR : Parcelable.Creator<GetMemberPaymentProductReviewRes> {
        override fun createFromParcel(parcel: Parcel) = GetMemberPaymentProductReviewRes(parcel)
        override fun newArray(size: Int) = arrayOfNulls<GetMemberPaymentProductReviewRes?>(size)
    }
}