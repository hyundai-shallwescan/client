package com.ite.sws.domain.member.api.repository

import android.content.Context
import com.google.gson.Gson
import com.ite.sws.common.RetrofitClient
import com.ite.sws.common.data.ErrorRes
import com.ite.sws.domain.member.api.service.MemberService
import com.ite.sws.domain.member.data.PostLoginReq
import com.ite.sws.util.SharedPreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.ite.sws.domain.member.data.GetMemberPaymentRes
import com.ite.sws.domain.member.data.GetMemberRes
import com.ite.sws.domain.member.data.GetMemberReviewRes
import com.ite.sws.domain.member.data.PatchMemberReq
import com.ite.sws.domain.member.data.PostLoginRes
import com.ite.sws.domain.member.data.PostMemberReq
import com.google.android.gms.wearable.Wearable
import com.ite.sws.domain.member.data.JwtToken

/**
 * 회원 Repository
 * @author 정은지
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31   정은지        최초 생성
 * 2024.08.31   정은지        로그인 추가
 * 2024.09.02   정은지        회원 정보 조회 추가
 * 2024.09.03   정은지        로그아웃 추가
 * 2024.09.03   정은지        회원 탈퇴 추가
 * 2024.09.04   정은지        회원가입 추가
 * 2024.09.04   정은지        회원 정보 수정 추가
 * 2024.09.05   정은지        구매 내역 조회 추가
 * 2024.09.06   정은지        작성 리뷰 조회 추가
 * 2024.09.10   남진수        FCM 토큰 발급 추가
 * 2024.09.12   남진수        FCM 토큰을 워치로 전송 추가
 * 2024.09.12   정은지        로그인 시 리프레시 토큰 저장 추가
 * 2024.09.12   정은지        액세스 토큰 재발급 추가
 * </pre>
 */
class MemberRepository {

    private val memberService = RetrofitClient.instance.create(MemberService::class.java)

    /**
     * 로그인
     */
    fun login(
        context: Context,
        postLoginReq: PostLoginReq,
        onSuccess: () -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onFailure(ErrorRes(0, "FCM_TOKEN_ERROR", "FCM 토큰 발급 실패"))
                return@addOnCompleteListener
            }

            // 새로운 FCM Token 발급
            val fcmToken = task.result
            val call = memberService.login(fcmToken, postLoginReq)
            call.enqueue(object : Callback<PostLoginRes> {
                override fun onResponse(
                    call: Call<PostLoginRes>,
                    response: Response<PostLoginRes>
                ) {
                    if (response.isSuccessful) {
                        // 액세스 토큰 SharedPreferences에 저장
                        response.headers()["Authorization"]?.let { accessToken ->
                            val token = accessToken.removePrefix("Bearer ")
                            SharedPreferencesUtil.setAccessToken(token)
                            sendLoginTokenToWear(context, token)
                        }
                        // 리프레시 토큰 SharedPreferences에 저장
                        response.headers()["X-Refresh-Token"]?.let { refreshToken ->
                            val token = refreshToken.removePrefix("Bearer ")
                            SharedPreferencesUtil.setRefreshToken(token)
//                            Log.d("MemberRepository", "리프레시 토큰: $token")
                        }
                        // 장바구니 아이디 SharedPreferences에 저장
                        response.body()?.cartId?.let { cartId ->
                            SharedPreferencesUtil.setCartId(cartId)
                            Log.d("MemberRepository", "장바구니 아이디: $cartId")
                        }
                        // 이름 SharedPreferences에 저장
                        getMyInfo(
                            onSuccess = { memberRes ->
                                SharedPreferencesUtil.setCartMemberName(memberRes.name)
                                onSuccess()
                                Log.d("MemberRepository", "이름: ${memberRes.name}")
                            },
                            onFailure = { errorRes ->
                                onFailure(errorRes)
                            }
                        )
                    } else {
                        val errorRes = Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                        onFailure(errorRes)
                    }
                }

                override fun onFailure(call: Call<PostLoginRes>, t: Throwable) {
                    handleFailure(call, t, onFailure)
                }
            })
        }
    }

    /**
     * 로그아웃
     */
    fun logout(onSuccess: () -> Unit, onFailure: (ErrorRes) -> Unit) {
        val refreshToken = SharedPreferencesUtil.getRefreshToken()

        memberService.logout("Bearer $refreshToken").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 데이터 삭제
                    SharedPreferencesUtil.clearAll()
                    onSuccess()
                } else {
                    val errorRes = Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                handleFailure(call, t, onFailure)
            }
        })
    }

    /**
     * 내 정보 요청
     */
    fun getMyInfo(
        onSuccess: (GetMemberRes) -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        // SharedPreferences에서 JWT 토큰을 가져오기
        val token = SharedPreferencesUtil.getAccessToken()
        if (token != null) {
            memberService.getMyInfo().enqueue(object : Callback<GetMemberRes> {
                override fun onResponse(call: Call<GetMemberRes>, response: Response<GetMemberRes>) {
                    if (response.isSuccessful) {
                        onSuccess(response.body()!!)
                    } else {
                        val errorRes = Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                        onFailure(errorRes)
                    }
                }

                override fun onFailure(call: Call<GetMemberRes>, t: Throwable) {
                    handleFailure(call, t, onFailure)
                }
            })
        }
    }

    /**
     * 회원 탈퇴
     */
    fun withdraw(
        onSuccess: () -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        val refreshToken = SharedPreferencesUtil.getRefreshToken()

        memberService.withdraw("Bearer $refreshToken").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // SharedPrefer 비우기
                    SharedPreferencesUtil.clearAll()
                    onSuccess()
                } else {
                    val errorRes = Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                handleFailure(call, t, onFailure)
            }
        })
    }

    /**
     * 회원가입
     */
    fun signup(
        signupRequest: PostMemberReq,
        onSuccess: () -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        memberService.signup(signupRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorRes = Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                handleFailure(call, t, onFailure)
            }
        })
    }

    /**
     * 로그인 아이디 중복 체크
     */
    fun isLoginIdAvailable(
        loginId: String,
        onSuccess: () -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        memberService.isLoginIdAvailable(loginId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorRes = Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                handleFailure(call, t, onFailure)
            }
        })
    }

    /**
     * 회원 정보 수정
     */
    fun modifyMember(
        modifyRequest: PatchMemberReq,
        onSuccess: () -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        memberService.modifyMember(modifyRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorRes = Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                handleFailure(call, t, onFailure)
            }
        })
    }

    /**
     * 회원 구매 내역 조회
     */
    fun findPaymentItemList(
        onSuccess: (List<GetMemberPaymentRes>) -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        memberService.findPaymentItemList().enqueue(object : Callback<List<GetMemberPaymentRes>> {
            override fun onResponse(call: Call<List<GetMemberPaymentRes>>, response: Response<List<GetMemberPaymentRes>>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    val errorRes = Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<List<GetMemberPaymentRes>>, t: Throwable) {
                handleFailure(call, t, onFailure)
            }
        })
    }

    /**
     * 작성 리뷰 조회
     */
    suspend fun findReviewList(page: Int, size: Int): List<GetMemberReviewRes> {
        return try {
            val response = memberService.findReviewList(page, size)

            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            throw Exception("Network error: ${e.localizedMessage}")
        }
    }

    /**
     * 액세스 토큰 재발급
     */
    fun reissueAccessToken(
        onSuccess: () -> Unit,
        onFailure: (ErrorRes) -> Unit) {
        val refreshToken = SharedPreferencesUtil.getRefreshToken()

        memberService.reissueAccessToken("Bearer $refreshToken").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    response.headers()["Authorization"]?.let { accessToken ->
                        val token = accessToken.removePrefix("Bearer ")
                        SharedPreferencesUtil.setAccessToken(token)
                        onSuccess()
                    }
                } else {
                    val errorRes = Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                handleFailure(call, t, onFailure)
            }
        })
    }

    /**
     * 공통 처리 함수
     */
    private fun <T> handleFailure(call: Call<T>, t: Throwable, onFailure: (ErrorRes) -> Unit) {
        val networkError = ErrorRes(
            status = 0,
            errorCode = "NETWORK_ERROR",
            message = t.localizedMessage ?: "Unknown network error"
        )
        onFailure(networkError)
    }

    /**
     * FCM 토큰을 워치로 전송
     */
    private fun sendLoginTokenToWear(context: Context, token: String) {
        val message = token.toByteArray()

        Wearable.getNodeClient(context).connectedNodes
            .addOnSuccessListener { nodes ->
                if (nodes.isEmpty()) {
                    Log.e("WearableMessageClient", "연결된 기기가 없습니다.")
                } else {
                    for (node in nodes) {
                        Wearable.getMessageClient(context).sendMessage(node.id, "/login_token", message)
                            .addOnSuccessListener {
                                Log.d("WearableMessageClient", "Message successfully sent to ${node.displayName} (${node.id})")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("WearableMessageClient", "Failed to send message to ${node.displayName} (${node.id}): $exception")
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("WearableMessageClient", "Failed to get connected nodes: $exception")
            }
    }

}