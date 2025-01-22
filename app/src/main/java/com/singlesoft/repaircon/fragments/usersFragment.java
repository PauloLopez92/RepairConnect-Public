package com.singlesoft.repaircon.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.activitys.addUser;
import com.singlesoft.repaircon.adapter.UserAdapter;
import com.singlesoft.repaircon.models.User;
import com.singlesoft.repaircon.retrofit.RetrofitService;
import com.singlesoft.repaircon.retrofit.UserApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class usersFragment extends Fragment {
    private Context mContext;
    private RecyclerView recyclerView;
    private List<User> userList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // It's important here

        // To load data when open the app
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
                    List<User> userSearch = new ArrayList<>(); // create a new list for search results
                    for (User user : userList){
                        String name = user.getName();
                        if(name.toLowerCase().startsWith(newText.toLowerCase())){ // compare with case-insensitivity
                            userSearch.add(user);
                        }
                    }
                    putInListView(userSearch); // pass the search results to the function for displaying in ListView
                    return true;
                } catch (Exception ignored){}
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
        //return super.onCreateOptionsMenu(menu);
    }
    // Get Context for adapters
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers, container, false);

        // Floating add button
        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), addUser.class);
            startActivity(intent);
        });

        //To Shows recycleView
        recyclerView = view.findViewById(R.id.recycleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
    public void update() {
        // TODO use executors
        try {
            loadData();
        }catch (Exception ignored){}
    }
    private void loadData(){
        SharedPreferences prefs = mContext.getSharedPreferences("Prefs", MODE_PRIVATE);
        // Retrieve the token string
        String tokenString = prefs.getString("token", "");
        RetrofitService service = new RetrofitService(tokenString);
        UserApi userApi = service.getRetrofit().create(UserApi.class);
        userApi.getAllUsers()
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<User>> call, Response<List<User>> response) {
                        userList = response.body();
                        System.out.println(userList);
                        putInListView(userList);
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<User>> call, Throwable t) {
                        Toast.makeText(getActivity(), "Connection Error: Failed to receive Data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void putInListView(List<User> userList) {
        UserAdapter userAdapter =  new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);
    }
}