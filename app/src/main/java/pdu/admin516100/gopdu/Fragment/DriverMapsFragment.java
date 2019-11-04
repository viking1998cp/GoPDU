package pdu.admin516100.gopdu.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pdu.admin516100.gopdu.Activity.MainActivity;
import pdu.admin516100.gopdu.R;
import pdu.admin516100.gopdu.until.until;

public class DriverMapsFragment extends Fragment implements OnMapReadyCallback, RoutingListener, NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private View view;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private Toolbar toolbar;

    private ImageView imvCustomerAvatar;
    private TextView tvCustomerName, tvCustomerNumberPhone, tvDestination, tvPrice;
    private Button btnRideStatus, btnCancelTrip;
    private CoordinatorLayout coordinatorCustomerInfo;
    private View bottomSheet;

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private String userId ;
    private String customerId , destination;
    private int status = 0;
    private Boolean statusWorking = false;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private float rideDistance;
    private LatLng destinaLatLng;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_driver_maps, container, false);
        setHasOptionsMenu(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        anhxa();
        initPermission();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        polylines = new ArrayList<>();
        anhxa();
        customerId = "";

        mAuth = FirebaseAuth.getInstance();
        check3g();
        laychuyendi();
        toolbar();
        getPriceforService();
        btnRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status){
                    case 1:
                        DatabaseReference customerRefStatus = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(customerId).child("DriverRequest").child("status");
                        customerRefStatus.setValue("moving");
                        status =2;
                        erasePolyLines();
                        if(destinaLatLng.latitude!=0.0 && destinaLatLng.longitude!=0.0){
                            chiduongtoikhachhang(destinaLatLng);
                        }
                        btnCancelTrip.setVisibility(View.GONE);
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                2.0f
                        );
                        LinearLayout.LayoutParams paramCancle = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                0.0f
                        );
                        btnRideStatus.setLayoutParams(param);
                        btnRideStatus.setText("Completed");
                        btnCancelTrip.setLayoutParams(paramCancle);
                        break;
                    case 2:

                        btnCancelTrip.setVisibility(View.VISIBLE);
                        recorRide();
                        endRide();
                        break;

                }
            }
        });

        btnCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endRide();
            }
        });
        return view;

    }

    int priceForService ;
    private void getPriceforService() {
        DatabaseReference driverService  = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(userId).child("service");
        Log.d("price", "getPriceforService: "+userId);
        driverService.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String service = dataSnapshot.getValue().toString();
                    Log.d("price", "onDataChange: "+service);
                    final DatabaseReference price = FirebaseDatabase.getInstance().getReference().child("Service").child(service).child("price");
                    price.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           if(dataSnapshot.exists()){
                               priceForService = Integer.parseInt(dataSnapshot.getValue().toString());

                           }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("price", "onDataChange: "+databaseError.getMessage());
            }
        });
    }


    String statusTrip ="";

    private void given_statusTrip() {
            DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(customerId).child("DriverRequest");
            Log.d("BBB", "given_statusTrip: " + customerId);
             customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("status") != null) {
                            statusTrip = map.get("status").toString();
                        }
                        switch (statusTrip) {
                            case "pickup":
                                status = 1;
                                erasePolyLines();
                                chiduongtoikhachhang(pickupLatLng);
                                pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup Location")
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                                break;
                            case "moving":
                                if (destinaLatLng.latitude != 0.0 && destinaLatLng.longitude != 0.0) {
                                    chiduongtoikhachhang(destinaLatLng);
                                }
                                btnCancelTrip.setVisibility(View.GONE);
                                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        2.0f
                                );
                                LinearLayout.LayoutParams paramCancle = new LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        0.0f
                                );
                                btnRideStatus.setLayoutParams(param);
                                btnRideStatus.setText("Completed");
                                btnCancelTrip.setLayoutParams(paramCancle);
                                status = 2;
                                erasePolyLines();
                                chiduongtoikhachhang(destinaLatLng);
                                break;

                        }


                    } else {
                        statusTrip = "";
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    private void anhxa() {



        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        coordinatorCustomerInfo = (CoordinatorLayout) view.findViewById(R.id.coordinatorCustomerInfo);
        imvCustomerAvatar = (ImageView) view.findViewById(R.id.imvCustomerAvatar);
        tvCustomerName = (TextView) view.findViewById(R.id.tvCustomerName);
        tvCustomerNumberPhone = (TextView) view.findViewById(R.id.tvCustomerPhoneNumber);
        tvDestination = (TextView) view.findViewById(R.id.tvDestination);
        tvPrice = (TextView) view.findViewById(R.id.tvPrice);

        btnRideStatus = (Button) view.findViewById(R.id.btnRideStatus);
        btnCancelTrip = (Button) view.findViewById(R.id.btnCancelTrip);
    }

    public void toolbar() {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
        buildGoogleApiClient();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);



    }
    SwitchCompat switchWorking;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setActionView(R.layout.switch_layout);

        switchWorking = item.getActionView().findViewById(R.id.swWorking);
        switchWorking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(checkInternet){
                        if(isGpsEnabled(view.getContext())){
                            if(!customerId.equals("")) {
                                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                until.ring(view.getContext(), vibrator,5000 );
                            }
                            connectDriver();
                            given_statusTrip();
                        }else {
                            Toast.makeText(view.getContext(),"Bạn không thể sử dụng khi chưa bật vị trí", Toast.LENGTH_SHORT).show();
                            showSettingDialog();
                            switchWorking.setChecked(false);
                        }
                    }else {
                        switchWorking.setChecked(false);
                        Toast.makeText(view.getContext(),"Bạn không thể sử dụng khi chưa bật mạng Internet", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    disconnectDriver();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){

            case ACCESS_FINE_LOCATION_INTENT_ID: {
                while (true){
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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



    GoogleApiClient mGoogleApiClient;;

    protected synchronized void buildGoogleApiClient(){
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
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        Toast.makeText(view.getContext(), "Vị trí được bật", Toast.LENGTH_SHORT).show();
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
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
    private Boolean checkInternet =false;
    private void check3g() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(isOnline(context)){
                    checkInternet = true;
                }else {
                    checkInternet = false;
                    Toast.makeText(view.getContext(),"Bạn chưa bật internet",Toast.LENGTH_SHORT).show();
                    disconnectDriver();

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
        view.getContext().registerReceiver(broadcastReceiver,intentFilter);
    }






    final LocationCallback mlocationCallBack = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if(view.getContext()!=null){

                    mLastLocation = location;
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    if(customerId.equals("")){
                        rideDistance += mLastLocation.distanceTo(location)/1000;
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        if(!polylines.isEmpty()){
                            erasePolyLines();
                        }
                    }
                    DatabaseReference refAvailble = FirebaseDatabase.getInstance().getReference("driversAvailable");
                    DatabaseReference refWorKing = FirebaseDatabase.getInstance().getReference("driverWorking");
                    GeoFire geoAvailble = new GeoFire(refAvailble);
                    GeoFire geoWorking = new GeoFire(refWorKing);
                    switch (customerId){

                        case "":
                            geoWorking.removeLocation(userId);
                            geoAvailble.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;
                        default :
                            geoAvailble.removeLocation(userId);
                            geoWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;
                    }

                }
            }
        }
    };

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(view.getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(view.getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(view.getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Register permission
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Error")
                        .setMessage("Mời bạn cấp quyền truy cập vị trí")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
                            }
                        })
                        .create()
                        .show();
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {

            }
        }
    }

    private void checklocationPermission() {
        if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ){
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Error")
                        .setMessage("Mời bạn cấp quyền truy caoạ vị trí")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
                            }
                        })
                        .create()
                        .show();
            }else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
            }
        }
    }

    private void connectDriver(){
        checklocationPermission();
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mlocationCallBack, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if(mLastLocation != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
        if(!customerId.equals("")){
            coordinatorCustomerInfo.setVisibility(View.VISIBLE);
        }

    }

    private  void disconnectDriver(){
        if(fusedLocationProviderClient != null){
            fusedLocationProviderClient.removeLocationUpdates(mlocationCallBack);
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        checklocationPermission();
        mMap.setMyLocationEnabled(false);
        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if(!polylines.isEmpty()){
            erasePolyLines();
        }
        coordinatorCustomerInfo.setVisibility(View.GONE);

        Log.d("BBB", "connectDriver: "+statusWorking);
    }

    List<Marker> markerList =  new ArrayList<Marker>();


    private void laychuyendi(){

        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("BBB", "getAssignedCustomer: "+driverId);
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(driverId).child("customerRequest").child("customerRideId");
        driverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(switchWorking.isChecked()){
                        coordinatorCustomerInfo.setVisibility(View.VISIBLE);
                        Log.d("BBB", "onDataChange: table on");
                        Vibrator vibrator = (Vibrator) getActivity().getSystemService(view.getContext().VIBRATOR_SERVICE);
                        until.ring(view.getContext(), vibrator,5000 );
                    }
                    bottomSheet =  view.findViewById(R.id.bottom_sheet);
                    BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    status =1;
                    customerId = dataSnapshot.getValue().toString();
                    layvitrikhachhang();
                    laynoidenkhachhang();
                    laythongtinkhachhang();


                    Log.d("AAA", "onDataChange: "+customerId);
                }else {
                    coordinatorCustomerInfo.setVisibility(View.GONE);
                    statusTrip = "";
                    customerId = "";
                    if(pickupMarker != null){
                        pickupMarker.remove();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void laynoidenkhachhang() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("BBB", "getAssignedCustomer: "+driverId);
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(driverId).child("customerRequest");
        driverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("destination")!= null){
                        destination = map.get("destination").toString();
                        tvDestination.setText("Destination: "+destination);
                    }else {
                        tvDestination.setText("Destination: ~~");
                    }
                    Double destinationLat = 0.0;
                    Double destinationlongt = 0.0;
                    if(map.get("destinationLat")!= null){
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if(map.get("destinationLongt")!= null){
                        destinationlongt = Double.valueOf(map.get("destinationLongt").toString());
                    }
                    destinaLatLng = new LatLng(destinationLat,destinationlongt);
                    if(map.get("price") != null){
                        DecimalFormat format = new DecimalFormat("###,###");
                        tvPrice.setText(format.format(map.get("price"))+" VND");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void laythongtinkhachhang() {
        DatabaseReference customerDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(customerId);
        customerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0 ){
                    Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!= null){
                        Log.d("BBB", "onChildAdded: "+map.get("name").toString());
                        tvCustomerName.setText(map.get("name").toString());
                    }
                    if(map.get("phone")!= null){
                        tvCustomerNumberPhone.setText(map.get("phone").toString());
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("BBB", "onCancelled: "+databaseError.toString());
            }
        });
    }

    Marker pickupMarker;
    LatLng pickupLatLng;
    private DatabaseReference customerPickupLocationRef;
    private  ValueEventListener customerPickupLocationRefListener;
    private void layvitrikhachhang() {
        customerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        customerPickupLocationRefListener =  customerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !customerId.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLongi = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLongi = Double.parseDouble(map.get(1).toString());
                    }
                    pickupLatLng = new LatLng(locationLat,locationLongi);
                    Log.d("BBB", "onDataChange: "+map.get(1).toString());
                    pickupMarker=  mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    chiduongtoikhachhang(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Long getCurrentTimeStmap() {
        Long timestmap = System.currentTimeMillis();
        return timestmap;
    }

    private void endRide() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f

        );
        param.setMargins(0,0,30,0);
        LinearLayout.LayoutParams paramCancle = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );
        btnCancelTrip.setLayoutParams(paramCancle);
        btnRideStatus.setLayoutParams(param);
        btnRideStatus.setText("Pickup Complete");
        erasePolyLines();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(userId).child("customerRequest");
        driverRef.removeValue();

        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(customerId).child("DriverRequest");
        customerRef.removeValue();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);
        customerId="";
        rideDistance = 0;

        if(pickupMarker != null){
            pickupMarker.remove();
        }

        erasePolyLines();
        coordinatorCustomerInfo.setVisibility(View.GONE);
        tvCustomerNumberPhone.setText("");
        tvCustomerName.setText("");
        tvDestination.setText("Destination:~~");
        imvCustomerAvatar.setImageResource(R.mipmap.ic_user_info);
        customerId ="";
        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if(customerPickupLocationRefListener != null){
            customerPickupLocationRef.removeEventListener(customerPickupLocationRefListener);
        }
        statusTrip = "";
    }

    private void recorRide(){
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(userId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(customerId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = historyRef.push().getKey();

        driverRef.child(requestId).setValue(requestId);
        customerRef.child(requestId).setValue(requestId);
        HashMap map = new HashMap();
        map.put("driverId", userId);
        map.put("customerId", customerId);
        map.put("rating", 0);
        map.put("timestmap", getCurrentTimeStmap());
        map.put("destination", destination);
        float price = 0;
        if(0<=rideDistance && rideDistance<1){
            price = priceForService;
        }else {
            price = rideDistance*priceForService;
        }
        map.put("price", price);
        map.put("pickupname", until.getAddressNameFromLocation(pickupLatLng, view.getContext()));
        map.put("distance",rideDistance);
        historyRef.child(requestId).setValue(map);
        if( pickupLatLng!= null && destinaLatLng!= null) {

            DatabaseReference locationLng = FirebaseDatabase.getInstance().getReference().child("history").child(requestId).child("location");
            locationLng.child("from").child("lat").setValue(pickupLatLng.latitude);
            locationLng.child("from").child("lng").setValue(pickupLatLng.longitude);
            locationLng.child("to").child("lat").setValue(destinaLatLng.latitude);
            locationLng.child("to").child("lng").setValue(destinaLatLng.longitude);
        }


    }


    private void chiduongtoikhachhang(LatLng pickupLatLng) {
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
            Toast.makeText(view.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("BBB", "onRoutingFailure: "+e.getMessage().toString());
        }else {
            Toast.makeText(view.getContext(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

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

            Toast.makeText(view.getContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_logout:
                mAuth.signOut();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;

        }
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
