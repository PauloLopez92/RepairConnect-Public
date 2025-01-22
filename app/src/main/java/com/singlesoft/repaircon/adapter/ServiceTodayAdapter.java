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
import com.singlesoft.repaircon.activitys.servicePage;
import com.singlesoft.repaircon.models.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ServiceTodayAdapter extends RecyclerView.Adapter<ServicesHolder>{
    private final List<Service> serviceList;
    private Context context;

    public ServiceTodayAdapter(Context context, List<Service> serviceList) {
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

        // To Set hour time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String time = timeFormat.format(service.getPrevisionTime());
        holder.price.setText(time);

        // To set Status
        if(service.getStatus() != 4){
            Calendar currentDate = Calendar.getInstance();
            Calendar timestampDate = Calendar.getInstance();
            timestampDate.setTimeInMillis(service.getPrevisionTime().getTime());
            if(currentDate.get(Calendar.HOUR_OF_DAY) < timestampDate.get(Calendar.HOUR_OF_DAY) || (currentDate.get(Calendar.HOUR_OF_DAY) == timestampDate.get(Calendar.HOUR_OF_DAY) && currentDate.get(Calendar.MINUTE) < timestampDate.get(Calendar.MINUTE))){
                holder.status.setText(context.getString(R.string.from_todo));
                holder.status.setTextColor(Color.parseColor("#0390db"));
            }else {
                holder.status.setText(context.getString(R.string.from_late));
                holder.status.setTextColor(Color.parseColor("#db1503"));
            }
        }else {
            holder.status.setText(context.getString(R.string.from_finished));
            holder.status.setTextColor(Color.parseColor("#03de8e"));
        }
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
