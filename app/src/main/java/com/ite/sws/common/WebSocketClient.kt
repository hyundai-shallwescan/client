package com.ite.sws.common

import android.annotation.SuppressLint
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

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
 * </pre>
 */
object WebSocketClient {
    private const val URL = "ws://10.0.2.2:8080/ws"
    private lateinit var stompClient: StompClient

    // JWT 토큰을 포함하여 WebSocket 연결 설정
    @SuppressLint("CheckResult")
    fun connect(jwtToken: String, lifecycleEventHandler: (LifecycleEvent) -> Unit) {
        // StompClient 초기화
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, URL)
        stompClient.withClientHeartbeat(1000).withServerHeartbeat(1000)

        // JWT 토큰을 포함한 StompHeader 설정
        val headers = mutableListOf<StompHeader>()
        headers.add(StompHeader("Authorization", "Bearer $jwtToken"))

        stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(lifecycleEventHandler)

        // WebSocket 연결 시 헤더를 포함하여 연결
        stompClient.connect(headers)
    }

    fun disconnect() {
        if (::stompClient.isInitialized) {
            stompClient.disconnect()
        }
    }

    @SuppressLint("CheckResult")
    fun subscribe(destination: String, messageHandler: (String) -> Unit) {
        stompClient.topic(destination)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                messageHandler(message.payload)
            }
    }

    @SuppressLint("CheckResult")
    fun sendMessage(destination: String, data: String, successHandler: () -> Unit, errorHandler: (Throwable) -> Unit) {
        stompClient.send(destination, data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                successHandler()
            }, { error ->
                errorHandler(error)
            })
    }
}


