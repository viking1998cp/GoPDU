package pdu.admin516100.gopdu.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import pdu.admin516100.gopdu.Activity.MainActivity;
import pdu.admin516100.gopdu.R;

public class AboutFragment extends Fragment {
    private View view;
    private Button btnLogOut;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_about_fragment, container, false);
        anhxa();
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }

    private void anhxa() {
        btnLogOut = (Button) view.findViewById(R.id.btnLogOut);
    }
}
