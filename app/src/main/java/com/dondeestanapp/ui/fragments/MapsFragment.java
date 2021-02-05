package com.dondeestanapp.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.dondeestanapp.R;
import com.dondeestanapp.ui.MessagesListActivity;
import com.dondeestanapp.ui.NotificationsListActivity;
import com.dondeestanapp.ui.ObserveeMainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MapsFragment extends Fragment implements OnMapReadyCallback{

    public MapsFragment() {
        // Required empty public constructor
    }

    private static final int LOCATION_REQUEST_CODE = 101;
    private GoogleMap mMap;
    MapView mMapView;
    View mapsView;

    FloatingActionButton myLocation_btn;
    FloatingActionButton add_btn;
    FloatingActionButton message_btn;
    FloatingActionButton notification_btn;

    Animation rotateOpen;
    Animation rotateClose;
    Animation fromBottom;
    Animation toBottom;

    Boolean clicked = false;
    private static final int INTERVAL = 2000; //2 seconds to exit
    private long firstClickTime;

    private FusedLocationProviderClient fusedLocationClient;
    private ActionBar toolbar;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(getActivity()!=null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mapsView = inflater.inflate(R.layout.fragment_maps, container, false);

        myLocation_btn = mapsView.findViewById(R.id.floating_action_button_my_location);
        add_btn = mapsView.findViewById(R.id.floating_action_button_add);
        message_btn = mapsView.findViewById(R.id.floating_action_button_message);
        notification_btn = mapsView.findViewById(R.id.floating_action_button_notification);

        rotateOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.to_bottom_anim);
/*
        mMapView = mapsView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); //needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        try{
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsFragment.this);
        }catch (Exception e){
            e.printStackTrace();
        }
        //mMapView.getMapAsync(MapsFragment.this);

        myLocation_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                moveCameraToLastLocation(mMap);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddButtonClicked();
            }
        });

        message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MessagesListActivity.class);
                startActivity(intent);
            }
        });

        notification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NotificationsListActivity.class);
                startActivity(intent);
            }
        });
        return mapsView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
            initMap(mMap);
        }
    }

    public void onDestroyView() {
        try {
            Fragment fragment = (getChildFragmentManager().findFragmentById(R.id.map));
            if (fragment != null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroyView();
    }

/*
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
*/
    private void moveCameraToLastLocation(GoogleMap googleMap) {
        mMap = googleMap;

        int permission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                float zoom = 15;
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
                            }
                        }
                    });
        } else {
            requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_REQUEST_CODE);
        }
    }
    private void initMap(GoogleMap googleMap) {
        mMap = googleMap;
        moveCameraToLastLocation(mMap);
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

    protected void requestPermission(String permissionType, int requestCode) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{permissionType}, requestCode
        );
    }
}