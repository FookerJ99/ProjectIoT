package com.example.project_iot;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GraphDustActivity extends AppCompatActivity {
    DatePickerDialog datePickerDialog_start,datePickerDialog_end;
    EditText editText_start,editText_end;
    TextView avg_dust;
    double avg;
    Button button;
    String start_, end_, URL_,dataFetch = "",date_end_state;
    Calendar c1,c2;
    private static final String TAG = "DUST";
    private static final String DUST_URL = "https://api.thingspeak.com/channels/1053927/field/1.xml?";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_dust);

        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            if(savedInstanceState != null){
                dataFetch = savedInstanceState.getString("value"); // get Data in Rotate
                start_ = savedInstanceState.getString("date_start");
                end_ = savedInstanceState.getString("date_end");
                date_end_state = savedInstanceState.getString("date_end_state");
                avg = savedInstanceState.getDouble("avg");
            }
            if(!dataFetch.equals("")) {
                String[] a = dataFetch.split(",");
                double b = 0.0;
                GraphView graphViewLand = findViewById(R.id.graph_dust_land);
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                for (int i = 0; i < a.length; i++) {
                    series.appendData(new DataPoint((i + 1), Double.valueOf(a[i])), false, 500);
                    b += Double.valueOf(a[i]);
                }
                series.setDrawDataPoints(true);
                series.setDataPointsRadius(8);
                graphViewLand.removeAllSeries();
                graphViewLand.getViewport().setScalable(true);
                graphViewLand.getViewport().setScrollable(true);
                graphViewLand.getViewport().setScalableY(true);
                // set manual X bounds
                graphViewLand.getViewport().setXAxisBoundsManual(true);
                graphViewLand.getViewport().setMinX(1);
                graphViewLand.getViewport().setMaxX(5);
                // set manual Y bounds
                graphViewLand.getViewport().setYAxisBoundsManual(true);
                graphViewLand.getViewport().setMinY(250);
                graphViewLand.getViewport().setMaxY(600);
                graphViewLand.addSeries(series);
            }
        }else{
            avg_dust = findViewById(R.id.avg_dust);
            editText_start = findViewById(R.id.dust_start);
            editText_end = findViewById(R.id.dust_end);
            button = findViewById(R.id.show1);

            String date_now = new SimpleDateFormat("yyyy-M-d", Locale.getDefault()).format(new Date());
            LocalDate today = LocalDate.now();
            today = today.plusDays(1);

            start_ = date_now;
            end_ = String.valueOf(today);
            date_end_state = date_now;

            editText_start.setText(date_now);
            editText_end.setText(date_now);
            if(savedInstanceState != null){
                dataFetch = savedInstanceState.getString("value"); // get Data in Rotate
                start_ = savedInstanceState.getString("date_start");
                end_ = savedInstanceState.getString("date_end");
                date_end_state = savedInstanceState.getString("date_end_state");
                avg = savedInstanceState.getDouble("avg");

                editText_start.setText(start_);
                editText_end.setText(date_end_state);
            }
            if(!dataFetch.equals("")) {
                String[] a = dataFetch.split(",");
                double b = 0.0;
                GraphView graphView = findViewById(R.id.graph_dust);
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                for (int i = 0; i < a.length; i++) {
                    series.appendData(new DataPoint((i + 1), Double.valueOf(a[i])), false, 500);
                    b += Double.valueOf(a[i]);
                }
                series.setDrawDataPoints(true);
                series.setDataPointsRadius(8);
                graphView.removeAllSeries();
                graphView.getViewport().setScalable(true);
                graphView.getViewport().setScrollable(true);
                graphView.getViewport().setScalableY(true);
                // set manual X bounds
                graphView.getViewport().setXAxisBoundsManual(true);
                graphView.getViewport().setMinX(1);
                graphView.getViewport().setMaxX(5);
                // set manual Y bounds
                graphView.getViewport().setYAxisBoundsManual(true);
                graphView.getViewport().setMinY(250);
                graphView.getViewport().setMaxY(600);
                graphView.addSeries(series);
                avg_dust.setText("AVG: " + String.format("%.2f",avg));
            }

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
                            start_ = year + "-" + (month+1) + "-" + dayOfMonth;
                            editText_start.setText(year + "-" + (month+1) + "-" + dayOfMonth);

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
                    int months = c2.get(Calendar.MONTH);
                    int years = c2.get(Calendar.YEAR);
                    datePickerDialog_end = new DatePickerDialog(GraphDustActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            end_ = year + "-" + (month+1) + "-" + (dayOfMonth+1);
                            date_end_state = year + "-" + (month+1) + "-" + dayOfMonth;
                            editText_end.setText(year + "-" + (month+1) + "-" + dayOfMonth);
                        }
                    },years,months,days);
                    datePickerDialog_end.show();
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    URL_ = DUST_URL + "start=" + start_ + "&end=" + end_;
                    FetchThingspeakTask_ch3 field1_ch_Dust = new FetchThingspeakTask_ch3();
                    field1_ch_Dust.execute(URL_);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("avg",avg);
        outState.putString("date_start",start_);
        outState.putString("date_end",end_);
        outState.putString("date_end_state",date_end_state);
        outState.putString("value",dataFetch);// Put Data in outState
    }

    //Dust of Thing Speak
    private class FetchThingspeakTask_ch3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            dataFetch = "";
            try {
                URL url = new URL(urls[0]);
                URLConnection connection;
                connection = url.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection)connection;
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = httpConnection.getInputStream();
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();

                    Document dom = db.parse(in);
                    Element docEle = dom.getDocumentElement();

                    NodeList nl = docEle.getElementsByTagName("feed");
                    if (nl != null && nl.getLength() > 0) {
                        for (int i = 0 ; i < nl.getLength(); i++) {
                            Element entry = (Element)nl.item(i);
                            Element dust = (Element)entry.getElementsByTagName("field1").item(0);
                            if(dust.hasChildNodes()) {
                                dataFetch += dust.getFirstChild().getNodeValue() + ",";
                            }
                        }
                    }
                }
                return dataFetch;
            } catch (MalformedURLException e) {
                Log.d(TAG, "MalformedURLException", e);
                return null;
            } catch (IOException e) {
                Log.d(TAG, "IOException", e);
                return null;
            } catch (ParserConfigurationException e) {
                Log.d(TAG, "Parser Configuration Exception", e);
                return null;
            } catch (SAXException e) {
                Log.d(TAG, "SAX Exception", e);
                return null;
            }
            finally {
            }
        }

        protected void onPostExecute(String response) {
            if (response.equals("")) {
                Toast.makeText(GraphDustActivity.this, "ไม่พบข้อมูล",Toast.LENGTH_SHORT).show();
                return;
            }else {
                String[] a = response.split(",");
                double b = 0.0;
                GraphView graphView = findViewById(R.id.graph_dust);

                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                for (int i = 0; i<a.length; i++){
                    series.appendData(new DataPoint((i+1),Double.valueOf(a[i])),false,500);
                    b += Double.valueOf(a[i]);
                }
                series.setDrawDataPoints(true);
                series.setDataPointsRadius(8);
                graphView.removeAllSeries();
                graphView.getViewport().setScalableY(true);
                graphView.getViewport().setScalable(true);
                graphView.getViewport().setScrollable(true);
                // set manual X bounds
                graphView.getViewport().setXAxisBoundsManual(true);
                graphView.getViewport().setMinX(1);
                graphView.getViewport().setMaxX(5);
                // set manual Y bounds
                graphView.getViewport().setYAxisBoundsManual(true);
                graphView.getViewport().setMinY(250);
                graphView.getViewport().setMaxY(600);
                graphView.addSeries(series);
                avg = b/a.length;
                avg_dust.setText("AVG: " + String.format("%.2f",b/a.length));
            }
        }
    }
    public void GoHome(View view){
        startActivity(new Intent(GraphDustActivity.this,MainActivity.class));
    }


}
