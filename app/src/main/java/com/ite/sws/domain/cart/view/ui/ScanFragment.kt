package com.ite.sws.domain.cart.view.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ite.sws.R
import com.ite.sws.common.WebSocketClient
import com.ite.sws.databinding.FragmentScanBinding
import com.ite.sws.domain.cart.data.CartItemDetail
import com.ite.sws.domain.cart.view.adapter.CartRecyclerAdapter
import com.ite.sws.domain.payment.data.PaymentDoneDTO
import com.ite.sws.domain.payment.view.ui.ExternalPaymentDoneFragment
import com.ite.sws.domain.payment.view.ui.PaymentFragment
import com.ite.sws.domain.payment.view.ui.PaymentQRFragment
import com.ite.sws.domain.product.view.ui.ProductFragment
import com.ite.sws.util.CustomDialog
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.SwipeHelperCallback
import com.ite.sws.util.replaceFragmentWithAnimation
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
 * 2024.08.31   김민정       최초 생성
 * 2024.08.31   김민정       상품 바코드 스캔
 * 2024.08.31   김민정       스캔한 상품을 장바구니 아이템으로 등록
 * 2024.09.01   김민정       카메라 권한 설정
 * 2024.09.02   김민정       장바구니 아이템 조회
 * 2024.09.03   김민정       장바구니 아이템 수량 변경
 * 2024.09.03   김민정       장바구니 아이템 삭제
 * 2024.09.05   김민정       웹소켓을 통해 실시간으로 장바구니 아이템 변경 사항을 구독
 * 2024.09.09   김민정       결제 버튼 이벤트 설정
 * 2024.09.11   김민정       결제 완료 화면 이동
 * 2024.09.17   김민정       상품 상세 페이지로 이동
 * </pre>
 */
class ScanFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    // ViewModel 객체
    private lateinit var viewModel: ScanViewModel
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var recyclerAdapter: CartRecyclerAdapter

    // 딜레이를 주기 위한 핸들러
    private val delayHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(ScanViewModel::class.java)
        barcodeScannerView = binding.barcodeScanner

        // 리사이클러뷰 설정
        setupRecyclerView(viewModel)

        // 카메라 권한 체크 및 요청
        checkCameraPermission()

        // 장바구니 아이템 가져오기
        viewModel.findCartItemList(SharedPreferencesUtil.getCartId())

        // ViewModel에서 발생한 이벤트를 관찰
        observeViewModel()

        // 공동 장바구니 아이템 변경 사항 관찰
        observeCartItemUpdate()

        // 버튼 설정
        btnSettings()
    }

    /**
     * RecyclerView 설정
     */
    private fun setupRecyclerView(scanViewModel : ScanViewModel) {
        recyclerAdapter = CartRecyclerAdapter(scanViewModel)
        binding.recyclerviewCart.apply {
            layoutManager = LinearLayoutManager(requireContext()) // LayoutManager 설정
            adapter = recyclerAdapter
        }

        // 리사이클러뷰 아이템 터치 시 ProductFragment로 이동
        recyclerAdapter.onViewDetail = { cartItem ->
            navigateToProductDetail(cartItem.productId)
        }

        itemTouchHelperSetting()
    }

    /**
     * ItemTouchHelper 설정
     * : 스와이프 동작 추가
     */
    private fun itemTouchHelperSetting() {
        val swipeHelperCallback = SwipeHelperCallback(recyclerAdapter, viewModel)
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewCart)
    }

    /**
     * 카메라 권한 체크 및 요청
     */
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startBarcodeScanner()
        } else {
            requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
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
                    viewModel.saveCartItem(barcode)
                }
            }

            override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) { }
        })
    }

    /**
     * ViewModel의 LiveData 관찰
     */
    private fun observeViewModel() {

        // 바코드 스캔 성공 결과 관찰
        viewModel.barcodeScanSuccess.observe(viewLifecycleOwner) { success ->
            success?.let {
                Toast.makeText(requireContext(), getString(R.string.fragment_scan_success), Toast.LENGTH_SHORT).show()
                resumeScannerWithDelay()    // 스캐너 재개
            }
        }

        // 바코드 스캔 실패 결과 관찰
        viewModel.barcodeScanError.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), getString(R.string.fragment_scan_fail), Toast.LENGTH_SHORT).show()
                resumeScannerWithDelay()    // 스캐너 재개
            }
        }

        // 장바구니 아이템 조회 결과 관찰
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            if (items.isNotEmpty()) {
                binding.recyclerviewCart.visibility = View.VISIBLE
                binding.layoutBtnPay.visibility = View.VISIBLE
                binding.btnPay.visibility = View.VISIBLE
                binding.layoutCartNotfound.visibility = View.GONE
                recyclerAdapter.submitList(items)
            } else {
                binding.recyclerviewCart.visibility = View.GONE
                binding.layoutBtnPay.visibility = View.GONE
                binding.btnPay.visibility = View.GONE
                binding.layoutCartNotfound.visibility = View.VISIBLE
            }
        }

        // 에러 상태 관찰
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 스캐너 재시작 시, 0.2초(200밀리초)딜레이
     */
    private fun resumeScannerWithDelay() {
        delayHandler.postDelayed({
            barcodeScannerView.resume()
        }, 200)
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

    /**
     * 웹소켓을 통해 실시간으로 장바구니 아이템 변경 사항을 구독
     */
    private fun observeCartItemUpdate() {
        val cartId = SharedPreferencesUtil.getCartId()
        val subscriptionPath = "/sub/cart/$cartId"

        WebSocketClient.subscribe(subscriptionPath) { message ->
            Log.d("STOMP CART", "Received message: $message")

            // JSON 메시지를 Map으로 파싱하여 type을 확인
            val messageMap = Gson().fromJson(message, Map::class.java)
            val messageType = messageMap["type"] as? String

            // 메시지 타입에 따라 처리
            when (messageType) {
                "cartUpdate" -> {
                    val cartItemDto = Gson().fromJson(message, CartItemDetail::class.java)
                    activity?.runOnUiThread {
                        updateCartRecyclerView(cartItemDto)
                    }
                }
                "paymentDone" -> {
                    // 결제 완료 화면 이동 처리
                    val paymentDoneDto = Gson().fromJson(message, PaymentDoneDTO::class.java)
                    activity?.runOnUiThread {
                        handlePaymentDone(paymentDoneDto)
                    }
                }
                else -> Log.e("STOMP CART", "Unknown message type: $messageType")
            }
        }
    }

    /**
     * 수신된 메시지에 따라 장바구니 리사이클러뷰 업데이트
     */
    private fun updateCartRecyclerView(cartItemDto: CartItemDetail) {
        when (cartItemDto.action) {
            "create" -> recyclerAdapter.addNewItem(cartItemDto)
            "increase" -> recyclerAdapter.increaseItemQuantity(cartItemDto)
            "decrease" -> recyclerAdapter.decreaseItemQuantity(cartItemDto)
            "delete" -> recyclerAdapter.removeItem(cartItemDto)
        }
    }

    /**
     * 버튼 이벤트 설정
     */
    private fun btnSettings() {
        // 결제 버튼
        binding.btnPay.setOnClickListener {
            replaceFragmentWithAnimation(
                R.id.container_main,
                PaymentFragment(),
                true,
                false
            )
        }
    }

    /**
     * 결제 완료 화면 이동 처리
     */
    private fun handlePaymentDone(paymentDoneDto: PaymentDoneDTO) {
        // 회원이라면, QR(결제 인증) 화면으로 이동
        if (paymentDoneDto.cartOwnerName == SharedPreferencesUtil.getCartMemberName()) {
            // 소켓 연결 해제
            WebSocketClient.unsubscribe("/sub/cart/${SharedPreferencesUtil.getCartId()}")

            // 새로운 장바구니 ID로 변경
            SharedPreferencesUtil.setCartId(paymentDoneDto.newCartId)
            observeNewCartWebSocket(SharedPreferencesUtil.getCartId())  // 새로운 장바구니 ID로 소켓 재구독

            // QR URL 번들에 추가
            val fragment = PaymentQRFragment()
            val bundle = Bundle()
            bundle.putString("qrUrl", paymentDoneDto.qrUrl)
            fragment.arguments = bundle

            replaceFragmentWithAnimation(
                R.id.container_main,
                fragment,
                false,
                false
            )
        } else {
            // 비회원이라면, 결제 완료 화면으로 이동
            // 소켓 연결 해제
            WebSocketClient.unsubscribe("/sub/cart/${SharedPreferencesUtil.getCartId()}")

            replaceFragmentWithAnimation(
                R.id.container_main,
                ExternalPaymentDoneFragment(),
                false,
                false
            )
        }
    }

    /**
     * ProductFragment로 이동하는 메소드
     */
    private fun navigateToProductDetail(productId: Long) {
        val productFragment = ProductFragment()
        val bundle = Bundle().apply {
            putLong("productId", productId)
        }
        productFragment.arguments = bundle

        replaceFragmentWithAnimation(R.id.container_main, productFragment, true, false)
    }

    /**
     * 웹소켓 재구독
     */
    private fun observeNewCartWebSocket(newCartId: Long) {
        val subscriptionPath = "/sub/cart/$newCartId"

        WebSocketClient.subscribe(subscriptionPath) { message ->
            Log.d("STOMP CART", "New cart subscription: $message")
        }
    }
}
