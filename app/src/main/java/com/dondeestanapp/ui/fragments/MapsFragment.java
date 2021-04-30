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
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.dondeestanapp.R;
import com.dondeestanapp.api.Api;
import com.dondeestanapp.api.model.Address;
import com.dondeestanapp.api.model.LocationDTO;
import com.dondeestanapp.api.model.NotificationDTO;
import com.dondeestanapp.api.model.ObserverUserDTO;
import com.dondeestanapp.api.model.ResponseAddressDTO;
import com.dondeestanapp.api.model.ServerResponse;
import com.dondeestanapp.ui.AddressActivity;
import com.dondeestanapp.ui.AddressListActivity;
import com.dondeestanapp.ui.CreateNotification;
import com.dondeestanapp.ui.DriverActivity;
import com.dondeestanapp.ui.MessagesListActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
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
    private static final String TAGTOPIC = "SUSCRIBED TO TOPIC: ";

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
    private Integer driverId;
    private String driverPrivacyKey;
    private String name;
    private String lastName;
    private String numberId;
    private String userType;
    private String driverLatitude;
    private String driverLongitude;
    private List<Address> addressDTOList;

    private final int TIME = 30000;

    private MarkerOptions busMarker;

    Boolean isSavedLocation = false;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                //moveCameraToLastLocation(mMap);
                new UpdateLocation(getActivity()).execute();
                //updateCamera(mMap);
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
        driverPrivacyKey = getArguments().getString("privacyKey");
        name = getArguments().getString("name");
        lastName = getArguments().getString("lastName");
        numberId = getArguments().getString("numberId");

        if (driverPrivacyKey == null) {
            driverPrivacyKey = "";
        }

        if (userType.equals("observer")) {
            setDataDriver();
            //setAddresses();
        }

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
                intent.putExtra("privacyKey", driverPrivacyKey);
                intent.putExtra("name", name);
                intent.putExtra("lastName", lastName);
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
                if (addressDTOList.size() == 0) {
                    Intent intent = new Intent(getActivity(), AddressActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("userType", userType);
                    startActivity(intent);

                } else if (addressDTOList.size() > 0) {
                    Intent intent = new Intent(getActivity(), AddressListActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("userType", userType);
                    intent.putExtra("addressCount", addressDTOList.size());
                    intent.putExtra("addresses", (Parcelable) addressDTOList);

                    startActivity(intent);

                } else {
                    Toast.makeText(getActivity(), "Error in addresses", Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(getActivity(), AddressActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userType", userType);
                startActivity(intent);
            }
        });

        return mapsView;
    }

    private void setDataDriver() {
        Call<ServerResponse> initObserverUserResponseCall;

        initObserverUserResponseCall = Api.getObserverUserService().setInitDataOfObserverUser(userId);

        initObserverUserResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse<ObserverUserDTO> userServerResponse =
                            new ServerResponse<ObserverUserDTO>(
                                    response.body().getCode(),
                                    response.body().getData(),
                                    response.body().getPaginator(),
                                    response.body().getStatus()
                            );

                    if (userServerResponse.getCode() == 200) {
                        List<ObserverUserDTO> observerUserDTOList = userServerResponse.getData();
                        Gson g = new Gson();
                        Type listType = new TypeToken<ArrayList<ObserverUserDTO>>() {
                        }.getType();
                        ArrayList<ObserverUserDTO> observerUserDTO =
                                g.fromJson(
                                        g.toJson(observerUserDTOList),
                                        listType
                                );

                        name = observerUserDTO.get(0).getName();
                        lastName = observerUserDTO.get(0).getLastName();
                        numberId = observerUserDTO.get(0).getNumberId();
                        driverId = observerUserDTO.get(0).getUserObserveeId();
                        driverPrivacyKey = observerUserDTO.get(0).getUserObserveePrivacyKey();
                        addressDTOList = observerUserDTO.get(0).getAddresses();
                        setTopicNotification();

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
    }

    public void setTopicNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic(driverPrivacyKey)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAGTOPIC, msg);
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
            initMap(mMap);
            moveCameraToLastLocation(mMap);
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
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LatLng latLng = new LatLng(Double.parseDouble(driverLatitude), Double.parseDouble(driverLongitude));
            float zoom = 15;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            mMap.clear();
            mMap.addMarker(busMarker.position(latLng));

            if (addressDTOList != null) {
                for (int i = 0; i < addressDTOList.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    Double.valueOf(addressDTOList.get(i).getLatitude()),
                                    Double.valueOf(addressDTOList.get(i).getLongitude()))
                            )
                            .title(addressDTOList.get(i).getStreet() +
                                    " " +
                                    addressDTOList.get(i).getNumber()));

                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(
                                    Double.valueOf(addressDTOList.get(i).getLatitude()),
                                    Double.valueOf(addressDTOList.get(i).getLongitude()))
                            )
                            .radius(100)
                            .strokeColor(Color.RED)
                            .fillColor(Color.BLUE));

                    float[] distanceBetween = new float[2];

                    Location.distanceBetween(
                            Double.valueOf(driverLatitude),
                            Double.valueOf(driverLongitude),
                            circle.getCenter().latitude,
                            circle.getCenter().longitude,
                            distanceBetween);

                    Float dist = distanceBetween[0];
                    Double radius = circle.getRadius();

                    //if (distanceBetween[0] < circle.getRadius()) {
                    if (dist < radius) {
                        createNotification(
                                "Aviso de arribo exitoso",
                                addressDTOList.get(i).getStreet() + " " +
                                        addressDTOList.get(i).getNumber(),
                                driverId
                        );
                    }
                }
            }
        } else {
            requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_REQUEST_CODE);
        }
    }

    private void createNotification(String title, String description, Integer driverId) {

        Call<ServerResponse> loginResponseCall = Api.getNotificationService().saveNotification(
                title,
                description,
                driverId
        );

        loginResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse<NotificationDTO> userServerResponse;
                    userServerResponse = new ServerResponse<NotificationDTO>(
                            response.body().getCode(),
                            response.body().getData(),
                            response.body().getPaginator(),
                            response.body().getStatus()
                    );
                    if (userServerResponse.getCode() == 200) {
                        Log.d("NOTIFICATION SENT: ", "Arribo exitoso");

                    } else if (userServerResponse.getCode() == 500) {
                        Toast.makeText(
                                getActivity(),
                                "Notification failed. Server error",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(
                            getActivity(),
                            "Notification failed",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(
                        getActivity(),
                        "Throwable " + t.getLocalizedMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void moveCameraToLastLocation(GoogleMap googleMap) {
        mMap = googleMap;

        int permission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            if (userType == "observee") {
                mMap.setMyLocationEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
            }
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
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
                                LatLng latLng = new LatLng(
                                        location.getLatitude(),
                                        location.getLongitude()
                                );
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

            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                        1000);
            } else {
                runTask();
            }
        }
    }

    public void runTask() {
        handler.postDelayed(runnable, TIME);
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
        locationUpdateResponseCall = Api.getObserverUserService().
                getLastLocationByObserverUserId(this.userId);

        locationUpdateResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse<LocationDTO> userServerResponse;
                    userServerResponse = new ServerResponse<LocationDTO>(
                            response.body().getCode(),
                            response.body().getData(),
                            response.body().getPaginator(),
                            response.body().getStatus());

                    if (userServerResponse.getCode() == 200) {
                        List<LocationDTO> locationsList = userServerResponse.getData();
                        Gson g = new Gson();
                        Type listType = new TypeToken<ArrayList<LocationDTO>>() {
                        }.getType();
                        ArrayList<LocationDTO> locations;
                        locations = g.fromJson(g.toJson(locationsList), listType);
                        driverLatitude = locations.get(0).getLatitude();
                        driverLongitude = locations.get(0).getLongitude();
                        isSavedLocation = true;

                    } else if (userServerResponse.getCode() == 500) {
                        Toast.makeText(
                                getActivity(),
                                "Server error",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                } else {
                    Toast.makeText(
                            getActivity(),
                            "Save location failed",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(
                        getActivity(),
                        "Throwable " + t.getLocalizedMessage(),
                        Toast.LENGTH_LONG
                ).show();
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

                        Toast.makeText(
                                context,
                                "Location updated successfull",
                                Toast.LENGTH_LONG
                        ).show();
                        isSavedLocation = false;
                    }
                });
            else
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(context,
                                "Update location failed",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
            return null;
        }
    }
}