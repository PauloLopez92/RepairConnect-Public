package com.singlesoft.repaircon.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singlesoft.repaircon.R;

public class UserHolder extends RecyclerView.ViewHolder {
    TextView name,type, number;

    public UserHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.customerNameTextView);
        type = itemView.findViewById(R.id.customerContactTextView);
        number = itemView.findViewById(R.id.customerServicesNumberTextView);
    }
}