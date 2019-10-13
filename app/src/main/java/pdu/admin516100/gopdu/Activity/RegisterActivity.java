package pdu.admin516100.gopdu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import pdu.admin516100.gopdu.Adapter.MainViewpager;
import pdu.admin516100.gopdu.R;
import pdu.admin516100.gopdu.until.until;

public class RegisterActivity extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tableLayout;
    Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        anhxa();
        MainViewpager mainViewpager = new MainViewpager(getSupportFragmentManager());
        mainViewpager.addfragment(new Fragment(),"Customer");
        mainViewpager.addfragment(new Fragment(),"Driver");
        viewPager.setAdapter(mainViewpager);
        tableLayout.setupWithViewPager(viewPager);
        tableLayout.getTabAt(0).setIcon(R.drawable.ic_customer);
        tableLayout.getTabAt(1).setIcon(R.drawable.ic_driver);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(until.checkConnect(RegisterActivity.this)){
                    switch (tableLayout.getSelectedTabPosition()){
                        case 0:
                            Intent intent_Customer = new Intent(RegisterActivity.this, RegisterCustomerActivity.class);
                            startActivity(intent_Customer);
                            break;
                        case 1:
                            Intent intent_Driver = new Intent(RegisterActivity.this, RegisterDriverActivity.class);
                            startActivity(intent_Driver);
                            break;
                    }
                }else {
                    Toast.makeText(RegisterActivity.this,"Kiểm tra kết nối internet",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void anhxa() {
        viewPager = (ViewPager) findViewById(R.id.viewPagerLogin);
        tableLayout = (TabLayout) findViewById(R.id.tablayoutLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }
}
