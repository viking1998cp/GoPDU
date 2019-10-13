package pdu.admin516100.gopdu.until;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pdu.admin516100.gopdu.R;

public class until {
    public static Dialog dialog;
    public  until() {
    }
    public static boolean checkConnect(Context context){
        boolean haveConnectWfi = false;
        boolean haveConnectMobile = false;
        ConnectivityManager cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for(NetworkInfo ni: networkInfos){
            if(ni.getTypeName().equalsIgnoreCase("WIFI")){
                if(ni.isConnected()){
                    haveConnectWfi = true;
                }
            }
            if(ni.getTypeName().equalsIgnoreCase("MOBILE")){
                if(ni.isConnected()){
                    haveConnectMobile = true;
                }
            }
        }
        return haveConnectMobile || haveConnectWfi;
    }
    public String editPhoneNumber(String phone){
        String newphone="+84";
        if(phone.startsWith("0")){
            newphone = newphone + phone.substring(1,phone.length());
            return  newphone;
        }else {
            return phone;
        }
    }




    public static boolean isGpsEnabled(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
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

    public static void ring(Context context, Vibrator vibrator , Integer time){
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(time); // for 500 ms
        }
    }
    public  static void cancelRing(Context context, Vibrator vibrator ){
        vibrator.cancel();
    }
    public static String getAddressNameFromLocation(LatLng latLng, Context context){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String address = "";
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();

            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            Log.d("BBB", "getAddressNameFromLocation: "+city+" "+state+" "+country+" "+knownName+" "+postalCode );
        } catch (IOException e) {

        }
        return address;
    }

    public static String getdate(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time*100);
        String date = android.text.format.DateFormat.format("dd-MM",cal).toString();
        return date;
    }
    public static void showdiaglog(Context context){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_processbar);
        ImageView imv  = dialog.findViewById(R.id.imv_loadingIcon);

    }
}
