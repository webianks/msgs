package com.webianks.hatkemessenger.activities

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.webianks.hatkemessenger.R
import com.webianks.hatkemessenger.constants.Constants
import com.webianks.hatkemessenger.receivers.DeliverReceiver
import com.webianks.hatkemessenger.receivers.SentReceiver

class NewSMSActivity : AppCompatActivity(), View.OnClickListener {

    private var txtphoneNo: EditText? = null
    private var txtMessage: EditText? = null
    private var phoneNo: String? = null
    private var message: String? = null

    private var sendBroadcastReceiver: BroadcastReceiver = SentReceiver()
    private var deliveryBroadcastReceiver: BroadcastReceiver = DeliverReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.send_sms_activity)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        init()
    }

    private fun init() {
        val sendBtn = findViewById<Button>(R.id.btnSendSMS)
        txtphoneNo = findViewById(R.id.editText)
        txtMessage = findViewById(R.id.editText2)
        val contact = findViewById<ImageButton>(R.id.contact)
        contact.setOnClickListener { v: View? ->
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent, Constants.RESULT_PICK_CONTACT)
        }
        sendBtn.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btnSendSMS) {
            phoneNo = txtphoneNo!!.text.toString()
            message = txtMessage!!.text.toString()
            if (phoneNo != null && phoneNo!!.trim { it <= ' ' }.isNotEmpty()) {
                if (message != null && message!!.trim { it <= ' ' }.isNotEmpty()) {
                    sendSMSNow()
                } else txtMessage!!.error = getString(R.string.please_write_message)
            } else txtphoneNo!!.error = getString(R.string.please_write_number)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(sendBroadcastReceiver)
            unregisterReceiver(deliveryBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendSMSNow() {
        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"
        val sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), 0)
        val deliveredPI = PendingIntent.getBroadcast(this, 0, Intent(DELIVERED), 0)
        registerReceiver(sendBroadcastReceiver, IntentFilter(SENT))
        registerReceiver(deliveryBroadcastReceiver, IntentFilter(DELIVERED))
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(phoneNo, null, message, sentPI, deliveredPI)
    }

    fun pickContact(v: View?) {
        val contactPickerIntent = Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(contactPickerIntent, Constants.RESULT_PICK_CONTACT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RESULT_PICK_CONTACT) contactPicked(data)
        } else {
            Log.e("MainActivity", "Failed to pick contact")
        }
    }

    private fun contactPicked(data: Intent?) {
        val cursor: Cursor?
        try {
            val phoneNo: String?
            var name: String? = null
            val uri = data!!.data

            cursor = contentResolver.query(uri!!, null, null, null, null)
            cursor!!.moveToFirst()

            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            phoneNo = cursor.getString(phoneIndex)
            name = cursor.getString(nameIndex)
            txtphoneNo!!.setText(phoneNo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}