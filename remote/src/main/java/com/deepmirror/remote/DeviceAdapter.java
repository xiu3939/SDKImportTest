package com.deepmirror.remote;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DeviceAdapter extends BaseAdapter {
    private List<BluetoothDevice> dataList;
    private LayoutInflater inflater;

    static class ViewHolder {
        TextView tvName, tvMac;
    }

    public DeviceAdapter(List<BluetoothDevice> dataList, LayoutInflater inflater) {
        this.dataList = dataList;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.list_scan_device, viewGroup, false);
            viewHolder.tvName = view.findViewById(R.id.tv_device_name);
            viewHolder.tvMac = view.findViewById(R.id.tv_device_mac);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvName.setText(dataList.get(i).getName());
        viewHolder.tvMac.setText(dataList.get(i).getAddress());
        return view;
    }
}
