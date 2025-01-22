package com.singlesoft.repaircon.activitys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.models.Customer;
import com.singlesoft.repaircon.models.User;
import com.singlesoft.repaircon.retrofit.CustomerApi;
import com.singlesoft.repaircon.retrofit.RetrofitService;
import com.singlesoft.repaircon.retrofit.UserApi;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class addUser extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

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

        User user = new User(0);

        EditText nameField = findViewById(R.id.nameEditText);
        EditText passField = findViewById(R.id.passEditText);
        EditText confirmPassField = findViewById(R.id.confirmPassEditText);
        MaterialButton SaveButton = findViewById(R.id.saveUserButton);


        // User Permission Spinner Setup
        Spinner permissionSpinner = findViewById(R.id.userTypeSpinner);
        String[] options = {
                getString(R.string.from_admin),
                getString(R.string.from_user),
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        permissionSpinner.setAdapter(adapter);
        permissionSpinner.setSelection(1);
        permissionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        user.setUserType("ADMIN");
                        break;
                    case 1:
                        user.setUserType("USER");
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");

        RetrofitService Rservice = new RetrofitService(tokenString);
        UserApi userApi = Rservice.getRetrofit().create(UserApi.class);

        SaveButton.setOnClickListener(view -> {
            String name = String.valueOf(nameField.getText());
            String pass = String.valueOf(passField.getText());
            String confirmPass = String.valueOf(confirmPassField.getText());

            if (!name.equals("") && !pass.equals("") && pass.equals(confirmPass)){
            user.setName(name);
            user.setPassword(pass);

            userApi.saveUser(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Toast.makeText(addUser.this, getString(R.string.from_successful_save), Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(addUser.this, getString(R.string.from_connection_error), Toast.LENGTH_SHORT).show();
                }
            });

            }else if(name.equals("")){
                LinearLayout warning = findViewById(R.id.warning);
                TextView warningText = findViewById(R.id.warningText);

                warningText.setText(getString(R.string.from_insert_username));

                warning.setVisibility(View.VISIBLE);
            }else if(pass.equals("")){
                LinearLayout warning = findViewById(R.id.warning);
                TextView warningText = findViewById(R.id.warningText);

                warningText.setText(getString(R.string.from_insert_pass));

                warning.setVisibility(View.VISIBLE);
            } else if(!pass.equals(confirmPass)){
                LinearLayout warning = findViewById(R.id.warning);
                TextView warningText = findViewById(R.id.warningText);

                warningText.setText(getString(R.string.from_pass_not_match));

                warning.setVisibility(View.VISIBLE);
            }
        });

    }
}