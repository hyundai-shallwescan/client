package com.ite.sws.domain.parking.view.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ite.sws.databinding.FragmentParkingBinding
import com.ite.sws.domain.parking.api.repository.ParkingRepository
import com.ite.sws.util.NumberFormatterUtil

/**
 * 주차정산 프래그먼트
 * @author 남진수
 * @since 2024.08.24
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.24  	남진수       최초 생성
 * 2024.09.08   남진수       주차 정산 정보 조회
 * </pre>
 */
class ParkingFragment : Fragment() {

    private var _binding: FragmentParkingBinding? = null
    private val binding get() = _binding!!
    private val parkingRepository = ParkingRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParkingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 주차 정보 가져오기
        parkingRepository.getParkingInfo(
            onSuccess = { parkingInfo ->

                if (parkingInfo.paymentStatus == "ACTIVE") {
                    // 주차정산 완료된 경우
                    Toast.makeText(context, "주차정산 완료.", Toast.LENGTH_SHORT).show()
                }
                // 주차정산이 이뤄지지 않은 경우
                else {
                    // 장바구니가 빈 경우
                    if (parkingInfo.parkingPaymentStatus == "EMPTY"){
                        binding.parkingPaymentMessage.text = "영수증 할인은 자동으로 이뤄집니다!"
                    }
                    // 장바구니에 담긴 경우
                    if (parkingInfo.parkingPaymentStatus == "CART") {
                        // 추가 결제를 해야 무료 주차 가능한 경우
                        if (parkingInfo.feeForFreeParking > 0) {
                            binding.parkingPaymentMessage.text = createSpannableString(
                                parkingInfo.feeForFreeParking.toString() + "원만 추가하면\n주차 정산이 무료예요!",
                                parkingInfo.feeForFreeParking.toString()
                            )
                          // 장바구니에 담긴 금액으로 무료 주차가 가능한 경우
                        } else if (parkingInfo.freeParkingTime.matches(Regex("^(?:[01]?\\d|2[0-3]):[0-5]\\d$"))) {
                            binding.parkingPaymentMessage.text = createSpannableString(
                                "현재 장바구니에 담긴 금액으로,\n" + parkingInfo.freeParkingTime + "까지 무료로 출차 가능합니다.",
                                parkingInfo.freeParkingTime
                            )
                        } else{
                            // 주차 시간이 5시간이 넘은 경우(무료 주차 불가)
                            binding.parkingPaymentMessage.text = parkingInfo.freeParkingTime
                        }

                    }
                    // 쇼핑 정산 완료된 경우
                    if (parkingInfo.parkingPaymentStatus == "PAID") {
                        // 추가 결제를 해야 무료 주차 가능한 경우
                        if (parkingInfo.feeForFreeParking > 0) {
                            binding.parkingPaymentMessage.text = createSpannableString(
                                parkingInfo.feeForFreeParking.toString() + "원만 추가하면\n주차 정산이 무료예요!",
                                parkingInfo.feeForFreeParking.toString()
                            )
                          // 결제 완료된 금액으로 무료 주차가 가능한 경우
                        } else if (parkingInfo.freeParkingTime.matches(Regex("^(?:[01]?\\d|2[0-3]):[0-5]\\d$"))) {
                            binding.parkingPaymentMessage.text = createSpannableString(
                                parkingInfo.freeParkingTime + "까지 무료로 출차 가능합니다.",
                                parkingInfo.freeParkingTime
                            )
                        } else{
                          // 주차 시간이 5시간이 넘은 경우(무료 주차 불가)
                            binding.parkingPaymentMessage.text = parkingInfo.freeParkingTime
                        }
                    }
                }
                binding.parkingEntryTime.text = parkingInfo.entranceAt.dropLast(3)
                binding.parkingCarNumber.text = parkingInfo.carNumber
                binding.parkingTimeHour.text = "${parkingInfo.parkingHour}시간"
                binding.parkingTimeMinute.text = "${parkingInfo.parkingMinute}분"
                binding.parkingDiscountHour.text = "- ${parkingInfo.discountParkingHour}시간"
                binding.parkingDiscountMinute.text = "00분"
                binding.parkingFeeTimeHour.text = "${parkingInfo.paymentHour}시간"
                binding.parkingFeeTimeMinute.text = "${parkingInfo.paymentMinute}분"
                binding.parkingFee.text = NumberFormatterUtil.formatCurrencyWithCommas(parkingInfo.parkingFee.toInt())

            },
            onFailure = { error ->
                // 에러 처리
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    /**
     * 특정 문자열 확대
     */
    private fun createSpannableString(fullText: String, targetText: String): SpannableString {
        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf(targetText)
        val endIndex = startIndex + targetText.length
        spannableString.setSpan(RelativeSizeSpan(1.5f), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
