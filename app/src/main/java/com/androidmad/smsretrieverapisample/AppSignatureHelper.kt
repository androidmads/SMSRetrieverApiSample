package com.androidmad.smsretrieverapisample

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class AppSignatureHelper(context: Context) : ContextWrapper(context) {

    /**
     * Get all the app signatures for the current package
     *
     * @return
     */
    // Get all package signatures for the current package
    // For each signature create a compatible hash
    val appSignatures: ArrayList<String>
        @SuppressLint("PackageManagerGetSignatures")
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        get() {
            val appCodes = ArrayList<String>()

            try {
                val packageName = packageName
                val packageManager = packageManager
                val signatures = packageManager.getPackageInfo(packageName,
                        PackageManager.GET_SIGNATURES).signatures
                signatures
                        .mapNotNull { hash(packageName, it.toCharsString()) }
                        .mapTo(appCodes) { String.format("%s", it) }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.v(TAG, "Unable to find package to obtain hash.", e)
            }

            return appCodes
        }

    companion object {
        val TAG = AppSignatureHelper::class.java.simpleName!!
        private val HASH_TYPE = "SHA-256"
        private val NUM_HASHED_BYTES = 9
        private val NUM_BASE64_CHAR = 11

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private fun hash(packageName: String, signature: String): String? {
            val appInfo = packageName + " " + signature
            try {
                val messageDigest = MessageDigest.getInstance(HASH_TYPE)
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
                var hashSignature = messageDigest.digest()

                // truncated into NUM_HASHED_BYTES
                hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
                // encode into Base64
                var base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
                base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

                Log.v(TAG + "sms_sample_test", String.format("pkg: %s -- hash: %s", packageName, base64Hash))
                return base64Hash
            } catch (e: NoSuchAlgorithmException) {
                Log.v(TAG + "sms_sample_test", "hash:NoSuchAlgorithm", e)
            }

            return null
        }
    }
}
