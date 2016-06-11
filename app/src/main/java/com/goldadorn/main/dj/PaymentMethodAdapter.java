package com.goldadorn.main.dj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.goldadorn.main.R;

import java.util.List;

/**
 * Created by User on 10-06-2016.
 */
public class PaymentMethodAdapter extends BaseAdapter{

        private LayoutInflater inflater;
        private List<String> dataList;

        public PaymentMethodAdapter(Context context, List<String> dataList) {

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.dataList = dataList;
        }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.adapter_payment_mode, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // bindData
            holder.text.setText(getItem(position));
            return convertView;
        }

    private static class ViewHolder {
        TextView text;
    }
}
