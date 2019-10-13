package pdu.admin516100.gopdu.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import pdu.admin516100.gopdu.R;

public class BannerAdapter extends PagerAdapter {
    int[] image ;
    LayoutInflater layoutInflater;
    Context context;

    public BannerAdapter(int[] image, Context context) {
        this.image = image;
        this.context = context;
    }

    @Override
    public int getCount() {
        return image.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view== object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapterbanner, null);
        ImageView imvBanner = (ImageView) view.findViewById(R.id.imvBanner);
        imvBanner.setBackgroundResource(image[position]);
        container.addView(view);
        return view;
    }
}
