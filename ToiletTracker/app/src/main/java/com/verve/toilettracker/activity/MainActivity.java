package com.verve.toilettracker.activity;
import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.verve.toilettracker.LocationAddress;
import com.verve.toilettracker.R;
import com.verve.toilettracker.URLConfig;
import com.verve.toilettracker.service.FloatingViewService;
import com.verve.toilettracker.service.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    public GPSTracker gps;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    protected LocationManager locationManager;
    int[] imgIds = {R.mipmap.ic_publictoilet, R.mipmap.hotel, R.mipmap.hotel, R.mipmap.ic_bus, R.mipmap.metro, R.mipmap.ic_railway, R.mipmap.hospital, R.mipmap.mall, R.mipmap.petrol, R.mipmap.bothmalefemle};
    CoordinatorLayout CoordinatorLayout;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    Geocoder geocoder;
    private LatLng latLng;
    private com.getbase.floatingactionbutton.FloatingActionButton fab_addtoilet, fab_reqtoilet, fab_feedback;
    private String address_onclik;
    private URLConfig urlConfig;
    Marker marker;
    private double latitude, longitude;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    SupportMapFragment mapFragment;
    DrawerLayout drawer;
    Toolbar toolbar;
    Menu nav_Menu;
    MenuInflater menuInflater;
    MenuItem item;
    private Menu mymenu;
    protected ServiceConnection mServerConn;
    private String toiletid;
    private String toiletAddress_snippet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initi();
        locationchak();
        service_calling();
    }
    @Override
    protected void onResume() {
        service_calling();
        threadforbackground();
        if (isFloatingviewserviceRunning(FloatingViewService.class)) {
            //   Toast.makeText(MainActivity.this, "true if condition", Toast.LENGTH_SHORT).show();
            stopService(new Intent(MainActivity.this, FloatingViewService.class));
            //     unbindService(mServerConn);
            //     unbindService(new Intent(this,  FloatingActionButton.class))     ;
        } else {
        }
        super.onResume();
    }
    private boolean isFloatingviewserviceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void threadforbackground() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getsharepreferences();
                    }
                });
            }
        }, 2000);
    }
    public void service_calling() {
        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLConfig.alldetailWebService,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //  Toast.makeText(MainActivity.this, "responce " + response, Toast.LENGTH_SHORT).show();
                                Log.i("response ", "responce " + response);
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                String value = jsonObject.getString("value");
                                ////////////
                                if (success.equals("1")) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(value);
                                        //     Toast.makeText(InboxActivity.this, "array " + jsonArray, Toast.LENGTH_SHORT).show();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jresponse = jsonArray.getJSONObject(i);
                                            String id = jresponse.getString("id");
                                            String name = jresponse.getString("name");
                                            String pincode = jresponse.getString("pincode");
                                            String address = jresponse.getString("address");
                                            String latitude1 = jresponse.getString("latitude");
                                            String longitude1 = jresponse.getString("longitude");
                                            String type1 = jresponse.getString("type");
                                            String date = jresponse.getString("date");
                                            String gender = jresponse.getString("gender");
                                            String remark = jresponse.getString("remark");
                                            String status = jresponse.getString("status");
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(new Double(latitude1), new Double(longitude1)))
                                                    .anchor(0.5f, 0.5f)
                                                    .title("" + id)
                                                    .snippet("" + address)
                                                    .icon(BitmapDescriptorFactory.fromResource(imgIds[geticon(type1)])));
//
                                            //  LatLng temlocation = new LatLng(new Double(latitude1), new Double(longitude1));
                                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                @Override
                                                public boolean onMarkerClick(Marker arg0) {
                                                    marker = arg0;
                                                    //   Toast.makeText(MainActivity.this, "title : "+marker.getTitle(), Toast.LENGTH_SHORT).show();
                                                    gps = new GPSTracker(MainActivity.this);
                                                    if (gps.canGetLocation()) {
                                                        latitude = gps.getLatitude();
                                                        longitude = gps.getLongitude();
                                                        latLng = marker.getPosition();
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                        builder.setMessage("Do you want to give feedback or get directions?")
                                                                .setCancelable(true)
                                                                .setPositiveButton("Feedback", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        urlConfig.showPD(MainActivity.this);
                                                                        toiletid = marker.getTitle();
                                                                        toiletAddress_snippet = marker.getSnippet();
                                                                        LocationAddress locationAddress = new LocationAddress();
                                                                        locationAddress.getAddressFromLocation(latLng.latitude, latLng.longitude,
                                                                                getApplicationContext(), new GeocoderHandler());
                                                                        dialog.cancel();
                                                                    }
                                                                })
                                                                .setNegativeButton("Direction", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                                                Uri.parse("http://maps.google.com/maps?saddr=" + latLng.latitude + "," + latLng.longitude + "&daddr=" + latitude + "," + longitude + ""));
                                                                        startActivity(intent);
                                                                        dialog.cancel();
//                                                                        bindService(intent, mServerConn, Context.BIND_AUTO_CREATE);
                                                                        startService(new Intent(MainActivity.this, FloatingViewService.class));
                                                                        MainActivity.this.finish();
                                                                    }
                                                                });
                                                        //Creating dialog box
                                                        AlertDialog alert = builder.create();
                                                        alert.setTitle("");
                                                        alert.show();
                                                    } else {
                                                        gps.showSettingsAlert();
                                                    }
                                                    return true;
                                                }
                                            });
//
                                        }
                                        LatLng temlocation = new LatLng(latitude, longitude);
                                        //  LatLng temlocation = new LatLng(latitude, longitude);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(temlocation));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temlocation, 14.5f));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        urlConfig.myToast(MainActivity.this, "Error : " + e.getMessage().toString());
                                    }
                                } else {
                                    urlConfig.myToast(MainActivity.this, message);
                                }
                            } catch (Exception e) {
                                Log.e("MyError101", e.getMessage().toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    Log.i("parameteralldata", String.valueOf(latitude) + " " + String.valueOf(longitude));
                    params.put("id", "1");
                    params.put("name", "");
                    params.put("pincode", "");
                    params.put("address", "");
                    params.put("lat", String.valueOf(latitude));
                    params.put("long", String.valueOf(longitude));
                    params.put("type", "");
                    params.put("date", "");
                    params.put("gender", "");
                    params.put("remark", "");
                    params.put("status", "");
                    return params;
                }
            };
            int socketTimeout = 10000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
    private void initi() {
        urlConfig = new URLConfig();
        geocoder = new Geocoder(this, Locale.getDefault());
        CoordinatorLayout = (CoordinatorLayout) findViewById(R.id.cordiantelayout);
        fab_addtoilet = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_tagtoilet);
        fab_reqtoilet = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fabrequest);
        fab_feedback = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fabfeedback);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fab_feedback.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Select On Toilet To Send Feedback", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
        fab_addtoilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    String faddress = address + "," + city + "," + state + "," + country
                            + "," + postalCode + "," + knownName;
                    // Toast.makeText(MainActivity.this, "address : \n " + faddress, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, AddToiletActivity.class);
                    intent.putExtra("addressaddtoilet", faddress);
//                    intent.putExtra("lat", latitude);
//                    intent.putExtra("long", longitude);
                    //  Toast.makeText(MainActivity.this, "data" + address_onclik, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
        });
        fab_reqtoilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    String faddress = address + "," + city + "," + state + "," + country
                            + "," + postalCode + "," + knownName;
                    Intent intent = new Intent(MainActivity.this, RequistActivity.class);
                    intent.putExtra("addressaddrequest", faddress);
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
        });
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        nav_Menu = navigationView.getMenu();
        mServerConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d("LOG_TAG", "onServiceConnected");
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("LOG_TAG", "onServiceDisconnected");
            }
        };
    }
    private void savePreferences(boolean b) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginvalue", b);
        editor.putString("id", "0");
        editor.commit();
    }
    private void locationchak() {
        initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            // set title
            alertDialogBuilder.setTitle("Exit ..?");
            alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
            // set dialog message
            alertDialogBuilder
                    .setMessage("Click Yes To Exit!")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        item = menu.findItem(R.id.idInbox);
        mymenu = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.idInbox) {
//
            startActivity(new Intent(MainActivity.this, InboxActivity.class));
//
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_login) {
//            showItemLogout();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        if (item.getItemId() == R.id.nav_logout) {
            //startActivity(new Intent(MainActivity.this, LoginActivity.class));
            showItemLogin();
            savePreferences(false);
            urlConfig.myToast(MainActivity.this, "Logout.Successfully");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void getsharepreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean checkBoxValue = sharedPreferences.getBoolean("loginvalue", false);
        if (checkBoxValue) {
            showItemLogout();
        } else {
            showItemLogin();
        }
    }
    public void showItemLogout() {
        nav_Menu.findItem(R.id.nav_logout).setVisible(true);
        nav_Menu.findItem(R.id.nav_login).setVisible(false);
        mymenu.findItem(R.id.idInbox)
                .setVisible(true);
    }
    public void showItemLogin() {
        nav_Menu.findItem(R.id.nav_login).setVisible(true);
        nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        mymenu.findItem(R.id.idInbox)
                .setVisible(false);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    private void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        updateGPSStatus("GPS is Enabled in your device");
                        allowed();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }
    private void allowed() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            // \n is for new line
            //  Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        updateGPSStatus("GPS is Enabled in your device");
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        updateGPSStatus("GPS is Disabled in your device");
                        break;
                }
                break;
        }
    }
    private void updateGPSStatus(String status) {
        urlConfig.myToast(MainActivity.this, status);
    }
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //  Toast.makeText(MainActivity.this, "latlong = " + mcurrentlatitude + "\n" + mcurrentlongitude, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    @Override
    public void onProviderEnabled(String s) {
    }
    @Override
    public void onProviderDisabled(String s) {
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            address_onclik = locationAddress;
            Intent intent = new Intent(MainActivity.this, FeedBackActivtiy.class);
            intent.putExtra("address", address_onclik);
            intent.putExtra("id", toiletid);
            intent.putExtra("toiletaddress", toiletAddress_snippet);
            startActivity(intent);
            urlConfig.hidePD();
        }
    }
    private int geticon(String type1) {
        int value = 9;
        switch (type1) {
            case "Public Toilet":
                value = 0;
                break;
            case "Hotel":
                value = 1;
                break;
            case "Retaurant":
                value = 2;
                break;
            case "Metro Station":
                value = 3;
                break;
            case "Bus Station":
                value = 4;
                break;
            case "Train Station":
                value = 5;
                break;
            case "Hospital":
                value = 6;
                break;
            case "Shopping Mall":
                value = 7;
                break;
            case "Petrol Pump":
                value = 8;
                break;
            case "":
                value = 9;
                break;
            default:
                value = 9;
                break;
        }
        return value;
    }
}
