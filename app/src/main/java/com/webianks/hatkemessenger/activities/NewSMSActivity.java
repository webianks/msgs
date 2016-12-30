package com.webianks.hatkemessenger.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.webianks.hatkemessenger.R;

/**
 * Created by R Ankit on 24-12-2016.
 */

public class NewSMSActivity extends AppCompatActivity implements View.OnClickListener {


    private Button sendBtn;
    private EditText txtphoneNo;
    private EditText txtMessage;
    private String phoneNo;
    private String message;

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

                sendSMSNow();

                break;
        }
    }

    private void sendSMSNow() {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);

        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();

    }


}
