package com.ite.sws.domain.cart.view.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.ite.sws.R
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.common.WebSocketClient
import com.ite.sws.util.replaceFragmentWithAnimation
import com.ite.sws.databinding.FragmentScanBinding
import com.ite.sws.domain.cart.view.adapter.CartRecyclerAdapter
import com.ite.sws.util.CustomDialog
import com.ite.sws.domain.chat.view.ui.ChatFragment
import ua.naiksoftware.stomp.dto.LifecycleEvent
import androidx.lifecycle.ViewModelProvider
import com.ite.sws.util.showCustomDialog
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

/**
 * 스캔앤고 프래그먼트
 * @author 김민정
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.24  	정은지       최초 생성
 * 2024.08.31   김민정       최초 생성
 * 2024.08.31   김민정       상품 바코드 스캔
 * 2024.08.31   김민정       스캔한 상품을 장바구니 아이템으로 등록
 * 2024.09.01   김민정       카메라 권한 설정
 * 2024.09.01  	남진수       WebSocket 연결
 * </pre>
 */
class ScanFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    // ViewModel 객체
    private lateinit var scanViewModel: ScanViewModel
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var cartRecyclerAdapter: CartRecyclerAdapter

    // 딜레이를 주기 위한 핸들러
    private val delayHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        scanViewModel = ViewModelProvider(this).get(ScanViewModel::class.java)
        barcodeScannerView = binding.barcodeScanner

        // 카메라 권한 체크 및 요청
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startBarcodeScanner()
        } else {
            requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

        setupRecyclerView()

        // 장바구니 아이템 가져오기
        scanViewModel.findCartItemList(cartId = 22)

        // ViewModel에서 발생한 이벤트를 관찰
        observeViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jwtToken = SharedPreferencesUtil.getString(requireContext(), "jwt_token")

        if (jwtToken.isNullOrEmpty() || !isTokenValid(jwtToken)) {
            findNavController().navigate(R.id.action_scanFragment_to_cartLoginFragment)
            return
        }

        /**
         * WebSocket 연결
         */
        WebSocketClient.connect(jwtToken) { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("STOMP", "WebSocket opened")
                    val cartId = SharedPreferencesUtil.getLong(requireContext(), "cart_id")
                    Log.d("STOMP", "Subscribing to cart $cartId")
                    // 연결이 열리면 특정 장바구니에 구독
                    subscribeToCart(cartId)
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d("STOMP", "WebSocket closed")
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e("STOMP", "WebSocket error", event.exception)
                }
                else -> {
                    Log.d("STOMP", "WebSocket event: ${event.message}")
                }
            }
        }
        setupClickListeners()
    }

    private fun isTokenValid(token: String): Boolean {

        return true
    }

    /**
     * 장바구니 구독
     */
    private fun subscribeToCart(cartId: Long) {
        if (cartId == 0L) {
            Log.e("ScanFragment", "Invalid cartId: $cartId")
            return
        }

        val subscriptionPath = "/sub/chat/$cartId"
        Log.d("ScanFragment", "Subscribing to $subscriptionPath")

        WebSocketClient.subscribe(subscriptionPath) { message ->
            Log.i("STOMP", "Received message for cart $cartId: $message")
            activity?.runOnUiThread {

            }
        }
    }

    /**
     * 채팅으로 이동
     */
    private fun setupClickListeners() {
        binding.button3.setOnClickListener {
            replaceFragmentWithAnimation(
                containerId = R.id.container_main,
                fragment = ChatFragment()
            )
        }
    }

    /**
     * 바코드 스캔 시작
     * - 바코드가 인식되면, 인식된 바코드를 서버로 전송하고 스캐너를 일시 정지
     */
    private fun startBarcodeScanner() {
        barcodeScannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    val barcode = it.text
                    barcodeScannerView.pause()
                    scanViewModel.saveCartItem(barcode)
                }
            }

            override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) { }
        })
    }

    /**
     * ViewModel의 LiveData 관찰
     */
    private fun observeViewModel() {
        scanViewModel.scanResult.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess {
                resumeScannerWithDelay()
            }.onFailure {
                resumeScannerWithDelay()    // 요청 실패 시에도 동일하게 재개
            }
        })

        scanViewModel.cartItems.observe(viewLifecycleOwner, { result ->
            result.onSuccess { items ->
//                cartRecyclerAdapter.submitList(items)
            }.onFailure {
                // 오류 처리
            }
        })
    }

    /**
     * 스캐너 재시작 시, 0.3초(300밀리초)딜레이
     */
    private fun resumeScannerWithDelay() {
        delayHandler.postDelayed({
            barcodeScannerView.resume()
        }, 300)
    }

    private fun setupRecyclerView() {
//        cartRecyclerAdapter = CartRecyclerAdapter()
//        binding.recyclerviewCart.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = cartRecyclerAdapter
//        }
    }

    override fun onResume() {
        super.onResume()
        barcodeScannerView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeScannerView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        delayHandler.removeCallbacksAndMessages(null) // 핸들러에서 대기 중인 작업 제거
        _binding = null
    }

    /**
     * 권한 요청 결과를 처리하는 launcher
     * - 사용자가 카메라 권한을 허용하면 스캐너를 시작, 거부하면 설정 화면으로 이동
     */
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startBarcodeScanner()
        } else {
            // 사용자가 권한을 거부한 경우
            CustomDialog(
                layoutId = R.layout.dialog_text2_btn2,
                title = getString(R.string.modal_camera_title),
                message = getString(R.string.modal_camera_message),
                confirmText = getString(R.string.modal_go_setting),
                cancelText = getString(R.string.modal_cancel),
                onConfirm = {
                    // 확인 버튼 클릭 시 실행할 작업
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                },
                onCancel = {
                    // 취소 버튼 클릭 시 실행할 작업
                }
            ).show(activity?.supportFragmentManager!!, "CustomDialog")
        }
    }


    override fun onResume() {
        super.onResume()
        barcodeScannerView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeScannerView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}