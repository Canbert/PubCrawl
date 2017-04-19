package com.rgu.scott1508551.pubcrawl;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonHttpRequest extends AsyncTask<String, Void, Void> {

    private String returnEntry;
    private boolean finished;

    public void readResponse(BufferedReader in) {
        String tmp = "";
        StringBuffer response = new StringBuffer();

        do {
            try {
                tmp = in.readLine();
            }
            catch (IOException ex) {

            }

            if (tmp != null) {
                response.append(tmp);
            }
        } while (tmp != null);

        returnEntry = response.toString();
    }

    public void sendPostRequest (String where) {
        URL loc = null;
        HttpURLConnection conn = null;
        InputStreamReader is;
        BufferedReader in;

        try {
            loc = new URL(where);
        }
        catch (MalformedURLException ex) {
            return;
        }

        try {
            conn = (HttpURLConnection)loc.openConnection();
            is = new InputStreamReader (conn.getInputStream(), "UTF-8");
            in = new BufferedReader (is);

            readResponse (in);
        }
        catch (IOException ex) {

        }
        finally {
            conn.disconnect();
        }

    }

    public String getReturnEntry() {
        if (!finished) {
            return "Hold tight!";
        }

        return returnEntry;
    }

    public JSONArray getResultAsJSON() {
        JSONArray jarr = null;

        if (finished == false) {
            return null;
        }
        try {
            jarr = new JSONArray(returnEntry);
        }
        catch (JSONException ex) {
            Log.d ("Output", "Error is " + ex.getMessage());
        }
        return jarr;
    }

    @Override
    protected void onPostExecute(Void result) {
        finished = true;

        Log.d("Output", returnEntry);
    }

    @Override
    protected Void doInBackground(String... params) {
        finished = false;
        sendPostRequest (params[0]);
        return null;
    }
}
