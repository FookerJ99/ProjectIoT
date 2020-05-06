package com.example.project_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GraphDustActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_dust);
    }

    public void GoHome(View view){
        startActivity(new Intent(GraphDustActivity.this,MainActivity.class));
    }
}
