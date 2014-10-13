package com.sednev.testgreeetings.app;


import com.appspot.after_yesterday_706.helloworld.model.HelloGreeting;
import com.google.api.client.util.Lists;

import java.util.ArrayList;

/* Dummy Application class that can hold static data for use only in sample applications.
*
* TODO(developer): Implement a proper data storage technique for your application.
*/

public class Application extends android.app.Application {
    ArrayList<HelloGreeting> greetings = Lists.newArrayList();

    @Override
    public void onCreate() {
        super.onCreate();

        //Тут регистрируем CloudMessaging
    }
}