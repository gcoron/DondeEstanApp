package com.dondeestanapp.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.dondeestanapp.R;
import com.dondeestanapp.ui.fragments.AccountFragment;
import com.dondeestanapp.ui.fragments.InformationFragment;
import com.dondeestanapp.ui.fragments.MapsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends FragmentActivity {

    private static final int INTERVAL = 2000; //2 segundos para salir
    private long firstClickTime;

    private Integer userId;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = getIntent().getIntExtra("userId", 0);
        userType = getIntent().getStringExtra("userType");

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
        Bundle bundle = new Bundle();
        bundle.putInt("userId", userId);
        bundle.putString("userType", userType);
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
    public void onBackPressed(){
        if (firstClickTime + INTERVAL > System.currentTimeMillis()){
            super.onBackPressed();
            finishAffinity();
        }else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        firstClickTime = System.currentTimeMillis();
    }

}