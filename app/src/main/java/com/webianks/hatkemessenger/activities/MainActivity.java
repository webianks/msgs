package com.webianks.hatkemessenger.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webianks.hatkemessenger.R;
import com.webianks.hatkemessenger.Sms;
import com.webianks.hatkemessenger.adapters.AllConversationAdapter;
import com.webianks.hatkemessenger.adapters.ItemCLickListener;
import com.webianks.hatkemessenger.constants.Constants;
import com.webianks.hatkemessenger.constants.SmsContract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ItemCLickListener, LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private AllConversationAdapter allConversationAdapter;
    private String TAG = MainActivity.class.getSimpleName();
    private String mCurFilter;
    private List<Sms> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        fab = (FloatingActionButton) findViewById(R.id.fab_new);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        fab.setOnClickListener(this);
    }


    private void setRecyclerView(List<Sms> data) {
        allConversationAdapter = new AllConversationAdapter(this, data);
        allConversationAdapter.setItemClickListener(this);
        recyclerView.setAdapter(allConversationAdapter);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_new:

                startActivity(new Intent(this, NewSMSActivity.class));
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.ic_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ic_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        Constants.MY_PERMISSIONS_REQUEST_READ_SMS);
            }
        } else
            getSupportLoaderManager().initLoader(Constants.ALL_SMS_LOADER, null, this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getSupportLoaderManager().initLoader(Constants.ALL_SMS_LOADER, null, this);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Can't access messages.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }


    @Override
    public void itemClicked(int position, String contact) {
        Intent intent = new Intent(this, SmsDetailedView.class);
        intent.putExtra(Constants.CONTACT_NAME, contact);
        startActivity(intent);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = null;
        String[] selectionArgs = null;

        if (mCurFilter != null) {
            selection = SmsContract.SMS_SELECTION_SEARCH;
            selectionArgs = new String[]{"%" + mCurFilter + "%", "%" + mCurFilter + "%"};
        }

        return new CursorLoader(this,
                SmsContract.ALL_SMS_URI,
                null,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null && cursor.getCount() > 0) {

            //allConversationAdapter.swapCursor(cursor);
            getAllSmsToFile(cursor);

        } else {
            //no sms
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        data = null;
        allConversationAdapter.notifyDataSetChanged();

        //allConversationAdapter.swapCursor(null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //Log.d(TAG, "onQueryTextSubmit: " + query);
        mCurFilter = !TextUtils.isEmpty(query) ? query : null;
        getSupportLoaderManager().restartLoader(Constants.ALL_SMS_LOADER, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //Log.d(TAG, "onQueryTextChange: " + newText);
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getSupportLoaderManager().restartLoader(Constants.ALL_SMS_LOADER, null, this);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportLoaderManager().destroyLoader(Constants.ALL_SMS_LOADER);
    }


    public void getAllSmsToFile(Cursor c) {

        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = null;
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                try {
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

                } catch (Exception e) {

                } finally {
                    lstSms.add(objSms);
                    c.moveToNext();
                }
            }
        }
        c.close();

        data = lstSms;
        setRecyclerView(data);

        convertToJson(lstSms);

    }

    private void convertToJson(List<Sms> lstSms) {

        Type listType = new TypeToken<List<Sms>>() {
        }.getType();
        Gson gson = new Gson();
        String json = gson.toJson(lstSms, listType);

        SharedPreferences sp = getSharedPreferences(Constants.PREF_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.SMS_JSON,json);
        editor.apply();
        //List<String> target2 = gson.fromJson(json, listType);
        //Log.d(TAG, json);

    }


}
