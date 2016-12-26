package com.webianks.hatkemessenger.activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.webianks.hatkemessenger.R;
import com.webianks.hatkemessenger.constants.Constants;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().
                replace(R.id.container,
                        new MyPreferenceFragment()).commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        private SwitchPreference drivePref;

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            final SharedPreferences.Editor my_prefrence = PreferenceManager.
                    getDefaultSharedPreferences(getActivity()).edit();

            drivePref = (SwitchPreference) findPreference(Constants.BACKUP);
            drivePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    //backup dialog
                    return true;
                }
            });

        }

    }

}
