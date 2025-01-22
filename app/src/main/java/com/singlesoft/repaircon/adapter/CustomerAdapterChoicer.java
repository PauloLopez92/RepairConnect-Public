package com.singlesoft.repaircon.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.models.Customer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomerAdapterChoicer extends RecyclerView.Adapter<CustomerHolder>{

    private final List<Customer> customerList;

    public CustomerAdapterChoicer(List<Customer> customerList) {
        Collections.reverse(customerList);
        this.customerList = customerList;
        sortCustomersByName();
}
    private void sortCustomersByName() {
        Collections.sort(customerList, new Comparator<Customer>() {
            @Override
            public int compare(Customer s1, Customer s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
    }
    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_item, parent,false);
        return new CustomerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.name.setText(customer.getName());
        holder.contact.setText(customer.getContact());
        holder.number.setText(String.valueOf(customer.getNumServices()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("customer", customer);
                ((Activity) v.getContext()).setResult(Activity.RESULT_OK, resultIntent);
                ((Activity) v.getContext()).finish();
            }
        });
    }
    @Override
    public int getItemCount() {
        return customerList.size();
    }
}