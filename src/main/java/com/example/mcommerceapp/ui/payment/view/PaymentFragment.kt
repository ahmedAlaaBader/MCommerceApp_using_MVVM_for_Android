package com.example.mcommerceapp.ui.payment.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mcommerceapp.databinding.FragmentPaymentBinding
import com.example.mcommerceapp.model.UIState
import com.example.mcommerceapp.ui.payment.viewmodel.PaymentViewModel
import kotlinx.coroutines.flow.collect

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PaymentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up click listener for PayPal payment
        binding.btnContinueToPayment.setOnClickListener {
            if (binding.radioPaypal.isChecked) {
                Toast.makeText(requireContext(), "Processing PayPal Payment", Toast.LENGTH_SHORT).show()
                viewModel.processPayPalPayment(requireActivity())
            } else {
                Toast.makeText(requireContext(), "Cash on Delivery Selected", Toast.LENGTH_SHORT).show()

            }
        }

        // Observe payment result
        lifecycleScope.launchWhenStarted {
            viewModel.paymentResult.collect { state ->
                handlePaymentStatus(state)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PaymentViewModel.PAYPAL_REQUEST_CODE) {
          Toast.makeText(requireContext(), "Processing PayPal Result {$data}", Toast.LENGTH_SHORT).show()
            viewModel.handlePaymentResult(data)
        }else {  Toast.makeText(requireContext(), "Processing PayPal Result {$data}", Toast.LENGTH_SHORT).show()}
    }

    private fun handlePaymentStatus(paymentStatus: UIState) {
        when (paymentStatus) {
            is UIState.Success<*> -> {
                Toast.makeText(requireContext(), "Payment Successful${paymentStatus.data}", Toast.LENGTH_SHORT).show()
                // Handle successful payment
            }
            is UIState.Failure -> {
                Log.d("handlePaymentStatus", "Payment Failed: ${paymentStatus.msg}")
                Toast.makeText(requireContext(), "Payment Failed: ${paymentStatus.msg}", Toast.LENGTH_LONG).show()
                // Show error message
            }
            is UIState.Loading -> {
                Toast.makeText(requireContext(), "Processing Payment...", Toast.LENGTH_SHORT).show()
                // Show loading
            }
            is UIState.NoData -> {
                Toast.makeText(requireContext(), "No data: ${paymentStatus}", Toast.LENGTH_SHORT).show()
                Log.d("handlePaymentStatus", "handlePaymentStatus: 9788"+paymentStatus.toString())
                // Do nothing
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
