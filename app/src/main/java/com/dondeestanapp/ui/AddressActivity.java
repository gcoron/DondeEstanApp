package com.dondeestanapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.dondeestanapp.R;
import com.dondeestanapp.api.Api;
import com.dondeestanapp.api.model.ResponseAddressDTO;
import com.dondeestanapp.api.model.ResponseDriverDTO;
import com.dondeestanapp.api.model.ServerResponse;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends FragmentActivity implements
        OnMapReadyCallback {

    private List<Address> addressList;
    private MarkerOptions userMarkerOptions = new MarkerOptions();
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 101;
    private static final float zoom = 15;
    private FusedLocationProviderClient fusedLocationClient;

    private Integer userId;
    private String userType;

    private String street;
    private String number;
    private String zipCode;
    private String floor;
    private String apartament;
    private String city;
    private String state;
    private String country;

    private Address userAddress = null;

    EditText et_street;
    EditText et_number;
    EditText et_zipCode;
    EditText et_floor;
    EditText et_apartament;
    EditText et_city;
    EditText et_state;
    EditText et_country;

    Button btn_cancel_address;
    Button btn_search_address;
    Button btn_create_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        userId = getIntent().getIntExtra("userId", 0);
        userType = getIntent().getStringExtra("userType");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_set_address);
        if (mapFragment == null) throw new AssertionError();
        mapFragment.getMapAsync(this);

        et_street = findViewById(R.id.et_street);
        et_number = findViewById(R.id.et_number);
        et_zipCode = findViewById(R.id.et_zipCode);
        et_floor = findViewById(R.id.et_floor);
        et_apartament = findViewById(R.id.et_apartament);
        et_city = findViewById(R.id.et_city);
        et_state = findViewById(R.id.et_state);
        et_country = findViewById(R.id.et_country);

        btn_cancel_address = findViewById(R.id.btn_cancel_address);
        btn_search_address = findViewById(R.id.btn_search_address);
        btn_create_address = findViewById(R.id.btn_create_address);

        btn_create_address.setEnabled(false);
    }

    public void onClick(View view) {
        setColorFilterInEditText();

        switch (view.getId()) {
            case R.id.btn_cancel_address:
                Intent intent = new Intent(AddressActivity.this, MainActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userType", userType);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;

            case R.id.btn_search_address:
                boolean isAllFieldsOk = true;

                if (!isFilledEditText(et_street)) {
                    et_street.getBackground().setColorFilter(Color.RED,
                            PorterDuff.Mode.SRC_ATOP);
                    isAllFieldsOk = false;
                }
                if (!isFilledEditText(et_number)) {
                    et_number.getBackground().setColorFilter(Color.RED,
                            PorterDuff.Mode.SRC_ATOP);
                    isAllFieldsOk = false;
                }
                if (!isFilledEditText(et_zipCode)) {
                    et_zipCode.getBackground().setColorFilter(Color.RED,
                            PorterDuff.Mode.SRC_ATOP);
                    isAllFieldsOk = false;
                }
                if (!isFilledEditText(et_city)) {
                    et_city.getBackground().setColorFilter(Color.RED,
                            PorterDuff.Mode.SRC_ATOP);
                    isAllFieldsOk = false;
                }
                if (!isFilledEditText(et_state)) {
                    et_state.getBackground().setColorFilter(Color.RED,
                            PorterDuff.Mode.SRC_ATOP);
                    isAllFieldsOk = false;
                }
                if (!isFilledEditText(et_country)) {
                    et_country.getBackground().setColorFilter(Color.RED,
                            PorterDuff.Mode.SRC_ATOP);
                    isAllFieldsOk = false;
                }

                if (!isAllFieldsOk) {
                    Toast.makeText(AddressActivity.this, "Complete todos los campos",
                            Toast.LENGTH_SHORT).show();
                } else {

                    street = et_street.getText().toString().toLowerCase().trim();
                    number = et_number.getText().toString().toLowerCase().trim();
                    zipCode = et_zipCode.getText().toString().toLowerCase().trim();
                    floor = et_floor.getText().toString().toLowerCase().trim();
                    apartament = et_apartament.getText().toString().toLowerCase().trim();
                    city = et_city.getText().toString().toLowerCase().trim();
                    state = et_state.getText().toString().toLowerCase().trim();
                    country = et_country.getText().toString().toLowerCase().trim();

                    String address = street + " " + number + ", " + city + ", " + country;

                    if (!TextUtils.isEmpty(address)) {
                        Geocoder geocoder = new Geocoder(this);
                        try {
                            addressList = geocoder.getFromLocationName(address, 5);

                            if (addressList.size() > 0) {
                                for (int i=0; i<addressList.size(); i++) {
                                    userAddress = addressList.get(i);
                                    LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                                    userMarkerOptions.position(latLng);
                                    userMarkerOptions.title(address);
                                    userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                                    mMap.addMarker(userMarkerOptions);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

                                    disableEditText();
                                    enableButtonCreateAddress();

                                }
                            } else {
                                Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "Por favor, ingrese una dirección", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                break;

            case R.id.btn_create_address:
                Call<ServerResponse> createAddressResponseCall;

                createAddressResponseCall = Api.addressService().setAddressInObserverUser(
                        street, number, floor, apartament, zipCode, city,
                        state, country, Double.toString(userAddress.getLatitude()),
                        Double.toString(userAddress.getLongitude()), userId);

                createAddressResponseCall.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            ServerResponse<ResponseAddressDTO> userServerResponse =
                                    new ServerResponse<ResponseAddressDTO>(
                                            response.body().getCode(), response.body().getData(),
                                            response.body().getPaginator(), response.body().getStatus());

                            if (userServerResponse.getCode() == 200){
                                Toast.makeText(AddressActivity.this, "Dirección creada correctamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(AddressActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("userType", userType);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            } else if (userServerResponse.getCode() == 500){
                                Toast.makeText(AddressActivity.this, "Incorrect fields", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AddressActivity.this, "Register failed", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddressActivity.this, MainActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("userType", userType);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(AddressActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }

    }

    private void disableEditText() {
        et_street.setEnabled(false);
        et_number.setEnabled(false);
        et_zipCode.setEnabled(false);
        et_floor.setEnabled(false);
        et_apartament.setEnabled(false);
        et_city.setEnabled(false);
        et_state.setEnabled(false);
        et_country.setEnabled(false);
    }

    private void enableButtonCreateAddress() {
        btn_create_address.setEnabled(true);
        btn_search_address.setEnabled(false);
    };

    private void setColorFilterInEditText() {
        et_street.getBackground().setColorFilter(Color.GRAY,
                PorterDuff.Mode.SRC_ATOP);
        et_number.getBackground().setColorFilter(Color.GRAY,
                PorterDuff.Mode.SRC_ATOP);
        et_zipCode.getBackground().setColorFilter(Color.GRAY,
                PorterDuff.Mode.SRC_ATOP);
        et_floor.getBackground().setColorFilter(Color.GRAY,
                PorterDuff.Mode.SRC_ATOP);
        et_apartament.getBackground().setColorFilter(Color.GRAY,
                PorterDuff.Mode.SRC_ATOP);
        et_city.getBackground().setColorFilter(Color.GRAY,
                PorterDuff.Mode.SRC_ATOP);
        et_state.getBackground().setColorFilter(Color.GRAY,
                PorterDuff.Mode.SRC_ATOP);
        et_country.getBackground().setColorFilter(Color.GRAY,
                PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
            initMap(mMap);
        }
    }

    private void moveCameraToLastLocation(GoogleMap googleMap) {
        mMap = googleMap;

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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

    protected void requestPermission(String permissionType, int requestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionType}, requestCode
        );
    }

    public boolean isFilledEditText(EditText et) {
        return (!et.getText().toString().equals(""));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddressActivity.this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userType", userType);
        startActivity(intent);
        finish();
    }

}
