package com.example.argus.ui.main.settings.common;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.argus.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BluetoothDeviceListAdapter extends BaseAdapter {

    private HashMap<Integer, String> bluetoothDeviceTypes = new HashMap<Integer, String>() {{
            put(1, "Miscellaneous");
            put(2, "Computer");
            put(3, "Phone");
            put(4, "LAN/Network");
            put(5, "Audio/Video");
            put(6, "Peripheral");
            put(7, "Imaging");
            put(8, "Wearable");
            put(9, "Toy");
            put(10, "Health");
            put(11, "Uncategorized; device code not specified");
            put(12, "Reserved");
    }};

    private HashMap<Integer, String> bluetoothDevicesBondStates = new HashMap<Integer, String>() {{
        put(10, "Not bonded");
        put(11, "Bonding");
        put(12, "Bonded");
    }};

    private static final String TAG = "TEST";
    private ArrayList<BluetoothDevice> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtAddress;
        TextView txtType;
        TextView txtBondState;
    }

    public BluetoothDeviceListAdapter(ArrayList<BluetoothDevice> data, Context context) {
        super();
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public BluetoothDevice getItem(int i) {
        return dataSet.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.bluetooth_device_row, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtAddress = (TextView) convertView.findViewById(R.id.address);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.txtBondState = (TextView) convertView.findViewById(R.id.bond_state);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.txtName.setText(device.getName());
        viewHolder.txtAddress.setText(device.getAddress());
        viewHolder.txtType.setText(bluetoothDeviceTypes.get(device.getType()));
        viewHolder.txtBondState.setText(bluetoothDevicesBondStates.get(device.getBondState()));



        // Return the completed view to render on screen
        return convertView;
    }
}
