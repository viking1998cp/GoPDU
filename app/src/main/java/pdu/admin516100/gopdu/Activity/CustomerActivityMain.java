package pdu.admin516100.gopdu.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pdu.admin516100.gopdu.R;
import pdu.admin516100.gopdu.fragment.AboutFragment;
import pdu.admin516100.gopdu.fragment.CustomerMapsFragment;
import pdu.admin516100.gopdu.fragment.HistoryCustomerFragment;
import pdu.admin516100.gopdu.fragment.SettingCustomerFragment;

public class CustomerActivityMain extends AppCompatActivity {
    private ActionBar toolbar;
    private int back =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        Fragment fragment = new CustomerMapsFragment();
        loadFragment(fragment);
        toolbar();
    }

        private void toolbar() {
            toolbar = getSupportActionBar();

            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_gifts:
                    if(back != 1){
                        back =1;
                        fragment = new CustomerMapsFragment();
                        loadFragment(fragment);
                    }
                    return true;
                case R.id.navigation_cart:
                    if(back != 2){
                        fragment = new HistoryCustomerFragment();
                        loadFragment(fragment);
                        back =2;
                    }

                    return true;
                case R.id.navigation_profile:
                    if(back != 3){
                        fragment = new AboutFragment();
                        loadFragment(fragment);
                        back =3;
                    }
                    return true;
            }

            return false;
        }
    };

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }



}
