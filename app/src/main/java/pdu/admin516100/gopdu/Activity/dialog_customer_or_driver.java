package pdu.admin516100.gopdu.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import pdu.admin516100.gopdu.Adapter.MainViewpager;
import pdu.admin516100.gopdu.R;
import pdu.admin516100.gopdu.fragment.CustomerMapsFragment;
import pdu.admin516100.gopdu.fragment.DriverMapsFragment;

public class dialog_customer_or_driver extends DialogFragment {
    public static dialog_customer_or_driver newInstance(String data) {
        dialog_customer_or_driver dialog = new dialog_customer_or_driver();
        Bundle args = new Bundle();
        args.putString("data", data);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_customer_or_driver, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // lấy giá trị tự bundle
        String data = getArguments().getString("data", "");

        ViewPager viewPager = view.findViewById(R.id.viewPagerChoose);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tablayoutCustomerOrDriver);

        MainViewpager mainViewpager = new MainViewpager(getChildFragmentManager());
        mainViewpager.addfragment(new Fragment(),"Customer");
        mainViewpager.addfragment(new Fragment(),"Driver");
        viewPager.setAdapter(mainViewpager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_customer);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_driver);

        Button btnSelect = view.findViewById(R.id.btnSelect);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (tabLayout.getSelectedTabPosition()){
                    case 0:
                        Intent intent_Customer = new Intent(getActivity(), CustomerActivityMain.class);
                        startActivity(intent_Customer);
                        break;
                    case 1:
                        Intent intent_Driver = new Intent(getActivity(), DriverActivityMain.class);
                        startActivity(intent_Driver);
                        break;
                }
            }
        });

    }







}
