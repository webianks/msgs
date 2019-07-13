package com.webianks.hatkemessenger.activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.webianks.hatkemessenger.R;
import com.webianks.hatkemessenger.constants.Constants;
import com.webianks.hatkemessenger.receivers.DeliverReceiver;
import com.webianks.hatkemessenger.receivers.SentReceiver;

/**
 * Created by R Ankit on 24-12-2016.
 */

public class NewSMSActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText txtphoneNo;
    private EditText txtMessage;
    private String phoneNo;
    private String message;
    BroadcastReceiver sendBroadcastReceiver = new SentReceiver();
    BroadcastReceiver deliveryBroadcastReciever = new DeliverReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_sms_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {

        Button sendBtn = findViewById(R.id.btnSendSMS);
        txtphoneNo =  findViewById(R.id.editText);
        txtMessage =  findViewById(R.id.editText2);
        ImageButton contact = findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent,Constants.RESULT_PICK_CONTACT);
            }
        });
        sendBtn.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSendSMS) {
            phoneNo = txtphoneNo.getText().toString();
            message = txtMessage.getText().toString();

            if (phoneNo != null && phoneNo.trim().length() > 0) {

                if (message != null && message.trim().length() > 0) {

                    sendSMSNow();

                } else
                    txtMessage.setError(getString(R.string.please_write_message));

            } else
                txtphoneNo.setError(getString(R.string.please_write_number));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            unregisterReceiver(sendBroadcastReceiver);
            unregisterReceiver(deliveryBroadcastReciever);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSMSNow() {

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
        registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNo, null, message, sentPI, deliveredPI);

    }

    public void pickContact(View v) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, Constants.RESULT_PICK_CONTACT);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.RESULT_PICK_CONTACT)
                contactPicked(data);
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
}

    private void contactPicked(Intent data) {

        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            txtphoneNo.setText(phoneNo);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
