package pdu.admin516100.gopdu.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import pdu.admin516100.gopdu.R;

public class SettingDriverFragment extends Fragment {

    private EditText editBirthdate;
    private Button btnBack;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting_driver, container, false);
        super.onViewCreated(view, savedInstanceState);
        anhxa();
        editBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chonngay();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), DriverMapsFragment.class);
                startActivity(intent);
            }
        });
        return view;
    }


    private void chonngay(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            }
        }, 2017,01,01);
        datePickerDialog.show();
    }

    private void anhxa() {
        editBirthdate = (EditText) view.findViewById(R.id.editBirthdate);
        btnBack = (Button) view.findViewById(R.id.btnBack);
    }
}
