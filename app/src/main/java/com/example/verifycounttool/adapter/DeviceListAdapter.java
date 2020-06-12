package com.example.verifycounttool.adapter;

import android.bluetooth.BluetoothClass;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.verifycounttool.R;

import java.util.List;

import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceHolder> {
    List<BleDevice> mBleDeviceList;

    public DeviceListAdapter( List<BleDevice> bleDeviceList){
        mBleDeviceList=bleDeviceList;
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_layout, parent, false);
        return  new DeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {
        holder.device.setText(mBleDeviceList.get(position).getBleName());
    }

    @Override
    public int getItemCount() {
        return mBleDeviceList.size();
    }

    class DeviceHolder extends RecyclerView.ViewHolder {
        TextView device;

        public DeviceHolder(@NonNull View itemView) {
            super(itemView);
            device =(TextView)itemView.findViewById(R.id.tv_device);
        }
    }

}
