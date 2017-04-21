package com.minicart.demo.baidumap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.minicart.demo.baidumap.location.LocationActivity;
import com.minicart.demo.baidumap.map.MapActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void joinMap(View view) {
        startActivity(new Intent(this, MapActivity.class));
    }

    public void joinLocation(View view) {
        startActivity(new Intent(this, LocationActivity.class));
    }
}
