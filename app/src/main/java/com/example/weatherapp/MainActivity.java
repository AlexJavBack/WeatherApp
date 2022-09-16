package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText user_Field;
    private Button button;
    private TextView result;
    public Settings settings = new Settings();
    public GetURLData getURLData = new GetURLData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_Field = findViewById(R.id.user_field);
        button = findViewById(R.id.button);
        result = findViewById(R.id.result);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_Field.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.Toast_Not_Find_Text,
                            Toast.LENGTH_SHORT).show();
                    }
                else {
                    String City = user_Field.getText().toString();
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + City +
                            "&appid=" + settings.getKey() + "&units=metric";
                    getURLData.execute(url);
                    //user_Field.setText(settings.getKey());
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute(){
            super.onPreExecute();
            result.setText("Expect...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) !=null){
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(httpURLConnection != null && reader != null) {
                    httpURLConnection.disconnect();
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String resultm){
            super.onPostExecute(resultm);

            try {
                JSONObject obj = new JSONObject(resultm);
                result.setText("C" + obj.getJSONObject("main").getDouble("temp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}