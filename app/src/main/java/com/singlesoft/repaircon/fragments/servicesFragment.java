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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
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
import com.singlesoft.repaircon.activitys.addService;
import com.singlesoft.repaircon.adapter.ServiceAdapter;
import com.singlesoft.repaircon.models.Service;
import com.singlesoft.repaircon.models.servicesViewModel;
import com.singlesoft.repaircon.retrofit.RetrofitService;
import com.singlesoft.repaircon.retrofit.ServiceApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class servicesFragment extends Fragment {

    private View v;
    private Context mContext;
    private RecyclerView recyclerView;
    private List<Service> servicesList;

    private String userType;

    private servicesViewModel data;

    public servicesFragment(String userType) {
        this.userType = userType;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // It's important here

        // To load data when open the app
        Executor executor = Executors.newFixedThreadPool(10);
        // code to be executed in parallel
        executor.execute(this::loadData);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        //getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.searchid);
        MenuItem all = menu.findItem(R.id.all);
        MenuItem budget = menu.findItem(R.id.budget);
        MenuItem auth = menu.findItem(R.id.auth);
        MenuItem bench = menu.findItem(R.id.bench);
        MenuItem nofix = menu.findItem(R.id.nofix);
        MenuItem finished = menu.findItem(R.id.finished);

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
                    List<Service> servicesSearch = new ArrayList<>(); // create a new list for search results
                    for (Service service : servicesList){
                        String model = service.getModel();
                        if(model.toLowerCase().startsWith(newText.toLowerCase())){ // compare with case-insensitivity
                            servicesSearch.add(service);
                        }
                    }
                    InListView(servicesSearch); // pass the search results to the function for displaying in ListView
                    return true;
                } catch (Exception e){}

                //System.out.println(servicesList);
                return false;
            }
        });
        all.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    List<Service> servicesSearch = new ArrayList<>(); // create a new list for search results
                    for (Service service : servicesList){
                            servicesSearch.add(service);
                    }
                    InListView(servicesSearch); // pass the search results to the function for displaying in ListView
                } catch (Exception e){}
                //System.out.println(servicesList);
                return true;
            }
        });
        budget.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    List<Service> servicesSearch = new ArrayList<>(); // create a new list for search results
                    for (Service service : servicesList){
                        if(service.getStatus() == 0){ // compare with case-insensitivity
                            servicesSearch.add(service);
                        }
                    }
                    InListView(servicesSearch); // pass the search results to the function for displaying in ListView
                } catch (Exception e){}

                //System.out.println(servicesList);
                return true;
            }
        });
        auth.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    List<Service> servicesSearch = new ArrayList<>(); // create a new list for search results
                    for (Service service : servicesList){
                        if(service.getStatus() == 1){ // compare with case-insensitivity
                            servicesSearch.add(service);
                        }
                    }
                    InListView(servicesSearch); // pass the search results to the function for displaying in ListView
                } catch (Exception e){}
                //System.out.println(servicesList);
                return true;
            }
        });
        nofix.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    List<Service> servicesSearch = new ArrayList<>(); // create a new list for search results
                    for (Service service : servicesList){
                        if(service.getStatus() == 2){ // compare with case-insensitivity
                            servicesSearch.add(service);
                        }
                    }
                    InListView(servicesSearch); // pass the search results to the function for displaying in ListView
                } catch (Exception e){}
                //System.out.println(servicesList);
                return true;
            }
        });
        bench.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    List<Service> servicesSearch = new ArrayList<>(); // create a new list for search results
                    for (Service service : servicesList){
                        if(service.getStatus() == 3){ // compare with case-insensitivity
                            servicesSearch.add(service);
                        }
                    }
                    InListView(servicesSearch); // pass the search results to the function for displaying in ListView
                } catch (Exception e){}
                //System.out.println(servicesList);
                return true;
            }
        });

        finished.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    List<Service> servicesSearch = new ArrayList<>(); // create a new list for search results
                    for (Service service : servicesList){
                        if(service.getStatus() == 4){ // compare with case-insensitivity
                            servicesSearch.add(service);
                        }
                    }
                    InListView(servicesSearch); // pass the search results to the function for displaying in ListView
                } catch (Exception e){}
                //System.out.println(servicesList);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
        //return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        v = view;

        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        if (userType.equals("USER")) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), addService.class);
                    startActivity(intent);
                }
            });
        }else{
            fab.setVisibility(View.GONE);
        }



        //To Shows recycleView
        recyclerView = view.findViewById(R.id.recyclerList);
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
        // Obtain the SharedPreferences object
        SharedPreferences prefs = mContext.getSharedPreferences("Prefs", MODE_PRIVATE);
        // Retrieve the token string
        String tokenString = prefs.getString("token", "");

        RetrofitService service = new RetrofitService(tokenString);
        ServiceApi serviceApi = service.getRetrofit().create(ServiceApi.class);

        if (userType.equals("USER")) {
            serviceApi.getUserServices().enqueue(new Callback<List<Service>>() {
                @Override
                public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                    servicesList = response.body();
                    // To set data to transfer serviceList to today fragment
                    data = new ViewModelProvider(requireActivity()).get(servicesViewModel.class);
                    data.setDataList(servicesList);
                    InListView(servicesList);
                }

                @Override
                public void onFailure(Call<List<Service>> call, Throwable t) {
                    Toast.makeText(getActivity(), "Connection Error: Failed to receive Data.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            serviceApi.getAllServices().enqueue(new Callback<List<Service>>() {
                @Override
                public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                    servicesList = response.body();

                    InListView(servicesList);
                }

                @Override
                public void onFailure(Call<List<Service>> call, Throwable t) {
                    Toast.makeText(getActivity(), "Connection Error: Failed to receive Data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void InListView(List<Service> serviceList) {
        ServiceAdapter serviceAdapter = new ServiceAdapter(getContext(),serviceList);
        recyclerView.setAdapter(serviceAdapter);
        // To set warning
        LinearLayout notServices = v.findViewById(R.id.notServices);
        if(servicesList.isEmpty()){
            notServices.setVisibility(View.VISIBLE);
        }else{
            notServices.setVisibility(View.GONE);
        }
    }
}