package com.sednev.testgreeetings.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Created by Newshka on 12.10.2014.
 */
public class EventSourceActivity extends Activity {

    private static final String EVENT_SOURCE_MAIN_PREFERENCE = "EVENT_SOURCE_MAIN_PREFERENCE";

    private static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";

    static final int REQUEST_ACCOUNT_PICKER = 2;

    SharedPreferences settings;
    GoogleAccountCredential credential;

    String accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences(EVENT_SOURCE_MAIN_PREFERENCE, 0);
        credential = GoogleAccountCredential.usingAudience(this,
                "server:client_id:710786198471-gkv9vdrgd3sejjkddp6p9f5mk6j5bjak.apps.googleusercontent.com");
        String accountName = settings.getString(PREF_ACCOUNT_NAME, null);
        setSelectedAccountName(accountName);


        if (credential.getSelectedAccountName() != null) {
            // Already signed in, begin app!
        } else {
            // Not signed in, show login window or request an account.
            //Делаем редирект на окно логина
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    // setSelectedAccountName definition
    private void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.commit();
        credential.setSelectedAccountName(accountName);
        this.accountName = accountName;
    }
}
