package com.example.project_iot;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private String message_cel = " ";
    private GoogleMap mMap;
    private LatLng currentLocation;
    double lat,lng;
    Geocoder geocoder;
    List<Address> addresses;
    String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        message_cel = intent.getStringExtra(MainActivity.EXTRA_MESSAGE1);
        TextView textView = findViewById(R.id.text5);
        textView.setText(message_cel + " celsius");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void GoHome(View view){
        startActivity(new Intent(MapsActivity.this,MainActivity.class));
    }

    public void Share_Location(View view){
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat,lng,1);
            address = addresses.get(0).getAddressLine(0);
            AlertDialog.Builder conDel = new AlertDialog.Builder(this);
            conDel.setTitle("คุณต้องการแชร์ที่อยู่ของคุณหรือไม่?");
            conDel.setMessage("Temperature: "+ message_cel + " celsius, Address: " + address);
            conDel.setNegativeButton("No",null);
            conDel.setPositiveButton("Yes",new AlertDialog.OnClickListener(){
                public void onClick(DialogInterface dialog, int arg1) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Temperature: "+ message_cel + " celsius, Address: " + address);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "Send to"));
                }
            });
            conDel.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(myLocation == null){
                currentLocation = new LatLng(0,0);
            }else currentLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
             lat = myLocation.getLatitude();
             lng = myLocation.getLongitude();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }
}
