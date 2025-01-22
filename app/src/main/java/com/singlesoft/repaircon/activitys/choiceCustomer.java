package com.singlesoft.repaircon.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.adapter.CustomerAdapterChoicer;
import com.singlesoft.repaircon.models.Customer;
import com.singlesoft.repaircon.retrofit.CustomerApi;
import com.singlesoft.repaircon.retrofit.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class choiceCustomer extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Customer> customerList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);

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
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_customer);
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(choiceCustomer.this, addCustomer.class);
                startActivity(intent);
            }
        });
        //To Shows recycleView
        recyclerView = findViewById(R.id.recycleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(choiceCustomer.this));

        // To load data when open the app
        loaddata();
    }
    @Override
    public void onResume() {
        super.onResume();
        loaddata();
    }
    private void loaddata(){
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
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
                        Toast.makeText(choiceCustomer.this, "Connection Error: Failed to receive Data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void putInListView(List<Customer> customerList) {
        CustomerAdapterChoicer customerAdapter =  new CustomerAdapterChoicer(customerList);
        recyclerView.setAdapter(customerAdapter);
    }
}