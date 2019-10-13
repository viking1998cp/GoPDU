package pdu.admin516100.gopdu.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pdu.admin516100.gopdu.Activity.MainActivity;
import pdu.admin516100.gopdu.Adapter.MainViewpager;
import pdu.admin516100.gopdu.R;
import pdu.admin516100.gopdu.until.until;

public class CustomerMapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener, RoutingListener {
    private View view;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private LinearLayout lnCallUber, ln_DriverInfo;
    private TextView tvDriverName, tvDriverNumber, tvDriverGender, tvPrice;
    private RatingBar ratingBar;
    private RadioButton rdMoney, rdPayment;
    private String userId;
    private TabLayout tabLayout;
    private Button btnCall;
    private View bottomSheet;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mMapFragment;
    LocationRequest mLocationRequest;
    private Marker driverMarker;
    Location mLastLocation;
    private Boolean requestBoolena = false;
    private String statusTrip;
    private Marker pickupMarker;
    SupportMapFragment mapFragment;
    private LatLng pickuplocation, destinationLalng;
    private String destination;
    private int requestService = 1;
    private Marker destinationMarker;
    private int distance = 0;
    private int pricefor_onekm = 0;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    AutocompleteSupportFragment autocompleteFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_maps, container, false);

        buildGoogleApiClient();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        anhxa();

        polylines = new ArrayList<>();
        userId = FirebaseAuth.getInstance().getUid();
        destinationLalng = new LatLng(0.0, 0.0);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mapFragment.getMapAsync(this);
        }
        searchLocation();
        check3g();
        given_pricefor_onekm();
        showSettingDialog();

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                requestService = tab.getPosition() + 1;
                given_pricefor_onekm();
                Log.d("BBB", "onTabSelected: " + distance);

                Log.d("BBB", "onDataChange: " + pricefor_onekm);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternet) {
                    if (isGpsEnabled(view.getContext())) {
                        if (mLastLocation != null) {
                            if (destination != null) {
                                DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(userId).child("DriverRequest");
                                customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            requestBoolena = true;
                                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                            if (map.get("status") != null) {
                                                statusTrip = map.get("status").toString();
                                            }

                                        } else {
                                            statusTrip = "";

                                        }
                                        endRide();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            } else {
                                Toast.makeText(view.getContext(), "Vui lòng chọn điểm đến của bạn", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(view.getContext(), "Vui lòng chờ chúng tôi sẽ xác định vị trí của bạn", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(view.getContext(), "Bạn không thể sử dụng khi chưa bật vị trí", Toast.LENGTH_SHORT).show();
                        showSettingDialog();
                    }
                } else {
                    Toast.makeText(view.getContext(), "Bạn không thể sử dụng khi chưa bật mạng Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        given_statusTrip();
        return view;
    }

    private void anhxa() {



        tabLayout = view.findViewById(R.id.tablayoutCustomerMap);
        viewPager = view.findViewById(R.id.viewPagerCustomerMap);

        MainViewpager mainViewpager = new MainViewpager(getFragmentManager());
        mainViewpager.addfragment(new Fragment(), "Bike");
        mainViewpager.addfragment(new Fragment(), "Car");
        mainViewpager.addfragment(new Fragment(), "Car Plus");
        viewPager.setAdapter(mainViewpager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_bike);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_car);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_car_luxurios);

        btnCall = view.findViewById(R.id.btnCall);

        ln_DriverInfo = view.findViewById(R.id.ln_DriverInfo);
        lnCallUber = view.findViewById(R.id.lnCallUber);
        tvDriverName = view.findViewById(R.id.tvdDriverName);
        tvDriverNumber = view.findViewById(R.id.tvDriverPhoneNumber);
        tvDriverGender = view.findViewById(R.id.tvDriverGender);
        tvPrice = view.findViewById(R.id.tvPrice);
        ratingBar = view.findViewById(R.id.ratingBar);
        rdMoney = view.findViewById(R.id.rdMoney);
        rdPayment = view.findViewById(R.id.rdPayment);
        ratingBar.setEnabled(false);
        rdMoney.setChecked(true);
        bottomSheet =  view.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            view.getContext(), R.raw.map_style_night));

            if (!success) {
                Log.e("'BBB", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("BBB", "Can't find style. Error: ", e);
        }
        if (ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (view.getContext() != null) {
            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (destination == null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ACCESS_FINE_LOCATION_INTENT_ID: {
                while (true) {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {

                        //If permission granted show location dialog if APIClient is not null
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                            showSettingDialog();
                        } else
                            showSettingDialog();


                    } else {
                        Toast.makeText(view.getContext(), "Location Permission denied.", Toast.LENGTH_SHORT).show();

                        break;
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }
                return;
            }
            default:
        }
    }

    private void searchLocation() {
        Places.initialize(view.getContext(), "AIzaSyBAl-_gUg9VuQ-w-nlace8wPVEz2UTcaEU");

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(view.getContext());
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragment.getView().setBackgroundResource(R.drawable.duongvien_goctron);

        autocompleteFragment.setHint("Vui lòng chọn điểm đến");

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME));

// Set up a PlaceSelectionListener to handle the response.


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (isGpsEnabled(view.getContext())) {
                    if (place.getLatLng() != null) {
                        destination = until.getAddressNameFromLocation(place.getLatLng(), getContext());
                        if (destinationLalng != null && destinationMarker != null) {
                            destinationMarker.remove();
                            destinationLalng = null;
                            destinationLalng = place.getLatLng();
                            destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLalng).title(destination));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLalng, 15));
                        } else {
                            destinationLalng = place.getLatLng();
                            destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLalng).title(destination));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLalng, 15));
                        }
                        if(!polylines.isEmpty()){
                            erasePolyLines();
                        }
                        chiduong(destinationLalng);
                        getDistance();


                    } else {
                        autocompleteFragment.setText("");
                        Toast.makeText(view.getContext(), "Đang xác định vị trí của bạn vui lòng chờ", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    destination = null;
                    autocompleteFragment.setText("");
                    Toast.makeText(view.getContext(), "Bạn cần bật vị trí trước khi thực hiện tìm kiếm", Toast.LENGTH_SHORT).show();
                    showSettingDialog();
                }


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });

        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (destinationMarker != null) {
                            destinationMarker.remove();
                            destination = null;
                        }
                        distance = 0;
                        destination =null;
                        autocompleteFragment.setText("");

                        if(mLastLocation!= null){
                            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

                        }
                        tvPrice.setText("0 VND");
                        if(!polylines.isEmpty()){
                            erasePolyLines();
                        }

                    }
                });
    }

    private void given_statusTrip() {
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(userId).child("DriverRequest");
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    requestBoolena = true;
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("status") != null) {
                        statusTrip = map.get("status").toString();
                    }
                    if (map.get("driverRideId") != null) {
                        driverFoundId = map.get("driverRideId").toString();
                        driverFound = true;
                        getHasRideEnded();
                        laythongtintaixe();
                        getDriverlocation();
                    }

                    Double destinationLat = 0.0;
                    Double destinationlongt = 0.0;
                    if(map.get("destinationLat")!= null){
                        destinationLat = Double.parseDouble(map.get("destinationLat").toString()) ;

                    }
                    if(map.get("destinationLongt")!= null){
                        destinationlongt = Double.parseDouble(map.get("destinationLongt").toString()) ;
                    }
                    destinationLalng = new LatLng(destinationLat, destinationlongt);
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLalng).title(destination));
                    if (map.get("destination") != null) {
                        destination = map.get("destination").toString();
                    }
                    Log.d("AAA", "onDataChange: "+destination);
                    Log.d("AAA", "onDataChange: "+destinationLalng.latitude+"/"+destinationLalng.longitude);

                    Thread t1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true){
                                if(mLastLocation == null){

                                }else {
                                    chiduong(destinationLalng);

                                    break;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    t1.start();
                    autocompleteFragment.getView().setVisibility(View.GONE);

                } else {
                    statusTrip = "";

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int given_price(int pricefor_onekm, int distance) {
        int price = 0;
        if (distance >= 1.0) {
            price = pricefor_onekm * distance;
        } else {
            switch (requestService) {
                case 1:
                    price = 5000;
                    break;
                case 2:
                    price = 8000;
                    break;
                case 3:
                    price = 10000;
            }
        }
        return price;
    }

    private void given_pricefor_onekm() {
        DatabaseReference mService = FirebaseDatabase.getInstance().getReference().child("Service").child(String.valueOf(String.valueOf(requestService)));
        mService.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("price") != null) {
                        pricefor_onekm = Integer.parseInt(map.get("price").toString());
                        Log.d("BBB", "onDataChange: " + pricefor_onekm);
                        if (distance != 0 && pricefor_onekm != 0) {
                            DecimalFormat format = new DecimalFormat("###,###");
                            tvPrice.setText(format.format(given_price(pricefor_onekm, distance)) + " VND");
                            Log.d("BBB", "onDataChange: " + given_price(pricefor_onekm, distance));
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }













    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(view.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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
                 Status status = result.getStatus();
                 LocationSettingsStates state = result.getLocationSettingsStates();

                Log.d("yy", "onResult: >>"+state);
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.


                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {

                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }

                        Toast.makeText(view.getContext(), "Vị trí không được bật", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:


                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
        if(isGpsEnabled(view.getContext())){
            autocompleteFragment.setText("");
            destination = null;
            destinationLalng = null;
        }
    }

    private Boolean checkInternet = false;

    private void check3g() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (isOnline(context)) {
                    checkInternet = true;
                } else {
                    checkInternet = false;
                    Toast.makeText(view.getContext(), "Bạn chưa bật internet", Toast.LENGTH_SHORT).show();


                }


            }

            public boolean isOnline(Context context) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                //should check null because in airplane mode it will be null
                return (netInfo != null && netInfo.isConnected());
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        view.getContext().registerReceiver(broadcastReceiver, intentFilter);
    }




    private void endRide() {
        if (requestBoolena) {
            if(statusTrip.equals("moving")){
                Toast.makeText(view.getContext(), "Chuyến đi đang thực hiện không thể hủy", Toast.LENGTH_SHORT).show();
            }else {
                requestBoolena = false;
                ln_DriverInfo.setVisibility(View.GONE);
                lnCallUber.setVisibility(View.VISIBLE);
                if(autocompleteFragment.getView() != null){
                    autocompleteFragment.getView().setVisibility(View.VISIBLE);
                    autocompleteFragment.setText("");
                }

                if (geoQuery != null) {
                    geoQuery.removeAllListeners();
                }
                if (driverFoundId != null) {
                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(driverFoundId).child("customerRequest");
                    driverRef.removeValue();
                    driverlocationRef.removeEventListener(driverlocationRefListener);
                    driverHasEndedRef.removeEventListener(driverHasEndedEventListener);
                    Log.d("BBB", "endRide: " + driverFoundId);
                    driverFoundId = null;
                    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(userId).child("DriverRequest");
                    customerRef.removeValue();
                }
                if (destinationMarker != null) {
                    destinationMarker.remove();
                    destination = null;
                }
                distance = 0;
                destination =null;

                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                tvPrice.setText("0 VND");
                driverFound = false;
                radius = 1;
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userId);

                if (pickupMarker != null) {
                    pickupMarker.remove();
                }
                if (driverMarker != null) {
                    driverMarker.remove();
                }
                btnCall.setText("Tìm tài xế");
                tvDriverName.setText("");
                tvDriverGender.setText("");
                tvDriverNumber.setText("");
                if(!polylines.isEmpty()){
                    erasePolyLines();
                }
            }



        } else {

            Log.d("BBB", "endRide: " + requestService);

            if (driverFound) {
                return;
            }
            autocompleteFragment.getView().setVisibility(View.GONE);
            requestBoolena = true;
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            pickuplocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            pickupMarker = mMap.addMarker(new MarkerOptions().position(pickuplocation).title("Đón tôi ở đây").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
            btnCall.setText("Đang tìm tài xế cho bạn......");
            laytaixe();

        }
    }

    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundId;
    GeoQuery geoQuery;

    private void laytaixe() {
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickuplocation.latitude, pickuplocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, GeoLocation location) {
                if (driverFound == false && requestBoolena) {
                    DatabaseReference customerDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(key);
                    customerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound) {
                                    return;
                                }
                                if (Integer.parseInt(String.valueOf(driverMap.get("service"))) == requestService) {
                                    driverFound = true;
                                    driverFoundId = dataSnapshot.getKey();

                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(driverFoundId).child("customerRequest");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap hashMapDriver = new HashMap();
                                    hashMapDriver.put("customerRideId", customerId);
                                    hashMapDriver.put("service", requestService);
                                    hashMapDriver.put("destination", destination);//Tìm thấy tài xế
                                    hashMapDriver.put("destinationLat", destinationLalng.latitude);
                                    hashMapDriver.put("destinationLongt", destinationLalng.longitude);
                                    hashMapDriver.put("price", given_price(pricefor_onekm, distance));
                                    driverRef.updateChildren(hashMapDriver);

                                    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(userId).child("DriverRequest");
                                    HashMap hashMapCustomer = new HashMap();
                                    hashMapCustomer.put("driverRideId", key);
                                    hashMapCustomer.put("destination", destination);
                                    hashMapCustomer.put("destinationLat", destinationLalng.latitude);
                                    hashMapCustomer.put("destinationLongt", destinationLalng.longitude);
                                    hashMapCustomer.put("price", given_price(pricefor_onekm, distance));
                                    hashMapCustomer.put("status", "pickup");
                                    customerRef.updateChildren(hashMapCustomer);
                                    btnCall.setText("Xác định địa điểm của tài xế...");
                                    laythongtintaixe();
                                    getDriverlocation();
                                    getHasRideEnded();
                                    Log.d("BBB", "onKeyEntered: " + customerId);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (driverFound == false && requestBoolena) {
                    radius++;
                    laytaixe();
                    Log.d("BBB", "onGeoQueryReady: " + radius + "");
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private DatabaseReference driverlocationRef;
    private ValueEventListener driverlocationRefListener;

    public void getDriverlocation() {
        ln_DriverInfo.setVisibility(View.VISIBLE);
        lnCallUber.setVisibility(View.GONE);
        driverlocationRef = FirebaseDatabase.getInstance().getReference().child("driverWorking").child(driverFoundId).child("l");
        driverlocationRefListener = driverlocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLongi = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLongi = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat, locationLongi);
                    if (driverMarker != null) {
                        driverMarker.remove();
                    }

                    btnCall.setText("Huỷ chuyến đi");
                    Log.d("BBB", "onDataChange: " + map.get(1).toString());
                    driverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your Driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng,15));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void laythongtintaixe() {

        DatabaseReference customerDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(driverFoundId);
        customerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        Log.d("BBB", "onChildAdded: " + map.get("name").toString());
                        tvDriverName.setText(map.get("name").toString());
                    }
                    if (map.get("phone") != null) {
                        tvDriverNumber.setText(map.get("phone").toString());
                    }
                    if (map.get("gender") != null) {
                        tvDriverGender.setText(map.get("gender").toString());
                    }

                    int ratingSum = 0;
                    int ratingTotal = 0;
                    float ratingAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()) {
                        ratingSum = ratingSum + Integer.parseInt(child.getValue().toString());
                        ratingTotal++;
                    }
                    if (ratingTotal != 0) {
                        ratingAvg = ratingSum / ratingAvg;
                        ratingBar.setRating(ratingAvg);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("BBB", "onCancelled: " + databaseError.toString());
            }
        });

    }

    private DatabaseReference driverHasEndedRef;
    ValueEventListener driverHasEndedEventListener;

    private void getHasRideEnded() {
        driverHasEndedRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(userId).child("DriverRequest").child("driverRideId");
        driverHasEndedEventListener = driverHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                } else {
                    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(userId).child("DriverRequest");
                    customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                requestBoolena = true;
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                if (map.get("status") != null) {
                                    statusTrip = map.get("status").toString();
                                }

                            } else {
                                statusTrip = "";

                            }
                            endRide();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public void getDistance() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        final String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&destination=" + destinationLalng.latitude + "," + destinationLalng.longitude + "&sensor=false&units=metric&mode=biking&key=AIzaSyDGulByRh-xg8ytGTIXBRL0PSCeEjN4SCc";
        Log.d("BBB", "getDistance: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    // routesArray contains ALL routes
                    JSONArray routesArray = jsonObject.getJSONArray("routes");
                    // Grab the first route
                    JSONObject route = routesArray.getJSONObject(0);
                    // Take all legs from the route
                    JSONArray legs = route.getJSONArray("legs");
                    // Grab first leg
                    JSONObject leg = legs.getJSONObject(0);
                    //take travel distance
                    JSONObject distanceObject = leg.getJSONObject("distance");
                    distance = (int) Integer.parseInt(String.valueOf(Math.round(Double.parseDouble(distanceObject.getString("value")) / 1000)));
                    DecimalFormat format = new DecimalFormat("###,###");
                    tvPrice.setText(format.format(given_price(pricefor_onekm, distance)) + " VND");
                    //Take travel duration
                    JSONObject durationObject = leg.getJSONObject("duration");
                    String duration = durationObject.getString("text");
                    Log.d("BBB", "onResponse: "+url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("BBB", "onErrorResponse: " + error.toString());
            }
        });
        queue.add(stringRequest);

        Log.d("BBB", "getDistance: " + distance);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                mAuth.signOut();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            default:
                break;

        }

        menuItem.setChecked(true);
        return false;
    }

    private void chiduong(LatLng pickupLatLng) {
        if(pickupLatLng != null  && mLastLocation != null){
            Routing routing = new Routing.Builder()
                    .key("AIzaSyCNwMTXZfiE5MbQuItG7A8ewq2dK16r_s8")
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()), pickupLatLng)
                    .build();
            routing.execute();
        }
    }

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Log.d("BBB", "onRoutingFailure: "+e.toString());
            Toast.makeText(view.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(view.getContext(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onRoutingStart() {
        for (Polyline line: polylines){
            line.remove();
        }
        polylines.clear();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polyline.setColor(Color.GREEN);
            polylines.add(polyline);

            Toast.makeText(view.getContext(),"Thời gian di chuyển khoảng: "+route.get(i).getDurationText(),Toast.LENGTH_SHORT).show();
            route.get(i).getDistanceValue();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }
    public void erasePolyLines(){
        for (Polyline line: polylines){
            line.remove();
        }
        polylines.clear();
    }
    private boolean isGpsEnabled(Context context) {
        ContentResolver contentResolver = view.getContext().getContentResolver();
        // Find out what the settings say about which providers are enabled
        //  String locationMode = "Settings.Secure.LOCATION_MODE_OFF";
        int mode = Settings.Secure.getInt(
                contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
        if (mode != Settings.Secure.LOCATION_MODE_OFF) {
            return true;

        } else {
            return false;
        }
    }


}
