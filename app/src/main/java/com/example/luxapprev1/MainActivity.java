package com.example.luxapprev1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // the interface
    private Button mapBtn;
    private Button fragBtn;
    private TextView luxMainView;

    // sensor related
    private SensorManager sensorManager;
    private Sensor sensorLight;

    // fragment related
    private Boolean isResultShowed = true;

    // map related
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLatitude;
    private double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // get location service
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        // when permission granted, get location
        // when permission not granted, request permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        // show result frag
        ResFrag resultFragment = ResFrag.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frag_layout, resultFragment, "RESULT_FRAG").addToBackStack(null).commit();

        // interface assign
        luxMainView = findViewById(R.id.lux_value);
        fragBtn = findViewById(R.id.frag_btn);
        mapBtn = findViewById(R.id.map_btn);

        // frag button action
        fragBtn.setOnClickListener(view -> {
            if (isResultShowed) {
                displayDescFragment();
            } else {
                closeDescFragment();
            }
        });

        // map button action
        mapBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("LATITUDE", currentLatitude);
            intent.putExtra("LONGITUDE", currentLongitude);
            startActivity(intent);
        });
    }

    // location request permission result
    // when granted, get location
    // when not granted, show warning (toast)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, R.string.permission_denied_label, Toast.LENGTH_SHORT).show();
        }
    }

    // register listener on app start
    @Override
    protected void onStart() {
        super.onStart();
        if (sensorLight != null) {
            sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // unregister listener on app stop
    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    // when sensor has detect some changes
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            // show lux value
            luxMainView.setText(String.format(Locale.getDefault(),"%1$.2f", sensorEvent.values[0]));

            // show result details only if fragment is showed
            if (isResultShowed){
                // get fragment
                ResFrag resFrag = (ResFrag) getSupportFragmentManager().findFragmentByTag("RESULT_FRAG");

                // show lux
                resFrag.changeLuxText(String.format(Locale.getDefault(), "%1$.2f", sensorEvent.values[0]));

                // show date and time
                Date dateInstance = new Date();
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dateInstance);
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(dateInstance);
                resFrag.changeDateText(date);
                resFrag.changeTimeText(time);

                // show location
                String location = String.format(Locale.getDefault(),"(Lat) %1$.4f, (Long) %1.4f", currentLatitude, currentLongitude);
                resFrag.changeLocText(location);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // get current location
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // get location manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // when location service enabled, get location
        // when location service not enabled, open setting
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            // action when location retrieve has completed
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();

                if (location != null) {
                    // set latitude & longitude
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                } else {
                    // when above method don't get any result
                    LocationRequest locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10000)
                            .setFastestInterval(1000)
                            .setNumUpdates(1);
                    LocationCallback locationCallback =new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            Location location1 = locationResult.getLastLocation();
                            if (location1 != null) {
                                currentLatitude = location1.getLatitude();
                                currentLongitude = location1.getLongitude();
                            } else {
                                Toast.makeText(MainActivity.this, R.string.get_location_failed_label, Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }

            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    // close result, display description
    private void displayDescFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ResFrag resFrag = (ResFrag) fragmentManager.findFragmentById(R.id.frag_layout);
        if (resFrag != null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(resFrag).commit();
        }
        DescFrag descFrag = DescFrag.newInstance();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frag_layout, descFrag).addToBackStack(null).commit();
        fragBtn.setText(R.string.frag_btn_label2);
        isResultShowed = false;
    }

    // close description, display result
    private void closeDescFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DescFrag descFrag = (DescFrag) fragmentManager.findFragmentById(R.id.frag_layout);
        if (descFrag != null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(descFrag).commit();
        }
        ResFrag resFrag = ResFrag.newInstance();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frag_layout, resFrag, "RESULT_FRAG").addToBackStack(null).commit();
        fragBtn.setText(R.string.frag_btn_label);
        isResultShowed = true;
    }

    // inflate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    // do action on menu selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_language) {
            Intent languageIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(languageIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}