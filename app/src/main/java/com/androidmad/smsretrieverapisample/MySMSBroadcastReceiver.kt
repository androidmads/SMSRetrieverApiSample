package com.androidmad.smsretrieverapisample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


class MySMSBroadcastReceiver : BroadcastReceiver() {

    private var otpReceiver: OTPReceiveListener? = null

    fun initOTPListener(receiver: OTPReceiveListener) {
        this.otpReceiver = receiver
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    Log.d("OTP_Message", otp)
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server for SMS authenticity.
                    // But here we are just passing it to MainActivity
                    if (otpReceiver != null) {
                        otp = otp.replace("<#> ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        otpReceiver!!.onOTPReceived(otp)
                    }
                }

                CommonStatusCodes.TIMEOUT ->
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    if (otpReceiver != null)
                        otpReceiver!!.onOTPTimeOut()
            }
        }
    }

    interface OTPReceiveListener {

        fun onOTPReceived(otp: String)

        fun onOTPTimeOut()
    }
}