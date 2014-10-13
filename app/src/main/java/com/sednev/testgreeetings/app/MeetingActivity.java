package com.sednev.testgreeetings.app;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.after_yesterday_706.helloworld.Helloworld;
import com.appspot.after_yesterday_706.meetings.Meetings;
import com.appspot.after_yesterday_706.meetings.model.CollectionResponseMeetRecord;
import com.appspot.after_yesterday_706.meetings.model.MeetFilter;
import com.appspot.after_yesterday_706.meetings.model.MeetRecord;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.sednev.testgreeetings.app.R;

import java.io.IOException;
import java.util.List;

public class MeetingActivity extends Activity {

    private static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";

    static final int REQUEST_ACCOUNT_PICKER = 2;

    SharedPreferences settings;
    GoogleAccountCredential credential;

    String accountName;

    Meetings meetingsAuth;

    ListView mListView;

    MeetingListAdapter mMeetingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        mListView = (ListView)findViewById(R.id.listView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MeetRecord meetRecord = mMeetingListAdapter.getItem(position);
                Intent intent = new Intent(MeetingActivity.this, MeetActivity.class);
                intent.putExtra(MeetActivity.MEET_ID_EXTRA, meetRecord.getId());
                intent.putExtra(MeetActivity.MEET_NAME_EXTRA, meetRecord.getName());
                intent.putExtra(MeetActivity.MEET_DESCRIPTION_EXTRA, meetRecord.getDescription());
                intent.putExtra(MeetActivity.MEET_PLACE_EXTRA, meetRecord.getPlace());
                startActivity(intent);
            }
        });

        mMeetingListAdapter = new MeetingListAdapter(this, null);

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

    @Override
    protected void onResume() {
        super.onResume();
        GetMeetingListAsyncTask getMeetingListAsyncTask = new GetMeetingListAsyncTask();
        GetMeetingListParams getMeetingListParams = new GetMeetingListParams(10, new MeetFilter());
        getMeetingListAsyncTask.execute(getMeetingListParams);
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
        getMenuInflater().inflate(R.menu.meeting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.addMeet) {
            Intent intent = new Intent(this, MeetActivity.class);  
            startActivity(intent);
            return true;
        }



        return super.onOptionsItemSelected(item);


    }

    private static class GetMeetingListParams {
        private int mCount;
        private MeetFilter mMeetFilter;

        private GetMeetingListParams(int count, MeetFilter meetFilter) {
            mCount = count;
            mMeetFilter = meetFilter;
        }

        public int getCount() {
            return mCount;
        }

        public MeetFilter getMeetFilter() {
            return mMeetFilter;
        }
    }

    private class GetMeetingListAsyncTask extends AsyncTask<GetMeetingListParams, Void, CollectionResponseMeetRecord> {
        @Override
        protected CollectionResponseMeetRecord doInBackground(GetMeetingListParams... params) {
            try {
                Meetings.MeetingsOperations.List list = meetingsAuth.meetings().list(50, new MeetFilter());
                CollectionResponseMeetRecord collectionResponseMeetRecord = list.execute();
                return collectionResponseMeetRecord;
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CollectionResponseMeetRecord collectionResponseMeetRecord) {
            if (collectionResponseMeetRecord != null) {
                List<MeetRecord> meetingList = collectionResponseMeetRecord.getItems();
                if (meetingList != null) {
                    mMeetingListAdapter = new MeetingListAdapter(MeetingActivity.this, meetingList);
                    mListView.setAdapter(mMeetingListAdapter);
                }
            } else {
                Toast errorLoadingMeetingList = Toast.makeText(MeetingActivity.this, "Не удалось загрузить список встреч", Toast.LENGTH_LONG);
                errorLoadingMeetingList.show();
            }
        }
    }

    private class MeetingListAdapter extends ArrayAdapter<MeetRecord> {

        private MeetingListAdapter(Context context, List<MeetRecord> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linearLayout;
            if (convertView == null)  {
                linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_meet, parent, false);
            } else {
                linearLayout = (LinearLayout)convertView;
            }

            TextView nameTextView = (TextView)linearLayout.findViewById(R.id.nameTextView);
            TextView placeTextView = (TextView)linearLayout.findViewById(R.id.placeTextView);
            TextView descriptionTextView = (TextView)linearLayout.findViewById(R.id.descriptionTextView);

            MeetRecord meetRecord = getItem(position);

            nameTextView.setText(meetRecord.getName());
            placeTextView.setText(meetRecord.getPlace());
            descriptionTextView.setText(meetRecord.getDescription());

            return linearLayout;
        }
    }

}
