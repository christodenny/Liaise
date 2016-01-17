package com.misc.liaise;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class EventActivity extends AppCompatActivity {

    private JSONObject event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        try {
            event = new JSONObject(getIntent().getStringExtra("json"));
        } catch (JSONException e) {
            Toast.makeText(null, "Can't parse JSON", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView eventTitle = (TextView) findViewById(R.id.textView);
        eventTitle.setText(getEventName());
        TextView eventDescription = (TextView) findViewById(R.id.textView2);
        eventDescription.setText(getEventDescription());
        TextView textstart = (TextView) findViewById(R.id.textView3);
        textstart.setText(getStart());
        TextView textend = (TextView) findViewById(R.id.textView4);
        textend.setText(getEnd());
        CheckBox subscribed = (CheckBox) findViewById(R.id.checkBox);
        subscribed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // add/remove user/event combo
                new SubscribeCheckboxTask(isChecked).execute((Void) null);
            }
        });
        Button mapsButton = (Button) findViewById(R.id.button);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + getLatitude() + "," + getLongitude()));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        int mode = Activity.MODE_PRIVATE;
        SharedPreferences preferences = getSharedPreferences(getString(R.string.login_preference), mode);
        if (!preferences.contains(getString(R.string.login_id))) {
            Intent intent = new Intent("logout");
            finish();
        }
    }

    public String getLongitude() {
        try {
            return Double.toString(event.getDouble("longitude"));
        } catch (JSONException e) {
            Toast.makeText(null, "Can't parse JSON for longitude!", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public String getLatitude() {
        try {
            return Double.toString(event.getDouble("latitude"));
        } catch (JSONException e) {
            Toast.makeText(null, "Can't parse JSON for latitude!", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public String getEventName() {
        try {
            return event.getString("name");
        } catch (JSONException e) {
            Toast.makeText(null, "Can't parse JSON for event name!", Toast.LENGTH_SHORT).show();
            return "";
        }
    }
    public String getStart() {
        try {
            return event.getString("start_time").substring(12, 16);
        } catch (JSONException e) {
            Toast.makeText(null, "Can't parse JSON for event name!", Toast.LENGTH_SHORT).show();
            return "";
        }
    }
    public String getEnd() {
        try {
            return event.getString("end_time").substring(12, 16);
        } catch (JSONException e) {
            Toast.makeText(null, "Can't parse JSON for event name!", Toast.LENGTH_SHORT).show();
            return "";
        }
    }
    public String getEventDescription() {
        try {
            return event.getString("description");
        } catch (JSONException e) {
            Toast.makeText(null, "Can't parse JSON for event description!", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    private class SubscribeCheckboxTask extends AsyncTask<Void, Void, Boolean> {

        private boolean isChecked;

        public SubscribeCheckboxTask(boolean checked) {
            isChecked = checked;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String endpoint;
            if (isChecked) {
                endpoint = "newpplevent";
            } else {
                // TODO: update when endpoint is ready
                return false;
            }
            int mode = Activity.MODE_PRIVATE;
            SharedPreferences preferences = getSharedPreferences(getString(R.string.login_preference), mode);
            int user_id = preferences.getInt(getString(R.string.login_id), -1);
            if (user_id == -1) {
                // we're not logged in???
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
            int event_id;
            try {
                event_id = event.getInt("id");
            } catch (JSONException e) {
                Toast.makeText(null, "Invalid Event JSON!", Toast.LENGTH_SHORT).show();
                return false;
            }
            HttpHelper.postStringRequest(endpoint, "ppl_id", Integer.toString(user_id), "event_id", Integer.toString(event_id));
            return true;
        }
    }
}
