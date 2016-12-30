package com.webianks.hatkemessenger.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.webianks.hatkemessenger.R;
import com.webianks.hatkemessenger.adapters.SingleGroupAdapter;
import com.webianks.hatkemessenger.constants.Constants;
import com.webianks.hatkemessenger.constants.SmsContract;
import com.webianks.hatkemessenger.receivers.DeliverReceiver;
import com.webianks.hatkemessenger.receivers.SentReceiver;
import com.webianks.hatkemessenger.services.UpdateSMSService;

public class SmsDetailedView extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private String contact;
    private SingleGroupAdapter singleGroupAdapter;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageView btSend;
    private String message;
    private boolean from_reciever;
    private long _Id;
    private int color;
    private String read = "1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_detailed_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }


    private void init() {

        Intent intent = getIntent();


        contact = intent.getStringExtra(Constants.CONTACT_NAME);
        _Id = intent.getLongExtra(Constants.SMS_ID,-123);
        color = intent.getIntExtra(Constants.COLOR,0);
        read = intent.getStringExtra(Constants.READ);

        from_reciever = intent.getBooleanExtra(Constants.FROM_SMS_RECIEVER, false);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(contact);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (ImageView) findViewById(R.id.btSend);

        btSend.setOnClickListener(this);

        setRecyclerView(null);

        if (read!=null && read.equals("0"))
            setReadSMS();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                if (from_reciever)
                    startActivity(new Intent(this, MainActivity.class));

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setRecyclerView(Cursor cursor) {
        singleGroupAdapter = new SingleGroupAdapter(this, cursor,color);
        recyclerView.setAdapter(singleGroupAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(Constants.CONVERSATION_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] selectionArgs = {contact};

        return new CursorLoader(this,
                SmsContract.ALL_SMS_URI,
                null,
                SmsContract.SMS_SELECTION,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null && cursor.getCount() > 0) {
            singleGroupAdapter.swapCursor(cursor);
        } else {
            //no sms
        }
    }


    private void setReadSMS() {

        Intent intent = new Intent(this, UpdateSMSService.class);
        intent.putExtra("id", _Id);
        startService(intent);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        singleGroupAdapter.swapCursor(null);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btSend:
                sendSMSMessage();
                break;
        }
    }

    protected void sendSMSMessage() {

        message = etMessage.getText().toString();

        if (message!=null && message.trim().length()>0)
            requestPermisions();
        else
            etMessage.setError(getString(R.string.please_write_message));

    }

    private void requestPermisions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        Constants.MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }else{
            sendSMSNow();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMSNow();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    private void sendSMSNow() {

        BroadcastReceiver sendBroadcastReceiver = new SentReceiver();
        BroadcastReceiver deliveryBroadcastReciever = new DeliverReceiver();

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
        registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));

        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(contact, null, message, sentPI, deliveredPI);
        }catch (Exception e){
            Toast.makeText(this,getString(R.string.cant_send),Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {

        if (from_reciever) {
            startActivity(new Intent(this, MainActivity.class));
        } else
            super.onBackPressed();
    }
}
