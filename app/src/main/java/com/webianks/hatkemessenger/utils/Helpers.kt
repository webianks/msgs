package com.webianks.hatkemessenger.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.webianks.hatkemessenger.constants.Constants
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by R Ankit on 27-12-2016.
 */
object Helpers {
    fun getCertificateSHA1Fingerprint(context: Context): String? {
        val pm = context.packageManager
        val packageName = context.packageName
        val flags = PackageManager.GET_SIGNATURES
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = pm.getPackageInfo(packageName, flags)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val signatures = packageInfo!!.signatures
        val cert = signatures[0].toByteArray()
        val input: InputStream = ByteArrayInputStream(cert)
        var cf: CertificateFactory? = null
        try {
            cf = CertificateFactory.getInstance("X509")
        } catch (e: CertificateException) {
            e.printStackTrace()
        }
        var c: X509Certificate? = null
        try {
            c = cf!!.generateCertificate(input) as X509Certificate
        } catch (e: CertificateException) {
            e.printStackTrace()
        }
        var hexString: String? = null
        try {
            val md = MessageDigest.getInstance("SHA1")
            val publicKey = md.digest(c!!.encoded)
            hexString = byte2HexFormatted(publicKey)
        } catch (e1: NoSuchAlgorithmException) {
            e1.printStackTrace()
        } catch (e: CertificateEncodingException) {
            e.printStackTrace()
        }
        return hexString
    }

    private fun byte2HexFormatted(arr: ByteArray): String {
        val str = StringBuilder(arr.size * 2)
        for (i in arr.indices) {
            var h = Integer.toHexString(arr[i].toInt())
            val l = h.length
            if (l == 1) h = "0$h"
            if (l > 2) h = h.substring(l - 2, l)
            str.append(h.toUpperCase())
            if (i < arr.size - 1) str.append(':')
        }
        return str.toString()
    }

    fun getSMSJson(context: Context): String? {
        val sp = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        return sp.getString(Constants.SMS_JSON, null)
    }

    fun getDate(milliSeconds: Long): String {
        val dateFormat = "dd/MM/yyyy"
        val formatter = SimpleDateFormat(dateFormat)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}