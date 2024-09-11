package com.ite.sws.domain.review.view.ui

import android.net.Uri
import androidx.fragment.app.Fragment

abstract class BaseVideoFragment : Fragment() {
    abstract fun showVideoUI(uri: Uri)
    abstract fun hideVideoUI()
    abstract fun toggleVideoPlayPause()
    abstract fun openVideoPicker()
    abstract fun resizeVideoView()
}
