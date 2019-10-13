package pdu.admin516100.gopdu.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pdu.admin516100.gopdu.Adapter.HistoryCustomerAdapter;
import pdu.admin516100.gopdu.Object.HistoryCustomer;
import pdu.admin516100.gopdu.R;

public class HistoryCustomerFragment extends Fragment {
    private View view;
    private String userId;
    private ArrayList<HistoryCustomer> historyCustomerArrayList;
    ListView lvHistory;
    HistoryCustomerAdapter historyAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_customer, container, false);
        historyCustomerArrayList = new ArrayList<>();
        userId = FirebaseAuth.getInstance().getUid();
        getHistory();
        anhxa();
        return view;
    }

    private void anhxa() {
        lvHistory = (ListView) view.findViewById(R.id.lvHistory);
        historyAdapter = new HistoryCustomerAdapter(view.getContext(), historyCustomerArrayList);
        lvHistory.setAdapter(historyAdapter);
    }

    private void getHistory() {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(userId).child("history");
        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot history: dataSnapshot.getChildren()){
                        getDataHistory(history.getValue().toString());

                    }
                    Log.d("BBB", "size: "+historyCustomerArrayList.size());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getDataHistory(String key) {

        DatabaseReference historyData = FirebaseDatabase.getInstance().getReference().child("history").child(key);
        historyData.keepSynced(false);
        historyData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HistoryCustomer historyCustomer= new HistoryCustomer();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    if(child.getKey().equals("destination")){
                        historyCustomer.setDestination(child.getValue().toString());
                    }
                    if(child.getKey().equals("distance")){
                        historyCustomer.setDistance(Double.parseDouble(child.getValue().toString()));;
                    }
                    if(child.getKey().equals("pickupname")){
                        historyCustomer.setPickupName(child.getValue().toString());

                    }
                    if(child.getKey().equals("price")){
                        historyCustomer.setPrice(Double.parseDouble(child.getValue().toString()));
                    }
                    if(child.getKey().equals("ratting")){
                        historyCustomer.setRatting(Double.parseDouble(child.getValue().toString()));
                    }
                    if(child.getKey().equals("timestmap")){
                        historyCustomer.setTimestmap((long)child.getValue());
                        Log.d("long", "onDataChange: "+historyCustomer.getTimestmap());
                    }
                    if(child.getKey().equals("location")){
                        historyCustomer.setPickupMarker(new LatLng(Double.parseDouble(child.child("from").child("lat").getValue().toString()),Double.parseDouble(child.child("from").child("lng").getValue().toString())));
                        historyCustomer.setDestinationMarker(new LatLng(Double.parseDouble(child.child("to").child("lat").getValue().toString()),Double.parseDouble(child.child("from").child("lng").getValue().toString())));
                    }

                }
                historyCustomerArrayList.add(historyCustomer);
                historyAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
