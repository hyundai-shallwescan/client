package com.ite.sws.common

import android.annotation.SuppressLint
import android.util.Log
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompCommand
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage

/**
 * WebSocket Client
 * @author 남진수
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	남진수       최초 생성
 * 2024.09.01  	남진수       JWT 토큰 포함하여 WebSocket 연결 설정
 * 2024.09.03  	남진수       구독 기능 추가
 * </pre>
 */
object WebSocketClient {
    private const val URL = "ws://10.0.2.2:8080/ws"
    private lateinit var stompClient: StompClient
    private var jwtToken: String? = null

    /**
     * JWT 토큰을 포함하여 WebSocket 연결 설정
     */
    @SuppressLint("CheckResult")
    fun connect(token: String, lifecycleEventHandler: (LifecycleEvent) -> Unit) {
        jwtToken = token

        val headers = mutableListOf<StompHeader>()
        headers.add(StompHeader("Authorization", "Bearer $jwtToken"))

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, URL)
        stompClient.withClientHeartbeat(1000).withServerHeartbeat(1000)

        stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(lifecycleEventHandler)

        stompClient.connect(headers)  // 연결 시점에 헤더 포함
    }

    fun disconnect() {
        if (::stompClient.isInitialized) {
            stompClient.disconnect()
        }
    }

    /**
     * 구독 기능
     */
    @SuppressLint("CheckResult")
    fun subscribe(destination: String, messageHandler: (String) -> Unit) {
        stompClient.topic(destination)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                messageHandler(message.payload)
            }
    }

    /**
     * 메시지 전송
     */
    @SuppressLint("CheckResult")
    fun sendMessage(destination: String, data: String, successHandler: () -> Unit, errorHandler: (Throwable) -> Unit) {
        val headers = mutableListOf<StompHeader>()
        // 메시지 전송 시 헤더에 목적지 포함
        headers.add(StompHeader(StompHeader.DESTINATION, destination))
        jwtToken?.let {
            // 토큰이 있을 경우 헤더에 포함
            headers.add(StompHeader("Authorization", "Bearer $it"))
            Log.d("WebSocketClient", "Sending message with token: Bearer $it")
        }

        val stompMessage = StompMessage(
            StompCommand.SEND,
            headers,
            data
        )

        stompClient.send(stompMessage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                successHandler()
            }, { error ->
                errorHandler(error)
            })
    }
}
