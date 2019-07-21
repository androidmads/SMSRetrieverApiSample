package com.androidmad.smsretrieverapisample

import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.androidmad.smsretrieverapisample.MySMSBroadcastReceiver.OTPReceiveListener
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OTPReceiveListener {

    private var smsReceiver: MySMSBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startSMSListener()
    }


    /**
     * Starts SmsRetriever, which waits for ONE matching SMS message until timeout
     * (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
     * action SmsRetriever#SMS_RETRIEVED_ACTION.
     */
    private fun startSMSListener() {
        try {
            smsReceiver = MySMSBroadcastReceiver()
            smsReceiver!!.initOTPListener(this)

            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)

            val client = SmsRetriever.getClient(this)

            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
            }

            task.addOnFailureListener {
                // Fail to start API
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun onBtnResendClick(view: View){
        startSMSListener()
    }

    override fun onOTPReceived(otp: String) {
        //showToast("OTP Received: " + otp)
        editText.setText(otp)
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver)
        }
    }

    override fun onOTPTimeOut() {
        showToast("OTP Time out")
    }


    override fun onDestroy() {
        super.onDestroy()
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver)
        }
    }


    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
