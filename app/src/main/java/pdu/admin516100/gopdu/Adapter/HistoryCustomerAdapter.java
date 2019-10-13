package pdu.admin516100.gopdu.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pdu.admin516100.gopdu.Object.HistoryCustomer;
import pdu.admin516100.gopdu.R;
import pdu.admin516100.gopdu.until.until;

public class HistoryCustomerAdapter extends BaseAdapter {

    Context context;
    ArrayList<HistoryCustomer> historyCustomerArrayList;

    public HistoryCustomerAdapter(Context context, ArrayList<HistoryCustomer> historyCustomerArrayList) {
        this.context = context;
        this.historyCustomerArrayList = historyCustomerArrayList;
    }

    @Override
    public int getCount() {
        return historyCustomerArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyCustomerArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.adapter_history_customer, null);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final HistoryCustomer historyCustomer = historyCustomerArrayList.get(position);
        viewHolder.tv_title.setText("Chuyến đi đến "+historyCustomer.getDestination());
        viewHolder.tv_date.setText(""+until.getdate(historyCustomer.getTimestmap()));
        viewHolder.tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BBB", "onClick: "+historyCustomer.getTimestmap());
            }
        });
        return convertView;
    }
    public class ViewHolder{
        TextView tv_title;
        TextView tv_date;
    }
}
