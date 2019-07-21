package com.androidmad.smsretrieverapisample

import android.app.Application
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log

/**
 * Created by Mushtaq
 * on 14-07-2019.
 */
public class MyApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate() {
        super.onCreate()

        // This code requires one time to get Hash keys do comment and share key
        val appSignature = AppSignatureHelper(this)
        Log.v("AppSignature", appSignature.appSignatures.toString())
    }
}