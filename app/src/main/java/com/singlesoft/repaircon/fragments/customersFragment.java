package com.singlesoft.repaircon.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.activitys.addCustomer;
import com.singlesoft.repaircon.adapter.CustomerAdapter;
import com.singlesoft.repaircon.models.Customer;
import com.singlesoft.repaircon.retrofit.CustomerApi;
import com.singlesoft.repaircon.retrofit.RetrofitService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class customersFragment extends Fragment {
    private View v;
    private Context mContext;
    private RecyclerView recyclerView;
    private List<Customer> customerList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // It's important here

        Executor executor = Executors.newFixedThreadPool(10);
        // code to be executed in parallel
        executor.execute(this::loadData);
    }
    // Search field
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        //getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.searchid);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.from_search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    List<Customer> customerSearch = new ArrayList<>(); // create a new list for search results
                    for (Customer customer : customerList){
                        String name = customer.getName();
                        if(name.toLowerCase().startsWith(newText.toLowerCase())){ // compare with case-insensitivity
                            customerSearch.add(customer);
                        }
                    }
                    putInListView(customerSearch); // pass the search results to the function for displaying in ListView
                    return true;
                } catch (Exception e){}
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
        //return super.onCreateOptionsMenu(menu);
    }
    // Get Context for adapters
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers, container, false);
        v = view;

        // Floating add button
        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), addCustomer.class);
                startActivity(intent);
            }
        });

        //To Shows recycleView
        recyclerView = view.findViewById(R.id.recycleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // To load data when open the app
        /*
        Executor executor = Executors.newFixedThreadPool(4);
        executor.execute(new Runnable() {
            public void run() {
                // code to be executed in parallel
            }
        });
        */
        //loadData();
        return view;
    }
    public void update() {
        try {
            loadData();
        }catch (Exception e){}
    }
    private void loadData(){
        SharedPreferences prefs = mContext.getSharedPreferences("Prefs", MODE_PRIVATE);
        // Retrieve the token string
        String tokenString = prefs.getString("token", "");
        RetrofitService service = new RetrofitService(tokenString);
        CustomerApi customerApi = service.getRetrofit().create(CustomerApi.class);
        customerApi.getAllCustomers()
                .enqueue(new Callback<List<Customer>>() {
                    @Override
                    public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                        customerList = response.body();

                        putInListView(customerList);
                    }
                    @Override
                    public void onFailure(Call<List<Customer>> call, Throwable t) {
                        Toast.makeText(getActivity(), "Connection Error: Failed to receive Data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void putInListView(List<Customer> customers) {
        CustomerAdapter customerAdapter =  new CustomerAdapter(customers);
        recyclerView.setAdapter(customerAdapter);

        // To set warning
        LinearLayout notServices = v.findViewById(R.id.notCustomers);
        if(customerList.isEmpty()){
            notServices.setVisibility(View.VISIBLE);
        }else {
            notServices.setVisibility(View.GONE);
        }

    }
}