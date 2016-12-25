package com.webianks.hatkemessenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.webianks.hatkemessenger.constants.Constants;

public class SmsDetailedView extends AppCompatActivity {

    private String contact;
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
}
