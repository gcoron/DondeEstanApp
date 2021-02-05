package com.dondeestanapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.dondeestanapp.R;
import com.dondeestanapp.ui.fragments.AccountFragment;
import com.dondeestanapp.ui.fragments.InformationFragment;
import com.dondeestanapp.ui.fragments.MapsFragment;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;

public class ObserveeMainActivity extends FragmentActivity {

    private static final int LOCATION_REQUEST_CODE = 101;
    private GoogleMap mMap;

    FloatingActionButton myLocation_btn;
    FloatingActionButton add_btn;
    FloatingActionButton message_btn;
    FloatingActionButton notification_btn;

    Animation rotateOpen;
    Animation rotateClose;
    Animation fromBottom;
    Animation toBottom;

    Boolean clicked = false;
    private static final int INTERVAL = 2000; //2 segundos para salir
    private long firstClickTime;

    private FusedLocationProviderClient fusedLocationClient;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_observee);

        BottomNavigationView navigation = findViewById(R.id.navigationViewObservee);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new MapsFragment());

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_maps:
                    //toolbar.setTitle("Mapa");
                    loadFragment(new MapsFragment());
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


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void onAddButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClickable(clicked);

        if (!clicked) {
            clicked = true;
        } else {
            clicked = false;
        }
    }

    private void setClickable(Boolean clicked) {
        if (!clicked) {
            message_btn.setClickable(true);
            notification_btn.setClickable(true);
        } else {
            message_btn.setClickable(false);
            notification_btn.setClickable(false);
        }
    }

    @SuppressLint("RestrictedApi")
    private void setVisibility(Boolean clicked) {
        if (!clicked) {
            message_btn.setVisibility(View.VISIBLE);
            notification_btn.setVisibility(View.VISIBLE);
        } else {
            message_btn.setVisibility(View.INVISIBLE);
            notification_btn.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(Boolean clicked) {

        if (!clicked) {
            message_btn.startAnimation(fromBottom);
            notification_btn.startAnimation(fromBottom);
            add_btn.startAnimation(rotateOpen);
        } else {
            message_btn.startAnimation(toBottom);
            notification_btn.startAnimation(toBottom);
            add_btn.startAnimation(rotateClose);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottom_navigation, menu);

        return true;
    }

    @Override
    public void onBackPressed(){
        if (firstClickTime + INTERVAL > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        firstClickTime = System.currentTimeMillis();
    }

}