package com.singlesoft.repaircon.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.adapter.ServiceAdapter;
import com.singlesoft.repaircon.models.Customer;
import com.singlesoft.repaircon.models.JwtPayload;
import com.singlesoft.repaircon.models.Service;
import com.singlesoft.repaircon.retrofit.CustomerApi;
import com.singlesoft.repaircon.retrofit.RetrofitService;
import com.singlesoft.repaircon.retrofit.ServiceApi;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class customerPage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Customer customer;
    private String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page);

        // To Get Token from Prefs
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");
        // To Create a Payload object from Token string and get user id
        String[] jwtParts = tokenString.split("\\.");
        String jwtPayload = new String(Base64.decode(jwtParts[1], Base64.DEFAULT));
        Gson gson = new Gson();
        JwtPayload payload = gson.fromJson(jwtPayload, JwtPayload.class);
        userType = payload.getUserType();

        // To set back to parent activity button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recycleServicesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(customerPage.this));

        //long customer_id = getIntent().getLongExtra("customer_id", 1);
        customer = (Customer) getIntent().getSerializableExtra("customer");

        MaterialButton addService = findViewById(R.id.addServiceButton);
        addService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(customerPage.this, com.singlesoft.repaircon.activitys.addService.class);
                //intent.putExtra("customer_id", customer.getId());
                intent.putExtra("customer", customer);
                startActivity(intent);
            }
        });

        EditText nameField = findViewById(R.id.nameEditText);
        EditText contactField = findViewById(R.id.contactEditText);
        EditText addressField = findViewById(R.id.addressEditText);
        if(!userType.equals("ADMIN")){
            nameField.setFocusable(false);
            nameField.setFocusableInTouchMode(false);
        }
        //nameField.setText(customer.getName());
        nameField.setText(customer.getName());
        contactField.setText(customer.getContact());
        addressField.setText(customer.getAddress());
        //contactField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        loadData();
        sendData(nameField, contactField,addressField);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_page, menu);

        MenuItem cameraItem = menu.findItem(R.id.cameraId);
        cameraItem.setVisible(false);
        MenuItem binItem = menu.findItem(R.id.binId);

        if(userType.equals("ADMIN")) {
            binItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(customerPage.this);
                    builder.setTitle(getString(R.string.from_delete_customer_confirm_title));
                    builder.setMessage(getString(R.string.from_delete_customer_confirm_note));

                    builder.setPositiveButton(getString(R.string.from_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do something if user clicked Yes
                            AlertDialog dialog = builder.create();
                            dialog.show();

                            SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                            String tokenString = prefs.getString("token", "");

                            RetrofitService Rservice = new RetrofitService(tokenString);
                            CustomerApi customerApi = Rservice.getRetrofit().create(CustomerApi.class);
                            customerApi.deleteCustomer(customer.getId()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    dialog.dismiss();
                                    finish();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });

                        }
                    });

                    builder.setNegativeButton(getString(R.string.from_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do something if user clicked No
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });
        } else{
            binItem.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void sendData(EditText nameField, EditText contactField, EditText addressField) {

        MaterialButton save = findViewById(R.id.saveEditCustomer);
        save.setOnClickListener(view -> {
            String name = String.valueOf(nameField.getText());
            String contact = String.valueOf(contactField.getText());
            String address = String.valueOf(addressField.getText());

            customer.setName(name);
            customer.setContact(contact);
            customer.setAddress(address);

            SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
            String tokenString = prefs.getString("token", "");

            RetrofitService Rservice = new RetrofitService(tokenString);
            CustomerApi customerApi = Rservice.getRetrofit().create(CustomerApi.class);

            customerApi.updateCustomer(customer.getId(), customer).enqueue(new Callback<Customer>() {
                @Override
                public void onResponse(Call<Customer> call, Response<Customer> response) {
                    Toast.makeText(customerPage.this, "Saved Successful!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Customer> call, Throwable t) {
                    Toast.makeText(customerPage.this, "Connection Error: Failed to receive Data.", Toast.LENGTH_SHORT).show();
                    Logger.getLogger(addCustomer.class.getName()).log(Level.SEVERE,"Error to Send",t);
                }
            });
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
    private void loadData(){
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");

        RetrofitService service = new RetrofitService(tokenString);
        ServiceApi serviceApi = service.getRetrofit().create(ServiceApi.class);
        serviceApi.getAllCustomerServices(customer.getId()).enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                InListView(response.body());
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                Toast.makeText(customerPage.this, "Connection Error: Failed to receive Data.", Toast.LENGTH_SHORT).show();
                Logger.getLogger(addCustomer.class.getName()).log(Level.SEVERE,"Error to Send",t);
            }
        });
    }
    private void InListView(List<Service> serviceList) {
        ServiceAdapter serviceAdapter = new ServiceAdapter(customerPage.this,serviceList);
        recyclerView.setAdapter(serviceAdapter);
    }
}