package com.misc.liaise;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scott_000 on 1/16/2016.
 */
public class MainActivity_schedulefragment extends Fragment {

    private List<JSONObject> events = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.activity_main1_schedule,container,false);
        // lookup main event schedule
        GetEventsTask task = new GetEventsTask(v);
        task.execute((Void) null);

        return v;
    }

    private class GetEventsTask extends AsyncTask<Void, Void, Boolean> {
        private View view;
        public GetEventsTask(View view) {
            this.view = view;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONArray eventObjs = HttpHelper.getJSONArrayRequest("events");
            if (eventObjs == null) {
                return false;
            }
            try {
                for (int i = 0; i < eventObjs.length(); i++) {
                    JSONObject eventObj = eventObjs.getJSONObject(i);
                    events.add(eventObj);
                }
            } catch (JSONException e) {
                Toast.makeText(null, "Can't parse JSON!", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout2);
            Log.d("Post", success.toString());
            Log.d("Post", events.size() + "");
            if (!events.isEmpty()) {
                layout.removeAllViews();
            }
            for (int i = 0; i < events.size(); i++) {
                final JSONObject event = events.get(i);
                String name;
                try {
                    name = event.getString("name");
                } catch (JSONException e) {
                    Log.e("JSON error", event.toString());
                    continue;
                }
                Log.d("debug", "Making a button");
                Button button = new Button(view.getContext());
                button.setId(i + 1);
                button.setText(name);
                button.setBackgroundColor(Color.rgb(255, 255, 255));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                layout.addView(button, params);
                final int id_ = button.getId();
                Button button1 = (Button) view.findViewById(id_);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), EventActivity.class);
                        intent.putExtra("json", event.toString());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}