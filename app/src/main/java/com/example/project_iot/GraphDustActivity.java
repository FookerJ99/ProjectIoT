package com.example.project_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class GraphDustActivity extends AppCompatActivity {
    DatePickerDialog datePickerDialog_start,datePickerDialog_end;
    EditText editText_start,editText_end;
    TextView textView;
    Button button;
    String start_, end_, URL_;
    Calendar c1,c2;

    private static final String DUST_URL = "https://api.thingspeak.com/channels/1053927/field/1?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_dust);

        textView = findViewById(R.id.graph_dust);

        String date_now = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        editText_start = findViewById(R.id.dust_start);
        editText_start.setText(date_now);

        editText_end = findViewById(R.id.dust_end);
        editText_end.setText(date_now);

        button = findViewById(R.id.show1);

        start_ = date_now;
        end_ = date_now;

        editText_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c1 = Calendar.getInstance();
                int days = c1.get(Calendar.DAY_OF_MONTH);
                int months = c1.get(Calendar.MONTH);
                int years = c1.get(Calendar.YEAR);
                datePickerDialog_start = new DatePickerDialog(GraphDustActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String day = String.valueOf(dayOfMonth);
                        String m = String.valueOf(month+1);
                        if(dayOfMonth<10){
                            day = "0" + day;
                        }
                        if((month+1)<10){
                            m = "0" + m;
                        }
                        start_ = year + "-" + m + "-" + day;
                        editText_start.setText(year + "-" + m + "-" + day);

                    }
                },years,months,days);
                datePickerDialog_start.show();
            }
        });


        editText_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c2 = Calendar.getInstance();
                int days = c2.get(Calendar.DAY_OF_MONTH);
                int months = c1.get(Calendar.MONTH);
                int years = c1.get(Calendar.YEAR);
                datePickerDialog_end = new DatePickerDialog(GraphDustActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String day = String.valueOf(dayOfMonth);
                        String m = String.valueOf(month+1);
                        if(dayOfMonth<10){
                            day = "0" + day;
                        }
                        if((month+1)<10){
                            m = "0" + m;
                        }
                        end_ = year + "-" + m + "-" + day;
                        editText_end.setText(year + "-" + m + "-" + day);
                    }
                },years,months,days);
                datePickerDialog_end.show();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        //start=2020-05-06&end=2020-05-07
                        URL_ = DUST_URL + "start=" + start_ + "&end=" + end_;
                        FetchThingspeakTask_ch3 field1_ch_Dust = new FetchThingspeakTask_ch3();
                        field1_ch_Dust.execute(URL_);
                    }
                }, 0, 2000);
                Toast.makeText(getApplicationContext()," : " + URL_ ,Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Dust of Thing Speak
    private class FetchThingspeakTask_ch3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                Toast.makeText(GraphDustActivity.this, "There was an error",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                if(channel.has("field1")) {
                    String status = channel.getString("field1");
                    if(!status.equals("null")){
                        textView.setText(channel.getString("field1"));
                        //double d = Double.valueOf(channel.getString("field1"));
                    }else{
                        textView.setText("NULL");
                        Toast.makeText(getApplicationContext(),"ไม่มีค่าที่จะแสดง" ,Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void GoHome(View view){
        startActivity(new Intent(GraphDustActivity.this,MainActivity.class));
    }


}
