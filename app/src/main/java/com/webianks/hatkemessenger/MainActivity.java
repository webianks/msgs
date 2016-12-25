package com.webianks.hatkemessenger;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.webianks.hatkemessenger.adapters.AllConversationAdapters;
import com.webianks.hatkemessenger.adapters.ItemCLickListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ItemCLickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private int ALL_SMS_LOADER = 123;
    private AllConversationAdapters allConversationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setRecyclerView(null);

    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        fab = (FloatingActionButton) findViewById(R.id.fab_new);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        fab.setOnClickListener(this);
    }

   /* public List<Sms> getAllSms() throws Exception {

        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new Sms();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c
                        .getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");
                }

                lstSms.add(objSms);
                c.moveToNext();
            }
        } else {
            //no SMS to show
        }
        c.close();



        return lstSms;
    }*/

    private void setRecyclerView(Cursor cursor) {
        allConversationAdapter = new AllConversationAdapters(this, cursor);
        allConversationAdapter.setItemClickListener(this);
        recyclerView.setAdapter(allConversationAdapter);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_new:

                startActivity(new Intent(this, SendSMSActivity.class));
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(ALL_SMS_LOADER, null, this);
    }

    @Override
    public void itemClicked(int position) {
        startActivity(new Intent(this, SmsDetailedView.class));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri sms_uri = Uri.parse("content://sms/");

        return new CursorLoader(this,
                sms_uri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null && cursor.getCount() > 0) {
            allConversationAdapter.swapCursor(cursor);
        } else {
            //no sms
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        allConversationAdapter.swapCursor(null);
    }
}
