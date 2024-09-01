package com.ite.sws.util

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.ite.sws.R

/**
 * 공통 다이얼로그 함수
 * @author 김민정
 * @since 2024.09.01
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.01  김민정       최초 생성
 * 2024.09.01  김민정       버튼 텍스트 설정 추가
 * </pre>
 *
 * @param title 다이얼로그 타이틀
 * @param message 다이얼로그 메시지 (선택 사항)
 * @param layoutId 사용할 레이아웃 파일 ID (버튼 1개 또는 2개 레이아웃)
 * @param confirmText 확인 버튼에 표시할 텍스트
 * @param cancelText 취소 버튼에 표시할 텍스트 (선택 사항)
 * @param onConfirm 확인 버튼 클릭 시 동작
 * @param onCancel 취소 버튼 클릭 시 동작 (선택 사항)
 */
fun showCustomDialog(
    context: Context,
    title: String,
    message: String?,
    layoutId: Int,
    confirmText: String,
    cancelText: String? = null,
    onConfirm: () -> Unit,
    onCancel: (() -> Unit)? = null
) {
    val dialogView = LayoutInflater.from(context).inflate(layoutId, null)

    val titleTextView = dialogView.findViewById<TextView>(R.id.text_title)
    titleTextView.text = title

    // 메시지가 있는 경우 설정
    message?.let {
        val messageTextView = dialogView.findViewById<TextView>(R.id.text_message)
        messageTextView?.text = it
    }

    // 다이얼로그 생성
    val dialog = AlertDialog.Builder(context)
        .setView(dialogView)
        .setCancelable(false)
        .create()

    // 확인 버튼 설정
    val confirmBtn = dialogView.findViewById<ImageButton>(R.id.btn_confirm)
    val confirmTextView = dialogView.findViewById<TextView>(R.id.text_confirm)
    confirmTextView?.text = confirmText

    confirmBtn?.setOnClickListener {
        dialog.dismiss()
        onConfirm()
    }

    // 취소 버튼 설정 (onCancel 함수가 주어진 경우에만)
    val cancelBtn = dialogView.findViewById<ImageButton>(R.id.btn_cancel)
    val cancelTextView = dialogView.findViewById<TextView>(R.id.text_cancel)

    cancelBtn?.let {
        if (onCancel != null) {
            it.visibility = View.VISIBLE
            cancelTextView?.text = cancelText
            it.setOnClickListener {
                dialog.dismiss()
                onCancel()
            }
        } else {
            // 취소 버튼을 사용하지 않는 경우
            it.visibility = View.GONE
        }
    }

    dialog.setOnShowListener {
        dialog.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(
                (context.resources.displayMetrics.widthPixels * 0.8).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    dialog.show()
}
