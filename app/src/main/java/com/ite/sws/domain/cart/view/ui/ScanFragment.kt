package com.ite.sws.domain.cart.view.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ite.sws.R
import com.ite.sws.databinding.FragmentScanBinding
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
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  김민정       최초 생성
 * 2024.08.31  김민정       상품 바코드 스캔
 * 2024.08.31  김민정       스캔한 상품을 장바구니 아이템으로 등록
 * 2024.09.01  김민정       카메라 권한 설정
 * </pre>
 */
class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private lateinit var scanViewModel: ScanViewModel

    private lateinit var barcodeScannerView: DecoratedBarcodeView


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

        return binding.root
    }

    /**
     * 바코드 스캔 시작
     */
    private fun startBarcodeScanner() {
        barcodeScannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    val barcode = it.text
                    scanViewModel.postCartItem(barcode)
                    barcodeScannerView.pause()
                }
            }

            override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {
            }
        })
    }

    /**
     * 권한 요청 결과를 처리하는 launcher
     */
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startBarcodeScanner()
        } else {
            // 사용자가 권한을 거부한 경우
            showCustomDialog(
                context = requireContext(),
                title = getString(R.string.modal_camera_title),
                message = getString(R.string.modal_camera_message),
                layoutId = R.layout.dialog_title_message_twobtn,
                confirmText = getString(R.string.modal_go_setting),
                cancelText = getString(R.string.modal_cancel),
                onConfirm = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                },
                onCancel = {
                    // 사용자가 취소를 눌렀을 때의 동작
                }
            )
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