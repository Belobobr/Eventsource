package com.sednev.testgreeetings.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by Newshka on 13.10.2014.
 */
public class ExitDialog extends DialogPreference {

    private static final String EVENT_SOURCE_MAIN_PREFERENCE = "EVENT_SOURCE_MAIN_PREFERENCE";

    private static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";


    SharedPreferences settings;

    public ExitDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ExitDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected View onCreateDialogView() {
        settings = getContext().getSharedPreferences(EVENT_SOURCE_MAIN_PREFERENCE, 0);

        return super.onCreateDialogView();

    }

    @Override
    protected void showDialog(Bundle bundle) {
        super.showDialog(bundle);
        Button pos = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Сбросим настройки аккаунта, что бы позволить пользователю выбрать новый =)
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(PREF_ACCOUNT_NAME, null);
                editor.commit();

                //Нельзя проставлять флаг no_history - в  это случае даже onActivityResult не будет работать =)
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
    }

}
