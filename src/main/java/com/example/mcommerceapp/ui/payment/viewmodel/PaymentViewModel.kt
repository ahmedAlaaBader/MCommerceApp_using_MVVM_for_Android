package com.example.mcommerceapp.ui.payment.viewmodel

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mcommerceapp.model.UIState
import com.paypal.android.sdk.payments.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONException
import java.math.BigDecimal

class PaymentViewModel : ViewModel() {

    private val _paymentResult = MutableStateFlow<UIState>(UIState.Loading)
    val paymentResult: StateFlow<UIState> get() = _paymentResult

    fun processPayPalPayment(activity: Activity) {
        val payment = PayPalPayment(
            BigDecimal("5.00"), "USD", "Your Item", PayPalPayment.PAYMENT_INTENT_SALE
        )

        val intent = Intent(activity, PaymentActivity::class.java).apply {
            putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, getPayPalConfiguration())
            putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
        }
        activity.startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }

    fun handlePaymentResult(data: Intent?) {
        if (data != null) {
            val confirmation =
                data.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
            if (confirmation != null) {
                try {
                    val paymentDetails = confirmation.toJSONObject().toString(4)
                    _paymentResult.value = UIState.Success(paymentDetails)
                } catch (e: JSONException) {
                    _paymentResult.value = UIState.Failure(e)
                    Log.d("handlePaymentResult", "handlePaymentResult:0000 "+e)
                }
            } else {
                Log.d("handlePaymentResult", "handlePaymentResult: 88888")
                _paymentResult.value = UIState.Failure(Exception("Payment canceled"))
            }
        } else {
            Log.d("handlePaymentResult", "handlePaymentResult: 555555 ")
            _paymentResult.value = UIState.Failure(Exception("No payment data received"))
        }
    }

    private fun getPayPalConfiguration(): PayPalConfiguration {
        return PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(CLIENT_ID)
    }

    companion object {
        const val PAYPAL_REQUEST_CODE = 123
        const val CLIENT_ID = "AVqh2v2P2PB6qrj4xznDVvuKybHKBIBTds9_N3XWNB4ETDThgi5uN31axXAgjRLwV-KI5u0_nr71UUzB"
    }
}
