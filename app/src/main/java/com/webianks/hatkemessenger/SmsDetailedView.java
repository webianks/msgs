package com.webianks.hatkemessenger;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.webianks.hatkemessenger.constants.Constants;
import com.webianks.hatkemessenger.constants.SmsContract;

public class SmsDetailedView extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(contact);
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null) {
            //Toast.makeText(this, "This conversation has " + data.getCount() + " sms", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
