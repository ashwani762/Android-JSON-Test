package com.ashwani.json_test;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    TextView title,cal,cal_fat,total;
    Food food;
    String name = "Apple";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        food = new Food();
        title = (TextView) findViewById(R.id.name);
        cal = (TextView) findViewById(R.id.cal);
        cal_fat = (TextView) findViewById(R.id.cal_fat);
        total = (TextView) findViewById(R.id.total);

        new AsyncFetch().execute();
    }

    private class AsyncFetch extends AsyncTask<Void,Void,Void>{

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;
        StringBuilder result = new StringBuilder();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("\tLoading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                url = new URL("http://192.168.1.6/"+name);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.connect();

                int response_code = conn.getResponseCode();
                if(response_code == HttpURLConnection.HTTP_OK){
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    String line = "";

                    while((line = reader.readLine()) != null){
                        result.append(line);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                conn.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            try{
                JSONObject json_data = new JSONObject(result.toString());
                food.calories = json_data.getString("calories");
                food.calories_fat = json_data.getString("calories_fat");
                food.title = json_data.getString("name");
                food.total_fat = json_data.getString("total_fat");

                title.setText(food.title);
                cal.setText(food.calories);
                cal_fat.setText(food.calories_fat);
                total.setText(food.total_fat);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
