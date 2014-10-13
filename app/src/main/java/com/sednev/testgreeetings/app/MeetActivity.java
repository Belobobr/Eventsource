package com.sednev.testgreeetings.app;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appspot.after_yesterday_706.meetings.Meetings;
import com.appspot.after_yesterday_706.meetings.model.Meet;
import com.appspot.after_yesterday_706.meetings.model.MeetRecord;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.sednev.testgreeetings.app.R;

import java.io.IOException;

public class MeetActivity extends Activity {

    private static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";

    public static final String MEET_ID_EXTRA = "MEET_ID_EXTRA";
    public static final String MEET_PLACE_EXTRA = "MEET_PLACE_EXTRA";
    public static final String MEET_NAME_EXTRA = "MEET_NAME_EXTRA";
    public static final String MEET_DESCRIPTION_EXTRA = "MEET_DESCRIPTION_EXTRA";

    static final int REQUEST_ACCOUNT_PICKER = 2;

    SharedPreferences settings;
    GoogleAccountCredential credential;

    String accountName;

    Meetings meetingsAuth;

    EditText placeEditText;
    EditText nameEditText;
    EditText descriptionEditText;

    Button addMeetButton;

    MeetRecord mMeetRecord;

    boolean mEditMeet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet);

        placeEditText = (EditText)findViewById(R.id.placeEditText);
        nameEditText = (EditText)findViewById(R.id.nameEditText);
        descriptionEditText = (EditText)findViewById(R.id.descriptionEditText);
        addMeetButton = (Button)findViewById(R.id.addMeetButton);

        if (getIntent().getExtras() != null) {
            Bundle extraBundle = getIntent().getExtras();
            mMeetRecord = new MeetRecord();
            mMeetRecord.setId(extraBundle.getLong(MEET_ID_EXTRA));
            mMeetRecord.setName(extraBundle.getString(MEET_NAME_EXTRA));
            mMeetRecord.setDescription(extraBundle.getString(MEET_DESCRIPTION_EXTRA));
            mMeetRecord.setPlace(extraBundle.getString(MEET_PLACE_EXTRA));

            placeEditText.setText(mMeetRecord.getPlace());
            descriptionEditText.setText(mMeetRecord.getDescription());
            nameEditText.setText(mMeetRecord.getName());

            mEditMeet = true;
        }

        addMeetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEditMeet) {
                    AddMeetAsyncTask addMeetAsyncTask = new AddMeetAsyncTask();
                    addMeetAsyncTask.execute();
                } else {
                    mMeetRecord = mMeetRecord.clone();
                    mMeetRecord.setName(nameEditText.getText().toString());
                    mMeetRecord.setDescription(descriptionEditText.getText().toString());
                    mMeetRecord.setPlace(placeEditText.getText().toString());
                    EditMeetAsyncTask editMeetAsyncTask = new EditMeetAsyncTask();
                    editMeetAsyncTask.execute(mMeetRecord);
                }
            }
        });

        settings = getSharedPreferences("TicTacToeSample", 0);
        credential = GoogleAccountCredential.usingAudience(this,
                "server:client_id:710786198471-gkv9vdrgd3sejjkddp6p9f5mk6j5bjak.apps.googleusercontent.com");
        setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));


        if (credential.getSelectedAccountName() != null) {
            // Already signed in, begin app!
        } else {
            // Not signed in, show login window or request an account.
            chooseAccount();
        }

        Meetings.Builder meetingsAuthBuilder = new Meetings.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), credential);
//                .setRootUrl("http://192.168.1.4:8080/_ah/api/");
        meetingsAuth = meetingsAuthBuilder.build();
    }

    // setSelectedAccountName definition
    private void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.commit();
        credential.setSelectedAccountName(accountName);
        this.accountName = accountName;
    }


    void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    String accountName =
                            data.getExtras().getString(
                                    AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        setSelectedAccountName(accountName);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        // User is authorized.
                    }
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.meet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class AddMeetAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Meet meet = new Meet();
            meet.setPlace(placeEditText.getText().toString());
            meet.setName(nameEditText.getText().toString());
            meet.setDescription(descriptionEditText.getText().toString());

            try {
                Meetings.MeetingsOperations.AddMeet addMeet = meetingsAuth.meetings().addMeet(meet);
                addMeet.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MeetActivity.this.finish();
        }
    }

    public class EditMeetAsyncTask extends AsyncTask<MeetRecord, Void, Void> {
        @Override
        protected Void doInBackground(MeetRecord... params) {
            try {
                Meetings.MeetingsOperations.EditMeet editMeet = meetingsAuth.meetings().editMeet(params[0]);
                editMeet.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MeetActivity.this.finish();
        }
    }

}
