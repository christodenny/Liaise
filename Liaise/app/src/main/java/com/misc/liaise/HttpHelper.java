package com.misc.liaise;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raymond on 1/17/16.
 */
public class HttpHelper {

    public static final String BASE_URL = "https://fierce-reaches-5317.herokuapp.com/";

    public static String httpRequest(HttpUriRequest request) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();
                out.close();
                //..more logic
                return responseString;
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch(IOException e) {
            Log.e("HTTP error", e.getMessage());
            return null;
        }
    }

    public static String getStringRequest(String endpoint) {
        return httpRequest(new HttpGet(BASE_URL + endpoint));
    }

    public static JSONArray getJSONArrayRequest(String endpoint) {
        try {
            return new JSONArray(getStringRequest(endpoint));
        } catch (JSONException e) {
            Log.e("JSON error", "Can't parse JSON!");
            return null;
        }
    }

    public static JSONObject getJSONObjectRequest(String endpoint) {
        try {
            return new JSONObject(getStringRequest(endpoint));
        } catch (JSONException e) {
            Log.e("JSON error", "Can't parse JSON!");
            return null;
        }
    }

    public static String postStringRequest(String endpoint, String... data) {
        String url = BASE_URL + endpoint;
        Log.d("url", url);
        HttpPost post = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<>();
        for (int i = 0; i < data.length; i += 2) {
            if (i + 1 < data.length) {
                list.add(new BasicNameValuePair(data[i], data[i + 1]));
            }
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(list));
        } catch (UnsupportedEncodingException e) {
            Log.e("Parse error", e.getMessage());
            return null;
        }
        return httpRequest(post);
    }

    public static JSONArray postJSONArrayRequest(String endpoint) {
        try {
            return new JSONArray(postStringRequest(endpoint));
        } catch (JSONException e) {
            Log.e("JSON error", "Can't parse JSON!");
            return null;
        }
    }

    public static JSONObject postJSONObjectRequest(String endpoint) {
        try {
            return new JSONObject(postStringRequest(endpoint));
        } catch (JSONException e) {
            Log.e("JSON error", "Can't parse JSON!");
            return null;
        }
    }
}
