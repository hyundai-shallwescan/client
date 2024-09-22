package com.ite.sws.common

import android.annotation.SuppressLint
import android.util.Log
import com.ite.sws.BuildConfig
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
 * 2024.09.11  	김민정       구독 정보 저장
 * 2024.09.11  	김민정       구독 해지 기능 추가
 * </pre>
 */
object WebSocketClient {
    private const val URL = BuildConfig.WS_BASE_URL
    private lateinit var stompClient: StompClient
    private var jwtToken: String? = null

    // 구독을 관리하기 위한 Map (구독 ID -> 구독 객체)
    private val subscriptions = mutableMapOf<String, io.reactivex.disposables.Disposable>()

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
            .subscribe { event ->
                lifecycleEventHandler(event)
                if (event.type == LifecycleEvent.Type.OPENED) {
                    // WebSocket이 연결되었을 때 구독을 처리하도록 보장
                    Log.d("STOMP", "WebSocket 연결 완료")
                }
            }

        stompClient.connect(headers)
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
        if (::stompClient.isInitialized) { // stompClient가 초기화되어 있는지 확인
            val disposable = stompClient.topic(destination)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { message ->
                    messageHandler(message.payload)
                }

            subscriptions[destination] = disposable
        } else {
            Log.e("WebSocketClient", "stompClient가 초기화되지 않았습니다.")
        }
    }

    /**
     * 메시지 전송
     */
    @SuppressLint("CheckResult")
    fun sendMessage(destination: String, data: String, successHandler: () -> Unit, errorHandler: (Throwable) -> Unit) {
        if (::stompClient.isInitialized) { // stompClient가 초기화되어 있는지 확인
            val headers = mutableListOf<StompHeader>()
            headers.add(StompHeader(StompHeader.DESTINATION, destination))
            jwtToken?.let {
                headers.add(StompHeader("Authorization", "Bearer $it"))
            }

            val stompMessage = StompMessage(StompCommand.SEND, headers, data)

            stompClient.send(stompMessage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    successHandler()
                }, { error ->
                    errorHandler(error)
                })
        } else {
            Log.e("WebSocketClient", "stompClient가 초기화되지 않았습니다.")
        }
    }

    /**
     * 구독 해지 기능
     */
    fun unsubscribe(destination: String) {
        subscriptions[destination]?.dispose() // 구독 해지
        subscriptions.remove(destination)     // 구독 정보 삭제
    }
}
