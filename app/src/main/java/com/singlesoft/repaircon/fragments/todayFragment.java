package com.singlesoft.repaircon.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
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

import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.adapter.ServiceTodayAdapter;
import com.singlesoft.repaircon.models.Service;
import com.singlesoft.repaircon.models.servicesViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class todayFragment extends Fragment {

    private View v;
    private Context mContext;
    private RecyclerView recyclerView;
    private List<Service> servicesList = new ArrayList<>();

    //private String userType;

    private servicesViewModel data;

    //public todayFragment(String userType) {
    //    this.userType = userType;
    //}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // It's important here
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
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        v = view;

        //To Shows recycleView
        recyclerView = view.findViewById(R.id.recyclerView);
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
        data = new ViewModelProvider(requireActivity()).get(servicesViewModel.class);
        todayServices(Objects.requireNonNull(data.getDataList().getValue()));

        return view;
    }
    private void todayServices(List<Service> allServices){
        List<Service> serviceListToday = new ArrayList<>();
        Calendar currentDate = Calendar.getInstance();
        Calendar timestampDate = Calendar.getInstance();
        for(Service service : allServices){
            try {
                timestampDate.setTimeInMillis(service.getPrevisionTime().getTime());

                if(currentDate.get(Calendar.YEAR) == timestampDate.get(Calendar.YEAR) && currentDate.get(Calendar.DAY_OF_YEAR) == timestampDate.get(Calendar.DAY_OF_YEAR)){
                    serviceListToday.add(service);
                }
            }catch (Exception e){}
        }
        servicesList = serviceListToday;

        InListView(servicesList);
    }
    private void InListView(List<Service> serviceList) {
        ServiceTodayAdapter serviceAdapter = new ServiceTodayAdapter(getContext(),serviceList);
        recyclerView.setAdapter(serviceAdapter);

        // To Set warning
        LinearLayout notServices = v.findViewById(R.id.nothingToday);
        if(servicesList.isEmpty()){
            notServices.setVisibility(View.VISIBLE);
        }else{
            notServices.setVisibility(View.GONE);
        }
    }
}