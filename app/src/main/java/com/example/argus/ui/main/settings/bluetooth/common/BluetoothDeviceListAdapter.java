package com.example.argus.ui.main.settings.bluetooth.common;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.argus.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BluetoothDeviceListAdapter extends RecyclerView.Adapter<BluetoothDeviceListAdapter.ViewHolder> {

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
    private int selectedPos = RecyclerView.NO_POSITION;

    public BluetoothDeviceListAdapter(ArrayList<BluetoothDevice> data) {
        super();
        this.dataSet = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtAddress;
        TextView txtType;
        TextView txtBondState;
        public ViewHolder(View view) {
            super(view);
            // Handle item click and set the selection
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Redraw the old selection and the new
                    notifyItemChanged(selectedPos);
                    selectedPos = getLayoutPosition();
                    notifyItemChanged(selectedPos);
                }
            });
            // Define click listener for the ViewHolder's View
            txtName = (TextView) view.findViewById(R.id.name);
            txtAddress = (TextView) view.findViewById(R.id.address);
            txtType = (TextView) view.findViewById(R.id.type);
            txtBondState = (TextView) view.findViewById(R.id.bond_state);
        }
        public TextView getTxtName() { return txtName; }
        public TextView getTxtAddress() { return txtAddress; }
        public TextView getTxtType() { return txtType; }
        public TextView getTxtBondState() { return txtBondState; }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bluetooth_device_row, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
//        if(selectedPos == position){
//            viewHolder.itemView.setBackgroundColor(Color.parseColor("#567845"));
//        }
//        else
//        {
//            viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
//        }
        viewHolder.itemView.setSelected(selectedPos == position);
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTxtName().setText(dataSet.get(position).getName());
        viewHolder.getTxtAddress().setText(dataSet.get(position).getAddress());
        viewHolder.getTxtType().setText(bluetoothDeviceTypes.get(dataSet.get(position).getType()));
        viewHolder.getTxtBondState().setText(bluetoothDevicesBondStates.get(dataSet.get(position).getBondState()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
