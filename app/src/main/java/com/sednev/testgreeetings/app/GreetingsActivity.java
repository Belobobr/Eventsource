package com.sednev.testgreeetings.app;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.after_yesterday_706.helloworld.Helloworld;
import com.appspot.after_yesterday_706.helloworld.model.HelloGreeting;
import com.appspot.after_yesterday_706.helloworld.model.HelloGreetingCollection;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.util.Strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GreetingsActivity extends Activity {
    private static final String TAG = "GreetingsActivity";
    private static final String LOG_TAG = "MainActivity";
    private GreetingsDataAdapter mListAdapter;

    private static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";

    SharedPreferences settings;
    GoogleAccountCredential credential;

//    Button helloButton;
//    Button authButton;
//    TextView mTextView;

    //Сервис который позволяет обращаться к нашему api
    Helloworld helloworld;
    Helloworld mHelloworldAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);

        // Prevent the keyboard from being visible upon startup.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ListView listView = (ListView) findViewById(R.id.greetings_list_view);
        mListAdapter = new GreetingsDataAdapter((Application) getApplication());
        listView.setAdapter(mListAdapter);

//        helloButton = (Button)findViewById(R.id.helloButton);
//        helloButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                HelloAsyncTask helloAsyncTask = new HelloAsyncTask();
//                helloAsyncTask.execute();
//            }
//        });
//
//        authButton = (Button)findViewById(R.id.authButton);
//        authButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AuthAsyncTask authAsyncTask = new AuthAsyncTask();
//                authAsyncTask.execute();
//            }
//        });
//
//        mTextView = (TextView)findViewById(R.id.textView);

        // Inside your Activity class onCreate method
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

        Helloworld.Builder builder = new Helloworld.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
//                .setRootUrl("http://192.168.1.4:8080/_ah/api/")
//                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                    @Override
//                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                        abstractGoogleClientRequest.setDisableGZipContent(true);
//                    }
//                });
        helloworld = builder.build();

        Helloworld.Builder builderAuth = new Helloworld.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), credential);
//                .setRootUrl("http://192.168.1.4:8080/_ah/api/");
        mHelloworldAuth = builderAuth.build();

    }

    String accountName;

    // setSelectedAccountName definition
    private void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.commit();
        credential.setSelectedAccountName(accountName);
        this.accountName = accountName;
    }

    static final int REQUEST_ACCOUNT_PICKER = 2;

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
        getMenuInflater().inflate(R.menu.greetings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent meetingActivityIntent = new Intent(this, MeetingActivity.class);
            startActivity(meetingActivityIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private class HelloAsyncTask extends  AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                HelloGreetingCollection helloGreetingCollection = helloworld.greetings().listGreeting().execute();

                StringBuilder stringBuilder = new StringBuilder();

                for (HelloGreeting helloGreeting :  helloGreetingCollection.getItems()) {
                    stringBuilder.append(helloGreeting.getMessage());
                }

                return stringBuilder.toString();
            } catch (IOException exception) {
                Log.e(TAG, "Не удалось получить список приветсвий" + exception);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);

            //mTextView.setText(string);
        }


    }

    private class AuthAsyncTask extends  AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                HelloGreeting helloGreeting = mHelloworldAuth.greetings().authed().execute();

                return helloGreeting.getMessage();
            } catch (IOException exception) {
                Log.e(TAG, "Не удалось получить список приветсвий" + exception);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);

            //mTextView.setText(string);
        }
    }


    /**
     * Simple use of an ArrayAdapter but we're using a static class to ensure no references to the
     * Activity exists.
     */
    static class GreetingsDataAdapter extends ArrayAdapter {
        GreetingsDataAdapter(Application application) {
            super(application.getApplicationContext(), android.R.layout.simple_list_item_1,
                    application.greetings);
        }

        void replaceData(HelloGreeting[] greetings) {
            clear();
            for (HelloGreeting greeting : greetings) {
                add(greeting);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);

            HelloGreeting greeting = (HelloGreeting)this.getItem(position);

            StringBuilder sb = new StringBuilder();

            Set<String> fields = greeting.keySet();
            boolean firstLoop = true;
            for (String fieldName : fields) {
                // Append next line chars to 2.. loop runs.
                if (firstLoop) {
                    firstLoop = false;
                } else {
                    sb.append("\n");
                }

                sb.append(fieldName)
                        .append(": ")
                        .append(greeting.get(fieldName));
            }

            view.setText(sb.toString());
            return view;
        }
    }

    /**
     * This method is invoked when the "Get Greeting" button is clicked. See activity_main.xml for
     * the dynamic reference to this method.
     */
    public void onClickGetGreeting(View view) {
        View rootView = view.getRootView();
        TextView greetingIdInputTV = (TextView)rootView.findViewById(R.id.greeting_id_edit_text);
        if (greetingIdInputTV.getText()==null ||
                Strings.isNullOrEmpty(greetingIdInputTV.getText().toString())) {
            Toast.makeText(this, "Input a Greeting ID", Toast.LENGTH_SHORT).show();
            return;
        };

        String greetingIdString = greetingIdInputTV.getText().toString();
        int greetingId = Integer.parseInt(greetingIdString);

        // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
        // static-inner or top-level classes to prevent memory leak issues.
        // @see http://goo.gl/fN1fuE @26:00 for a great explanation.
        AsyncTask<Integer, Void, HelloGreeting> getAndDisplayGreeting =
                new AsyncTask<Integer, Void, HelloGreeting> () {
                    @Override
                    protected HelloGreeting doInBackground(Integer... integers) {
                        // Retrieve service handle.
                        Helloworld apiServiceHandle = helloworld;

                        try {
                            Helloworld.Greetings.GetGreeting getGreetingCommand = apiServiceHandle.greetings().getGreeting(integers[0]);
                            HelloGreeting greeting = getGreetingCommand.execute();
                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(HelloGreeting greeting) {
                        if (greeting!=null) {
                            displayGreetings(greeting);
                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };

        getAndDisplayGreeting.execute(greetingId);
    }

    private void displayGreetings(HelloGreeting... greetings) {
        String msg;
        if (greetings==null || greetings.length < 1) {
            msg = "Greeting was not present";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } else {
            Log.d(LOG_TAG, "Displaying " + greetings.length + " greetings.");

            List<HelloGreeting> greetingsList = Arrays.asList(greetings);
            mListAdapter.replaceData(greetings);
        }
    }

    public void onClickSendGreetings(View view) {
        View rootView = view.getRootView();

        TextView greetingCountInputTV = (TextView)rootView.findViewById(R.id.greeting_count_edit_text);
        if (greetingCountInputTV.getText()==null ||
                Strings.isNullOrEmpty(greetingCountInputTV.getText().toString())) {
            Toast.makeText(this, "Input a Greeting Count", Toast.LENGTH_SHORT).show();
            return;
        };

        String greetingCountString = greetingCountInputTV.getText().toString();
        final int greetingCount = Integer.parseInt(greetingCountString);

        TextView greetingTextInputTV = (TextView)rootView.findViewById(R.id.greeting_text_edit_text);
        if (greetingTextInputTV.getText()==null ||
                Strings.isNullOrEmpty(greetingTextInputTV.getText().toString())) {
            Toast.makeText(this, "Input a Greeting Message", Toast.LENGTH_SHORT).show();
            return;
        };

        final String greetingMessageString = greetingTextInputTV.getText().toString();

        AsyncTask<Void, Void, HelloGreeting> sendGreetings = new AsyncTask<Void, Void, HelloGreeting> () {
            @Override
            protected HelloGreeting doInBackground(Void... unused) {
                // Retrieve service handle.
                Helloworld apiServiceHandle = helloworld;

                try {
                    HelloGreeting greeting = new HelloGreeting();
                    greeting.setMessage(greetingMessageString);

                    Helloworld.Greetings.Multiply multiplyGreetingCommand = apiServiceHandle.greetings().multiply(greetingCount,
                            greeting);
                    greeting = multiplyGreetingCommand.execute();
                    return greeting;
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Exception during API call", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(HelloGreeting greeting) {
                if (greeting!=null) {
                    displayGreetings(greeting);
                } else {
                    Log.e(LOG_TAG, "No greetings were returned by the API.");
                }
            }
        };

        sendGreetings.execute((Void)null);
    }

    public void onClickGetAuthenticatedGreeting(View unused) {
        if (!isSignedIn()) {
            Toast.makeText(this, "You must sign in for this action.", Toast.LENGTH_LONG).show();
            return;
        }

        AsyncTask<Void, Void, HelloGreeting> getAuthedGreetingAndDisplay =
                new AsyncTask<Void, Void, HelloGreeting> () {
                    @Override
                    protected HelloGreeting doInBackground(Void... unused) {
                        if (!isSignedIn()) {
                            return null;
                        };

                        // Retrieve service handle using credential since this is an authenticated call.
                        Helloworld apiServiceHandle = mHelloworldAuth;

                        try {
                            Helloworld.Greetings.Authed getAuthedGreetingCommand = apiServiceHandle.greetings().authed();
                            HelloGreeting greeting = getAuthedGreetingCommand.execute();
                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(HelloGreeting greeting) {
                        if (greeting!=null) {
                            displayGreetings(greeting);
                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };

        getAuthedGreetingAndDisplay.execute((Void)null);
    }

    private boolean isSignedIn() {
        if (settings.getString(PREF_ACCOUNT_NAME, null) == null)
            return false;
        else
            return true;

    }

    public void onClickSignIn(View view) {
    }


    public void onClickRegistrationGCM(View view) {
         new GcmRegistrationAsyncTask().execute(this);
    }

    public void onSendMessage(View view) {
        new GcmSendMessage().execute();
    }

}

