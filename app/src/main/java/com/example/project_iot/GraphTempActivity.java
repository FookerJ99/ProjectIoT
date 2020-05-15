package com.example.project_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GraphTempActivity extends AppCompatActivity {
    DatePickerDialog datePickerDialog_start,datePickerDialog_end;
    EditText editText_start,editText_end;
    Calendar c1,c2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_temp);

        String date_now = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        editText_start = findViewById(R.id.temp_start);
        editText_start.setText(date_now);

        editText_end = findViewById(R.id.temp_end);
        editText_end.setText(date_now);

        editText_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c1 = Calendar.getInstance();
                int days = c1.get(Calendar.DAY_OF_MONTH);
                int months = c1.get(Calendar.MONTH);
                int years = c1.get(Calendar.YEAR);
                datePickerDialog_start = new DatePickerDialog(GraphTempActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                datePickerDialog_end = new DatePickerDialog(GraphTempActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        editText_end.setText(year + "-" + m + "-" + day);
                    }
                },years,months,days);
                datePickerDialog_end.show();
            }
        });
    }

    public void GoHome(View view){
        startActivity(new Intent(GraphTempActivity.this,MainActivity.class));
    }
}
