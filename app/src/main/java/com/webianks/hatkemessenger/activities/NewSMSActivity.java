package com.webianks.hatkemessenger.activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.webianks.hatkemessenger.R;
import com.webianks.hatkemessenger.receivers.DeliverReceiver;
import com.webianks.hatkemessenger.receivers.SentReceiver;

/**
 * Created by R Ankit on 24-12-2016.
 */

public class NewSMSActivity extends AppCompatActivity implements View.OnClickListener {


    private Button sendBtn;
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

        sendBtn = (Button) findViewById(R.id.btnSendSMS);
        txtphoneNo = (EditText) findViewById(R.id.editText);
        txtMessage = (EditText) findViewById(R.id.editText2);
        sendBtn.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSendSMS:

                phoneNo = txtphoneNo.getText().toString();
                message = txtMessage.getText().toString();

                if (phoneNo!=null && phoneNo.trim().length()>0){

                    if (message!=null && message.trim().length()>0){

                        sendSMSNow();

                    }else
                        txtMessage.setError(getString(R.string.please_write_message));

                }else
                    txtphoneNo.setError(getString(R.string.please_write_number));

                break;
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

}
