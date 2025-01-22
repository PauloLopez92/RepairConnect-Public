package com.singlesoft.repaircon.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singlesoft.repaircon.R;

public class ServicesHolder extends RecyclerView.ViewHolder {
    TextView model, customerName, price,status;

    public ServicesHolder(@NonNull View itemView) {
        super(itemView);
        model = itemView.findViewById(R.id.modelTextView);
        customerName = itemView.findViewById(R.id.customerTextView);
        price = itemView.findViewById(R.id.priceTextView);
        status = itemView.findViewById(R.id.statusTextView);
    }
}