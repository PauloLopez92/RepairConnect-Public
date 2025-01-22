package com.singlesoft.repaircon.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.models.Customer;
import com.singlesoft.repaircon.retrofit.CustomerApi;
import com.singlesoft.repaircon.retrofit.RetrofitService;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class addCustomer extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        // Cancel button function
        MaterialButton cancelButton = findViewById(R.id.cancelCreateCustomerButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // this will finish the current activity
            }
        });

        // To set sending functions
        Sender();
    }
    private void Sender(){

        TextInputEditText inputEditName = findViewById(R.id.NameInsert);
        TextInputEditText inputEditContact = findViewById(R.id.contactInsert);
        TextInputEditText inputEditAddress = findViewById(R.id.addressInsert);
        MaterialButton SaveButton = findViewById(R.id.saveButton);

        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");

        RetrofitService Rservice = new RetrofitService(tokenString);
        CustomerApi customerApi = Rservice.getRetrofit().create(CustomerApi.class);

        SaveButton.setOnClickListener(view -> {
            String name = String.valueOf(inputEditName.getText());
            String contact = String.valueOf(inputEditContact.getText());
            String address = String.valueOf(inputEditAddress.getText());

            Customer customer = new Customer();
            customer.setName(name);
            customer.setContact(contact);
            customer.setAddress(address);

            if (!name.equals("")){
            customerApi.saveCustomer(customer)
                    .enqueue(new Callback<Customer>() {
                        @Override
                        public void onResponse(Call<Customer> call, Response<Customer> response) {
                            Toast.makeText(addCustomer.this,"Saved Successful!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Customer> call, Throwable t) {
                            Toast.makeText(addCustomer.this,"Save Failed!", Toast.LENGTH_SHORT).show();
                            Logger.getLogger(addCustomer.class.getName()).log(Level.SEVERE,"Error to Send",t);

                        }
                    });
            }
            else {
                Toast.makeText(addCustomer.this, "Add a Customer name.", Toast.LENGTH_SHORT).show();
            }

        });

    }
}