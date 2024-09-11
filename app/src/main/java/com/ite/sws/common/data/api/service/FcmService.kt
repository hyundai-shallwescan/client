package com.ite.sws.common.data.api.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ite.sws.R
import com.ite.sws.domain.chat.view.ui.ChatFragment

/**
 * FCM 서비스
 * @author 남진수
 * @since 2024.09.10
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자       수정내용
 * ----------  --------    ---------------------------
 * 2024.09.10  	남진수       최초 생성
 * 2024.09.10  	남진수       푸쉬 알림 설정
 * </pre>
 */
class FcmService : FirebaseMessagingService() {
    companion object {
        private const val CHANNEL_ID = "Default"
        private const val CHANNEL_NAME = "Default channel"
    }

    /**
     * 해당 기기의 FCM 토큰이 변경되었을 때의 작업
     */
    @Override
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    /**
     * 푸쉬 메시지 수신 시 작업
     */
    @Override
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    /**
     * 푸쉬 알림 표시
     */
    private fun showNotification(title: String?, message: String?) {

        // 알림 클릭 시, 이동할 지점 지정
        val intent = Intent(this, ChatFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        // 푸쉬 알림 설정
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_nav_scan_off)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // 푸쉬 알림 표시
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.enableVibration(true) // 진동 허용
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}