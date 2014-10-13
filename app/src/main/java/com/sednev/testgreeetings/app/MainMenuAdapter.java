package com.sednev.testgreeetings.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appspot.after_yesterday_706.meetings.model.MeetRecord;

/**
 * Created by Newshka on 12.10.2014.
 */
public class MainMenuAdapter extends ArrayAdapter<String> {

    LayoutInflater mLayoutInflater;

    public MainMenuAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout linearLayout;
        if (convertView == null)  {
            linearLayout = (LinearLayout)mLayoutInflater.inflate(R.layout.layout_main_menu, parent, false);
        } else {
            linearLayout = (LinearLayout)convertView;
        }

        ImageView iconImageView = (ImageView)linearLayout.findViewById(R.id.icon);
        TextView textView  = (TextView)linearLayout.findViewById(R.id.title);
        View separator = linearLayout.findViewById(R.id.separator);
        textView.setText(getItem(position));

        if (position == getCount() - 1) {
            separator.setVisibility(View.INVISIBLE);
        } else {
            separator.setVisibility(View.VISIBLE);
        }

        return linearLayout;
    }
}
