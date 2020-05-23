package com.webianks.hatkemessenger.activities

import android.Manifest
import android.app.SearchManager
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.webianks.hatkemessenger.R
import com.webianks.hatkemessenger.SMS
import com.webianks.hatkemessenger.activities.MainActivity
import com.webianks.hatkemessenger.adapters.AllConversationAdapter
import com.webianks.hatkemessenger.adapters.ItemCLickListener
import com.webianks.hatkemessenger.constants.Constants
import com.webianks.hatkemessenger.constants.SmsContract
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),
        View.OnClickListener,
        ItemCLickListener,
        LoaderManager.LoaderCallbacks<Cursor?>,
        SearchView.OnQueryTextListener {

    private var allConversationAdapter: AllConversationAdapter? = null
    private var mCurFilter: String? = null
    private var data: ArrayList<SMS>? = null
    private var mReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = linearLayoutManager
        fab_new.setOnClickListener(this)
        if (checkDefaultSettings()) checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS),
                        Constants.MY_PERMISSIONS_REQUEST_READ_SMS)
        }else {
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS),
                        Constants.MY_PERMISSIONS_REQUEST_READ_CONTACTS)
            }else{
                LoaderManager.getInstance(this).initLoader(Constants.ALL_SMS_LOADER, null, this)
            }
        }
    }

    private fun checkDefaultSettings(): Boolean {
        var isDefault = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isDefault = if (Telephony.Sms.getDefaultSmsPackage(this) != packageName) {
                val builder = MaterialAlertDialogBuilder(this@MainActivity)
                builder.setMessage("This app is not set as your default messaging app. Do you want to set it as default?")
                        .setCancelable(false)
                        .setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                            checkPermissions()
                        }
                        .setPositiveButton("Yes") { _: DialogInterface?, id: Int ->
                            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
                            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
                            startActivity(intent)
                            checkPermissions()
                        }
                builder.show()
                false
            } else true
        }
        return isDefault
    }

    private fun setRecyclerView(data: ArrayList<SMS>?) {
        allConversationAdapter = AllConversationAdapter(this, data)
        allConversationAdapter!!.setItemClickListener(this)
        recyclerview.adapter = allConversationAdapter
    }

    override fun onClick(view: View) {
        if (view.id == R.id.fab_new) {
            startActivity(Intent(this, NewSMSActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(
                "android.intent.action.MAIN")
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val new_sms = intent.getBooleanExtra("new_sms", false)
                if (new_sms) supportLoaderManager.restartLoader(Constants.ALL_SMS_LOADER, null, this@MainActivity)
            }
        }
        this.registerReceiver(mReceiver, intentFilter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.ic_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(true)
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.ic_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.MY_PERMISSIONS_REQUEST_READ_SMS -> {
                run {
                    if (grantResults.size > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(this,
                                        Manifest.permission.READ_CONTACTS)
                                != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                            Manifest.permission.READ_CONTACTS)) {
                            } else {
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS),
                                        Constants.MY_PERMISSIONS_REQUEST_READ_CONTACTS)
                            }
                        } else LoaderManager.getInstance(this@MainActivity).initLoader(Constants.ALL_SMS_LOADER, null, this)
                    } else {
                        Toast.makeText(applicationContext,
                                "Can't access messages.", Toast.LENGTH_LONG).show()
                        return
                    }
                }
                run {
                    if (grantResults.size > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        LoaderManager.getInstance(this@MainActivity).initLoader(Constants.ALL_SMS_LOADER, null, this)
                    } else {
                        Toast.makeText(applicationContext,
                                "Can't access messages.", Toast.LENGTH_LONG).show()
                        return
                    }
                }
            }
            Constants.MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoaderManager.getInstance(this@MainActivity).initLoader(Constants.ALL_SMS_LOADER, null, this)
                } else {
                    LoaderManager.getInstance(this@MainActivity).initLoader(Constants.ALL_SMS_LOADER, null, this)
                    return
                }
            }
        }
    }

    override fun itemClicked(color: Int, contact: String?, savedContactName: String?, id: Long, read: String?) {
        val intent = Intent(this, SmsDetailedView::class.java)
        intent.putExtra(Constants.CONTACT_NAME, contact)
        intent.putExtra(Constants.SAVED_CONTACT_NAME, savedContactName);
        intent.putExtra(Constants.COLOR, color)
        intent.putExtra(Constants.SMS_ID, id)
        intent.putExtra(Constants.READ, read)
        startActivity(intent)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        if (mCurFilter != null) {
            selection = SmsContract.SMS_SELECTION_SEARCH
            selectionArgs = arrayOf("%$mCurFilter%", "%$mCurFilter%")
        }
        return CursorLoader(this,
                SmsContract.ALL_SMS_URI,
                null,
                selection,
                selectionArgs,
                SmsContract.SORT_DESC)
    }

    override fun onLoadFinished(loader: Loader<Cursor?>, cursor: Cursor?) {
        progressBar!!.visibility = View.GONE
        if (cursor != null && cursor.count > 0) {

            //allConversationAdapter.swapCursor(cursor);
            getAllSmsToFile(cursor)
        } //no sms
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {
        data = null
        if (allConversationAdapter != null) allConversationAdapter!!.notifyDataSetChanged()
        //allConversationAdapter.swapCursor(null);
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        mCurFilter = if (!TextUtils.isEmpty(query)) query else null
        supportLoaderManager.restartLoader(Constants.ALL_SMS_LOADER, null, this)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        mCurFilter = if (!TextUtils.isEmpty(newText)) newText else null
        supportLoaderManager.restartLoader(Constants.ALL_SMS_LOADER, null, this)
        return true
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mReceiver)
        supportLoaderManager.destroyLoader(Constants.ALL_SMS_LOADER)
    }

    private fun getAllSmsToFile(c: Cursor) {
        val lstSms: ArrayList<SMS>?  = arrayListOf()
        lateinit var objSMS: SMS
        val totalSMS = c.count
        if (c.moveToFirst()) {
            for (i in 0 until totalSMS) {
                try {
                    objSMS = SMS()
                    objSMS.id = c.getLong(c.getColumnIndexOrThrow("_id"))
                    val num = c.getString(c.getColumnIndexOrThrow("address"))
                    objSMS.address = num
                    objSMS.msg = c.getString(c.getColumnIndexOrThrow("body"))
                    objSMS.readState = c.getString(c.getColumnIndex("read"))
                    objSMS.time = c.getLong(c.getColumnIndexOrThrow("date"))
                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                        objSMS.folderName = "inbox"
                    } else {
                        objSMS.folderName = "sent"
                    }
                } catch (e: Exception) {
                } finally {
                    lstSms?.add(objSMS)
                    c.moveToNext()
                }
            }
        }
        c.close()
        data = lstSms

        //Log.d(TAG,"Size before "+data.size());
        sortAndSetToRecycler(lstSms)
    }

    private fun sortAndSetToRecycler(lstSms: List<SMS>?) {
        val s: Set<SMS> = LinkedHashSet(lstSms)
        data = ArrayList(s)
        setRecyclerView(data)
        convertToJson(lstSms)
    }

    private fun convertToJson(lstSms: List<SMS>?) {
        val listType = object : TypeToken<List<SMS?>?>() {}.type
        val gson = Gson()
        val json = gson.toJson(lstSms, listType)
        val sp = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(Constants.SMS_JSON, json)
        editor.apply()
        //List<String> target2 = gson.fromJson(json, listType);
        //Log.d(TAG, json);
    }
}