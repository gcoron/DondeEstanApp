package com.dondeestanapp.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dondeestanapp.MyFirebaseMessagingService;
import com.dondeestanapp.R;
import com.dondeestanapp.api.Api;
import com.dondeestanapp.api.model.LocationDTO;
import com.dondeestanapp.api.model.ObserverUserDTO;
import com.dondeestanapp.api.model.ServerResponse;
import com.dondeestanapp.ui.fragments.AccountFragment;
import com.dondeestanapp.ui.fragments.InformationFragment;
import com.dondeestanapp.ui.fragments.MapsFragment;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends FragmentActivity {

    private static final int INTERVAL = 2000; //2 segundos para salir

    private long firstClickTime;

    private Integer userId;
    private String driverPrivacyKey;
    private String userType;
    private String name;
    private String lastName;
    private String numberId;

    private String latitude;
    private String longitude;
    private String dayHour;

    Boolean isSavedLocation = false;

    //private GeofencingClient geofencingClient;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                new InsertLocation(MainActivity.this).execute();
                handler.postDelayed(runnable, 30000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String titleNotification = intent.getStringExtra("title");
            String descriptionNotification = intent.getStringExtra("description");

            if (titleNotification != null) {
                Intent intentDialog = new Intent(MainActivity.this, DialogActivity.class);
                intentDialog.putExtra("title", titleNotification);
                intentDialog.putExtra("description", descriptionNotification);
                startActivity(intentDialog);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = getIntent().getIntExtra("userId", 0);
        userType = getIntent().getStringExtra("userType");
        driverPrivacyKey = getIntent().getStringExtra("privacyKey");
        name = getIntent().getStringExtra("name");
        lastName = getIntent().getStringExtra("lastName");
        numberId = getIntent().getStringExtra("numberId");

        if (driverPrivacyKey == null) {
            driverPrivacyKey = "";
        }

        BottomNavigationView navigation = findViewById(R.id.navigationViewObservee);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(MyFirebaseMessagingService.ACTION_NEW_NOTIFICATION));

        loadFragment(new MapsFragment());

        if (userType.equals("observee")) {
            handler.postDelayed(runnable, 5000);

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                        1000);
            } else {
                locationStart();
            }
        }
    }


    //Insertamos los datos a nuestra webService
    private boolean insertLocation() {

        Call<ServerResponse> locationSaveResponseCall;

        locationSaveResponseCall = Api.getLocationService().saveLocation(
                this.latitude,
                this.longitude,
                this.dayHour,
                this.userId
        );

        locationSaveResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse<LocationDTO> userServerResponse = new ServerResponse<LocationDTO>(response.body().getCode(), response.body().getData(), response.body().getPaginator(), response.body().getStatus());
                    if (userServerResponse.getCode() == 200) {
                        isSavedLocation = true;

                    } else if (userServerResponse.getCode() == 500) {
                        Toast.makeText(MainActivity.this, "Server error", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Save location failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return isSavedLocation;
    }

    //AsyncTask para insertar Datos
    class InsertLocation extends AsyncTask<String, String, String> {

        private Activity context;

        InsertLocation(Activity context) {
            this.context = context;
        }

        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            if (insertLocation())
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Location saved successfull", Toast.LENGTH_LONG).show();
                        isSavedLocation = false;
                    }
                });
            else
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Save location failed", Toast.LENGTH_LONG).show();
                    }
                });
            return null;
        }
    }

    //Apartir de aqui empezamos a obtener la direciones y coordenadas
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    1000);
            return;
        }
        mlocManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0,
                (LocationListener) Local
        );
        mlocManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0,
                (LocationListener) Local
        );

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 3000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud

        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            this.latitude = Double.toString(loc.getLatitude());
            this.longitude = Double.toString(loc.getLongitude());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            this.dayHour = simpleDateFormat.format(new Date());
        }
    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        MainActivity mainActivity;

        public MainActivity getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            loc.getLatitude();
            loc.getLongitude();

            this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_maps:
                    //toolbar.setTitle("Mapa");
                    loadMapFragment(new MapsFragment());
                    return true;
                case R.id.menu_account:
                    //toolbar.setTitle("Mi Cuenta");
                    loadFragment(new AccountFragment());
                    return true;
                case R.id.action_info:
                    //toolbar.setTitle("Acerca de");
                    loadFragment(new InformationFragment());
                    return true;

            }
            return false;
        }
    };

    private void loadMapFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", userId);
        bundle.putString("userType", userType);
        bundle.putString("privacyKey", driverPrivacyKey);
        bundle.putString("name", driverPrivacyKey);
        bundle.putString("lastName", driverPrivacyKey);
        bundle.putString("numberId", driverPrivacyKey);

        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", userId);
        bundle.putString("userType", userType);
        bundle.putString("privacyKey", driverPrivacyKey);
        bundle.putString("name", name);
        bundle.putString("lastName", lastName);
        bundle.putString("numberId", numberId);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottom_navigation, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (firstClickTime + INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        firstClickTime = System.currentTimeMillis();
    }

}