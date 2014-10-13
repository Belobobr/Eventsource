package com.sednev.testgreeetings.app;

import android.os.AsyncTask;

import com.appspot.after_yesterday_706.messaging.Messaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

/**
 * Created by Newshka on 25.09.2014.
 */
public class GcmSendMessage extends AsyncTask<String, Void, Void> {

    private static Messaging mMessaging = null;

    @Override
    protected Void doInBackground(String[] params) {
        if (mMessaging == null) {
            Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null);

            mMessaging = builder.build();
        }

        try {
            Messaging.SendMessage sendMessage = mMessaging.sendMessage("Закхардкоженное сообщение");

            sendMessage.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return  null;

    }

    @Override
    protected void onPostExecute(Void o) {
        super.onPostExecute(o);
    }
}
