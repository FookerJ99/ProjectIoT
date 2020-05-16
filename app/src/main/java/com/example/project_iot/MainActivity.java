package com.example.project_iot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    TextView date_show,time_show,dust,temp,switch_check,tx1,tx2,tx3,tx4;
    int check_num = 1;
    LinearLayout dust_layout,temp_layout;
    private static final String DUST_URL = "https://api.thingspeak.com/channels/1053927/field/1/last.json";
    private static final String TEMPERATURE_URL = "https://api.thingspeak.com/channels/1053925/field/2/last.json";
    private static final String SWITCH_URL = "https://api.thingspeak.com/update.json?api_key=4PLI0O9D26Z685MW&field3=";
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //show date time
        String date_now = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date_show = findViewById(R.id.date_show);
        date_show.setText(date_now);

        time_show = findViewById(R.id.time_show);
        String time_now = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        time_show.setText(time_now);
        Thread t = new Thread(){
            @Override
            public void run(){
                try{
                    while(!isInterrupted()){
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String time_now = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                                time_show.setText(time_now);
                            }
                        });
                    }
                }catch (InterruptedException e){

                }
            }
        };
        t.start();
        // Show from thingspeak
        dust = findViewById(R.id.dust);
        temp = findViewById(R.id.temp);
        switch_check = findViewById(R.id.switch_check);

        switch_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchThingspeakTask_ch1 field3 = new FetchThingspeakTask_ch1();
                Random r = new Random();
                int number = r.nextInt(1000-3)+3;
                while (check_num==number){
                    number = r.nextInt(1000-3)+3;
                }
                String FIELD3_URL = SWITCH_URL + number;
                check_num = number;

                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        FetchThingspeakTask_ch2 field2_ch_Temp = new FetchThingspeakTask_ch2();
                        field2_ch_Temp.execute(TEMPERATURE_URL);
                    }
                }, 0, 2000);

                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        FetchThingspeakTask_ch3 field1_ch_Dust = new FetchThingspeakTask_ch3();
                        field1_ch_Dust.execute(DUST_URL);
                    }
                }, 0, 2000);

                field3.execute(FIELD3_URL);
                disableSwitch();
            }
        });

    }

    private void disableSwitch(){
        switch_check.setEnabled(false);

        new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                switch_check.setBackgroundColor(Color.parseColor("#BDBDBD"));
                switch_check.setText("Wait "+millisUntilFinished / 1000);
            }

            public void onFinish() {
                switch_check.setBackgroundColor(Color.parseColor("#FFD700"));
                switch_check.setText("Check");
                switch_check.setEnabled(true);
            }
        }.start();
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
            if (!response.equals("-1\n")) {
                if (response == null) {
                    Toast.makeText(MainActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                    if (channel.has("field1")) {
                        String status = channel.getString("field1");
                        if (!status.equals("")) {
                            dust.setText(channel.getString("field1"));
                            double d = Double.valueOf(channel.getString("field1"));
                            tx1 = findViewById(R.id.text1);
                            tx2 = findViewById(R.id.text2);
                            dust_layout = findViewById(R.id.layout_dust);
                            if (d > 400) {
                                tx1.setTextColor(Color.parseColor("#FFFFFF"));
                                tx2.setTextColor(Color.parseColor("#FFFFFF"));
                                dust.setTextColor(Color.parseColor("#FFFFFF"));

                                dust_layout.setBackgroundColor(Color.parseColor("#E74C3C"));
                            } else {
                                tx1.setTextColor(Color.parseColor("#6f6f6f"));
                                tx2.setTextColor(Color.parseColor("#6f6f6f"));
                                dust.setTextColor(Color.parseColor("#6f6f6f"));

                                dust_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            }
                        }else {
                            Toast.makeText(MainActivity.this, "ไม่พบข้อมูลของค่าฝุ่น PM 2.5", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(MainActivity.this, "ไม่พบข้อมูลของค่าฝุ่น PM 2.5", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Temp of Thing Speak
    private class FetchThingspeakTask_ch2 extends AsyncTask<String, Void, String> {
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
            if (!response.equals("-1\n")) {
                if (response == null) {
                    Toast.makeText(MainActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                    if (channel.has("field2")) {
                        String status = channel.getString("field2");
                        if (!status.equals("")) {
                            temp.setText(channel.getString("field2"));
                            double t = Double.valueOf(channel.getString("field2"));
                            tx3 = findViewById(R.id.text3);
                            tx4 = findViewById(R.id.text4);
                            temp_layout = findViewById(R.id.temp_layout);
                            if (t < 0) {
                                tx3.setTextColor(Color.parseColor("#FFFFFF"));
                                tx4.setTextColor(Color.parseColor("#FFFFFF"));
                                temp.setTextColor(Color.parseColor("#FFFFFF"));
                                temp_layout.setBackgroundColor(Color.parseColor("#303F9F"));
                            } else if (t < 20) {
                                tx3.setTextColor(Color.parseColor("#37474F"));
                                tx4.setTextColor(Color.parseColor("#37474F"));
                                temp.setTextColor(Color.parseColor("#37474F"));
                                temp_layout.setBackgroundColor(Color.parseColor("#1E88E5"));
                            } else if (t < 28) {
                                tx3.setTextColor(Color.parseColor("#37474F"));
                                tx4.setTextColor(Color.parseColor("#37474F"));
                                temp.setTextColor(Color.parseColor("#37474F"));
                                temp_layout.setBackgroundColor(Color.parseColor("#BBDEFB"));
                            } else if (t < 35) {
                                tx3.setTextColor(Color.parseColor("#37474F"));
                                tx4.setTextColor(Color.parseColor("#37474F"));
                                temp.setTextColor(Color.parseColor("#37474F"));
                                temp_layout.setBackgroundColor(Color.parseColor("#FF8A65"));
                            } else {
                                tx3.setTextColor(Color.parseColor("#FFFFFF"));
                                tx4.setTextColor(Color.parseColor("#FFFFFF"));
                                temp.setTextColor(Color.parseColor("#FFFFFF"));
                                temp_layout.setBackgroundColor(Color.parseColor("#F44336"));
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "ไม่พบข้อมูลของอุณหภูมิ", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(MainActivity.this, "ไม่พบข้อมูลของอุณหภูมิ", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Switch of Thing Speak
    private class FetchThingspeakTask_ch1 extends AsyncTask<String, Void, String> {
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
                Toast.makeText(MainActivity.this, "There was an error",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                if(channel.has("field3")) {
                    String status = channel.getString("field3");
                    if(!status.equals("null")){
                        Toast.makeText(getApplicationContext(),"กรุณารอสักครู่...",Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public final  static String EXTRA_MESSAGE1 = "th.ac.th.Message1";
    public void View_Map(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                // Show an explanation to the user asynchronously -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }
        }else {

            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra(EXTRA_MESSAGE1,dust.getText());
            startActivity(intent);
            //startActivity(new Intent(MainActivity.this,MapsActivity.class));
        }

    }

    public void ShowGraphDust(View view){
        startActivity(new Intent(MainActivity.this,GraphDustActivity.class));
    }

    public void ShowGraphTemp(View view){
        startActivity(new Intent(MainActivity.this,GraphTempActivity.class));
    }
}
