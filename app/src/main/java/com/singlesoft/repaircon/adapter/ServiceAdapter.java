package com.singlesoft.repaircon.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.models.Service;
import com.singlesoft.repaircon.activitys.servicePage;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServicesHolder>{
    private final List<Service> serviceList;
    private Context context;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        sortServiceListByModel();
    }
    private void sortServiceListByModel() {
        Collections.sort(serviceList, new Comparator<Service>() {
            @Override
            public int compare(Service s1, Service s2) {
                return s1.getModel().compareToIgnoreCase(s2.getModel());
            }
        });
    }
    @NonNull
    @Override
    public ServicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_item, parent,false);
        return new ServicesHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ServicesHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.model.setText(service.getModel());
        holder.customerName.setText(service.getCustomerName());
        // Price settings
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.getDefault()));
        String formattedPrice = df.format(service.getFinalPrice()).replace(".", ",");
        holder.price.setText(context.getString(R.string.from_money) + formattedPrice);
        // Status settings
        String[] options = {
                context.getString(R.string.from_budget),
                context.getString(R.string.from_authorized),
                context.getString(R.string.from_noFix),
                context.getString(R.string.from_bench),
                context.getString(R.string.from_finished)
        };
        //int[] colors = {Color.GRAY, Color.BLUE, Color.RED, Color.BLUE, Color.GREEN};
        int[] colors = {Color.parseColor("#adabaa"), Color.parseColor("#0390db"), Color.parseColor("#db1503"), Color.parseColor("#4803de"), Color.parseColor("#03de8e")};
        holder.status.setText(options[service.getStatus()]);
        holder.status.setTextColor(colors[service.getStatus()]);
        // OnClick settings
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), servicePage.class);
                intent.putExtra("service", service);
                v.getContext().startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return serviceList.size();
    }
}
