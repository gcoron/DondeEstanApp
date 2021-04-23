package com.dondeestanapp.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.dondeestanapp.R;
import com.dondeestanapp.api.Api;
import com.dondeestanapp.api.model.LocationDTO;
import com.dondeestanapp.api.model.ResponseAddressDTO;
import com.dondeestanapp.api.model.ServerResponse;
import com.dondeestanapp.ui.AddressActivity;
import com.dondeestanapp.ui.AddressListActivity;
import com.dondeestanapp.ui.CreateNotification;
import com.dondeestanapp.ui.DriverActivity;
import com.dondeestanapp.ui.MessagesListActivity;
import com.dondeestanapp.ui.NotificationsListActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public MapsFragment() {
        // Required empty public constructor
    }

    private static final int LOCATION_REQUEST_CODE = 101;
    private GoogleMap mMap;
    View mapsView;

    FloatingActionButton myLocation_btn;
    FloatingActionButton add_btn;
    FloatingActionButton message_btn;
    FloatingActionButton notification_btn;
    FloatingActionButton driver_btn;
    FloatingActionButton address_btn;

    Animation rotateOpen;
    Animation rotateClose;
    Animation fromBottom;
    Animation toBottom;

    Boolean clicked = false;

    private FusedLocationProviderClient fusedLocationClient;

    private Integer userId;
    private String userType;
    private String latitude;
    private String longitude;

    private final int TIEMPO = 30000;

    private MarkerOptions busMarker;

    Boolean isSavedLocation = false;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                new UpdateLocation(getActivity()).execute();
                updateCamera(mMap);
                handler.postDelayed(runnable, 30000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (getActivity() != null) {
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

        userId = getArguments().getInt("userId");
        userType = getArguments().getString("userType");

        myLocation_btn = mapsView.findViewById(R.id.floating_action_button_my_location);
        add_btn = mapsView.findViewById(R.id.floating_action_button_add);
        message_btn = mapsView.findViewById(R.id.floating_action_button_message);
        notification_btn = mapsView.findViewById(R.id.floating_action_button_notification);
        driver_btn = mapsView.findViewById(R.id.floating_action_button_driver);
        address_btn = mapsView.findViewById(R.id.floating_action_button_address);

        rotateOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.to_bottom_anim);

        busMarker = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_school_bus)).anchor(0.0f, 0.8f);

        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                intent.putExtra("userId", userId);
                intent.putExtra("userType", userType);
                startActivity(intent);
            }
        });

        notification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateNotification.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userType", userType);
                startActivity(intent);
            }
        });

        driver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DriverActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userType", userType);
                startActivity(intent);
            }
        });

        address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<ServerResponse> createAddressResponseCall;

                createAddressResponseCall = Api.getAddressService().getAddressesById(userId);

                createAddressResponseCall.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            ServerResponse<ResponseAddressDTO> userServerResponse =
                                    new ServerResponse<ResponseAddressDTO>(
                                            response.body().getCode(), response.body().getData(),
                                            response.body().getPaginator(), response.body().getStatus());

                            List<ResponseAddressDTO> addressList = userServerResponse.getData();
                            Gson g = new Gson();
                            Type listType = new TypeToken<ArrayList<ResponseAddressDTO>>() {
                            }.getType();
                            ArrayList<ResponseAddressDTO> observerUserAddress = g.fromJson(g.toJson(addressList), listType);

                            if (userServerResponse.getCode() == 200 && observerUserAddress.size() == 0) {
                                Intent intent = new Intent(getActivity(), AddressActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("userType", userType);
                                startActivity(intent);

                            } else if (userServerResponse.getCode() == 200 && observerUserAddress.size() > 0) {
                                Intent intent = new Intent(getActivity(), AddressListActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("userType", userType);
                                intent.putExtra("addressCount", observerUserAddress.size());
                                intent.putExtra("addresses", observerUserAddress);

                                startActivity(intent);

                            } else if (userServerResponse.getCode() == 500) {
                                Toast.makeText(getActivity(), "Incorrect userId", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Address request failed", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(getActivity(), "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                Intent intent = new Intent(getActivity(), AddressActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userType", userType);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    private void updateCamera(GoogleMap googleMap) {
        mMap = googleMap;

        int permission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            float zoom = 15;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            mMap.clear();
            mMap.addMarker(busMarker.position(latLng));
        } else {
            requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_REQUEST_CODE);
        }
    }

    private void moveCameraToLastLocation(GoogleMap googleMap) {
        mMap = googleMap;

        int permission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        if (userType.equals("observee")) {
            moveCameraToLastLocation(mMap);
        } else if (userType.equals("observer")) {
            handler.postDelayed(runnable, 5000);

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            } else {
                ejecutarTarea();
            }
        }
    }

    public void ejecutarTarea() {
        handler.postDelayed(runnable, TIEMPO);

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
            if (userType.equals("observer")) {
                driver_btn.setClickable(true);
                address_btn.setClickable(true);
            }
        } else {
            message_btn.setClickable(false);
            notification_btn.setClickable(false);
            if (userType.equals("observer")) {
                driver_btn.setClickable(false);
                address_btn.setClickable(false);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void setVisibility(Boolean clicked) {
        if (!clicked) {
            message_btn.setVisibility(View.VISIBLE);
            notification_btn.setVisibility(View.VISIBLE);
            if (userType.equals("observer")) {
                driver_btn.setVisibility(View.VISIBLE);
                address_btn.setVisibility(View.VISIBLE);
            }
        } else {
            message_btn.setVisibility(View.INVISIBLE);
            notification_btn.setVisibility(View.INVISIBLE);
            if (userType.equals("observer")) {
                driver_btn.setVisibility(View.INVISIBLE);
                address_btn.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setAnimation(Boolean clicked) {
        if (!clicked) {
            message_btn.startAnimation(fromBottom);
            notification_btn.startAnimation(fromBottom);
            add_btn.startAnimation(rotateOpen);
            if (userType.equals("observer")) {
                driver_btn.startAnimation(fromBottom);
                address_btn.startAnimation(fromBottom);
            }
        } else {
            message_btn.startAnimation(toBottom);
            notification_btn.startAnimation(toBottom);
            add_btn.startAnimation(rotateClose);
            if (userType.equals("observer")) {
                driver_btn.startAnimation(toBottom);
                address_btn.startAnimation(toBottom);
            }
        }
    }

    protected void requestPermission(String permissionType, int requestCode) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{permissionType}, requestCode
        );
    }


    //Insertamos los datos a nuestra webService
    private boolean updateLocation() {

        Call<ServerResponse> locationUpdateResponseCall;

        locationUpdateResponseCall = Api.getObserverUserService().getLastLocationByObserverUserId(this.userId);

        locationUpdateResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse<LocationDTO> userServerResponse = new ServerResponse<LocationDTO>(response.body().getCode(), response.body().getData(), response.body().getPaginator(), response.body().getStatus());
                    if (userServerResponse.getCode() == 200) {
                        List<LocationDTO> locationsList = userServerResponse.getData();
                        Gson g = new Gson();
                        Type listType = new TypeToken<ArrayList<LocationDTO>>() {
                        }.getType();
                        ArrayList<LocationDTO> locations = g.fromJson(g.toJson(locationsList), listType);
                        latitude = locations.get(0).getLatitude();
                        longitude = locations.get(0).getLongitude();
                        isSavedLocation = true;

                    } else if (userServerResponse.getCode() == 500) {
                        Toast.makeText(getActivity(), "Server error", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Save location failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return isSavedLocation;
    }

    //AsyncTask para actualizar Datos
    class UpdateLocation extends AsyncTask<String, String, String> {

        private Activity context;

        UpdateLocation(Activity context) {
            this.context = context;
        }

        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            if (updateLocation())
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        updateCamera(mMap);

                        Toast.makeText(context, "Location updated successfull", Toast.LENGTH_LONG).show();
                        isSavedLocation = false;
                    }
                });
            else
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Update location failed", Toast.LENGTH_LONG).show();
                    }
                });
            return null;
        }
    }
}