package com.example.ankit.findyourflight;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String destination;
    String date;
    String url;
    String origin;
    ArrayList<String> mydata;

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i2 = getIntent();
        Bundle b = i2.getExtras();




        if (b != null) {
            String from = (String) b.get("origin");
            origin = from;
            String to = (String) b.get("destination");
            destination = to;
            String on = (String) b.get("on");
            date = on;
        }


        contactList = new ArrayList<>();
        mydata =new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new GetContacts().execute();
        if(!date.isEmpty())
        {
            url = "https://api.sandbox.amadeus.com/v1.2/flights/extensive-search?apikey=hr0ywoAXFpbXHwWOPgOaGP6t0PbMV9Ly&origin=" + origin + "&destination=" + destination + "&departure_date=" + date + "&one-way=true&aggregation_mode=day";
        }
        else
        {
            url = "https://api.sandbox.amadeus.com/v1.2/flights/extensive-search?apikey=hr0ywoAXFpbXHwWOPgOaGP6t0PbMV9Ly&origin=" + origin + "&destination=" + destination + "&one-way=true&aggregation_mode=day";
        }
    }
    ArrayList<HashMap<String, String>> contactList;


    // URL to get contacts JSON



    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String origin=jsonObj.getString("origin");

                    // Getting JSON Array node
                    JSONArray results = jsonObj.getJSONArray("results");

                    // looping through All Contacts
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject c = results.getJSONObject(i);
                        String destination = c.getString("destination");
                        String departure_date = c.getString("departure_date");
                        String price = c.getString("price");
                        String airline = c.getString("airline");

                        // Phone node is JSON Object
                        //JSONObject outbound = c.getJSONObject("outbound");
                        //String mobile = outbound.getString("duration");


                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("origin",origin);
                        contact.put("destination", destination);
                        contact.put("departure_date", departure_date);
                        contact.put("price",price);
                        contact.put("airline",airline);
                        // adding contact to contact list
                        contactList.add(contact);
                        mydata.add(origin+"#"+destination+"#"+departure_date+"#"+price+"#"+airline);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"origin","destination", "departure_date"
                    ,"price","airline"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile,R.id.price,R.id.airline});

            lv.setAdapter(adapter);
            String csv = "/sdcard/output.csv";
            CSVWriter writer = null;
            try {
                writer = new CSVWriter(new FileWriter(csv));
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<String[]> data = new ArrayList<String[]>();

           for(int i=0;i<mydata.size();i++)
            {
                String [] data1=mydata.get(i).split("#");

                                data.add(data1);
            }


           // String [] country = "India#China#United States".split("#");

            writer.writeAll(data);

            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}
