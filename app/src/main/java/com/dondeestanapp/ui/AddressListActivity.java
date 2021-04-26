package com.dondeestanapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.dondeestanapp.R;
import com.dondeestanapp.api.Api;
import com.dondeestanapp.api.model.ResponseAddressDTO;
import com.dondeestanapp.api.model.ServerResponse;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressListActivity extends FragmentActivity implements
        OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 101;
    private static final float zoom = 13;
    private FusedLocationProviderClient fusedLocationClient;

    private Integer userId;
    private String userType;
    private Integer addressCount;
    private List<ResponseAddressDTO> addressList;

    Button btn_delete_address1;
    Button btn_delete_address2;
    Button btn_create_address_list;

    TextView tv_address1;
    TextView tv_address2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        userId = getIntent().getIntExtra("userId", 0);
        userType = getIntent().getStringExtra("userType");
        addressList = (List<ResponseAddressDTO>) getIntent().getSerializableExtra("addresses");
        addressCount = getIntent().getIntExtra("addressCount", addressList.size());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_address_list);
        if (mapFragment == null) throw new AssertionError();
        mapFragment.getMapAsync(this);

        btn_delete_address1 = findViewById(R.id.btn_delete_address1);
        btn_delete_address2 = findViewById(R.id.btn_delete_address2);
        btn_create_address_list = findViewById(R.id.btn_create_address_list);
        tv_address1 = findViewById(R.id.tv_address1);
        tv_address2 = findViewById(R.id.tv_address2);

        if (addressCount == 2) {
            btn_create_address_list.setEnabled(false);
            tv_address1.setText(addressList.get(0).getStreet() + " " +
                                addressList.get(0).getNumber() + ", " +
                                addressList.get(0).getCity());
            tv_address2.setText(addressList.get(1).getStreet() + " " +
                                addressList.get(1).getNumber() + ", " +
                                addressList.get(1).getCity());
        }
        if (addressCount == 1) {
            btn_delete_address2.setEnabled(false);
            tv_address1.setText(addressList.get(0).getStreet() + " " +
                                addressList.get(0).getNumber() + ", " +
                                addressList.get(0).getCity());
        }
        if (addressCount == 0) {
            btn_delete_address1.setEnabled(false);
            btn_delete_address2.setEnabled(false);
        }
    }

    public void onClick(View view) {
        Intent intent;
        Integer id;
        Call<ServerResponse> createAddressResponseCall;

        switch (view.getId()) {
            case R.id.btn_cancel_address:
                intent = new Intent(AddressListActivity.this, AddressActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userType", userType);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;

            case R.id.btn_create_address_list:
                intent = new Intent(AddressListActivity.this, AddressActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userType", userType);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btn_delete_address1:

                id = addressList.get(0).getId();
                createAddressResponseCall = Api.getAddressService().deleteAddressById(id);

                createAddressResponseCall.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            ServerResponse<ResponseAddressDTO> userServerResponse =
                                    new ServerResponse<ResponseAddressDTO>(
                                            response.body().getCode(), response.body().getData(),
                                            response.body().getPaginator(), response.body().getStatus());

                            if (userServerResponse.getCode() == 200){
                                Toast.makeText(AddressListActivity.this, "Dirección eliminada correctamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(AddressListActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("userType", userType);

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            } else if (userServerResponse.getCode() == 500){
                                Toast.makeText(AddressListActivity.this, "Incorrect fields", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AddressListActivity.this, "Delete failed", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddressListActivity.this, MainActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("userType", userType);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(AddressListActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                break;


            case R.id.btn_delete_address2:

                id = addressList.get(1).getId();
                createAddressResponseCall = Api.getAddressService().deleteAddressById(id);

                createAddressResponseCall.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            ServerResponse<ResponseAddressDTO> userServerResponse =
                                    new ServerResponse<ResponseAddressDTO>(
                                            response.body().getCode(), response.body().getData(),
                                            response.body().getPaginator(), response.body().getStatus());

                            if (userServerResponse.getCode() == 200){
                                Toast.makeText(AddressListActivity.this, "Dirección eliminada correctamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(AddressListActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("userType", userType);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            } else if (userServerResponse.getCode() == 500){
                                Toast.makeText(AddressListActivity.this, "Incorrect fields", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AddressListActivity.this, "Delete failed", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddressListActivity.this, MainActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("userType", userType);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(AddressListActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddressListActivity.this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userType", userType);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (addressCount > 0) {
            Double latitudeFinal = Double.valueOf(0);
            Double longitudeFinal = Double.valueOf(0);

            for (int i = 0; i < addressCount; i++) {
                ResponseAddressDTO userAddress = addressList.get(i);
                LatLng latLng = new LatLng( Double.parseDouble(userAddress.getLatitude()),
                        Double.parseDouble(userAddress.getLongitude()));

                latitudeFinal = latitudeFinal + Double.parseDouble(userAddress.getLatitude());
                longitudeFinal = longitudeFinal + Double.parseDouble(userAddress.getLongitude());
                MarkerOptions userMarkerOptions = new MarkerOptions();
                userMarkerOptions.position(latLng);
                userMarkerOptions.title(addressList.get(i).getStreet() + " " +
                        addressList.get(i).getNumber());
                userMarkerOptions.icon( BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                mMap.addMarker(userMarkerOptions);
            }
            LatLng latLngFinal = new LatLng( latitudeFinal / Double.valueOf(addressCount),
                    longitudeFinal / Double.valueOf(addressCount));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngFinal, zoom));

        }

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
}
