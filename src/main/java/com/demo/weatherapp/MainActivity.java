package com.demo.weatherapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private TextView textViewWeather;
    private String url = "https://api.openweathermap.org/data/2.5/weather?q=moscow&appid=0d830906a7da8ee891c24222ff0e41c4";
    private String name, weatherOutside;
    private double temp;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        editTextCity = findViewById(R.id.editTextCity);
        textViewWeather = findViewById(R.id.textViewWeather);
        setWeather();
    }

    public void setWeather() {
        DownloadJSONTask task = new DownloadJSONTask();
        try {
            result = task.execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(result);
            name = jsonObject.getString("name");
            JSONObject jsonObject1Main = jsonObject.getJSONObject("main");
            temp = jsonObject1Main.getDouble("temp") - 273.15;
            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
            weatherOutside = jsonObjectWeather.getString("main");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result.isEmpty()) {
            Toast.makeText(this, R.string.message_text, Toast.LENGTH_SHORT).show();
        }
        String weather = String.format(getString(R.string.text_weather), name, temp, weatherOutside);
        textViewWeather.setText(weather);
    }

    public void getCity(View view) {
        String city = editTextCity.getText().toString().trim();
        if (!city.isEmpty()) {
            url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=0d830906a7da8ee891c24222ff0e41c4";
        }
        setWeather();
    }

    private static class DownloadJSONTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    result.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return result.toString();
        }

//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            try {
//                JSONObject jsonObject = new JSONObject(s);
//                name = jsonObject.getString("name");
//                JSONObject jsonObject1Main = jsonObject.getJSONObject("main");
//                temp = jsonObject1Main.getDouble("temp") - 273.15;
//                JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
//                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
//                weatherOutside = jsonObjectWeather.getString("main");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
    }
}