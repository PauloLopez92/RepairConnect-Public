package com.singlesoft.repaircon.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.adapter.ServiceAdapter;
import com.singlesoft.repaircon.models.Customer;
import com.singlesoft.repaircon.models.JwtPayload;
import com.singlesoft.repaircon.models.Service;
import com.singlesoft.repaircon.models.User;
import com.singlesoft.repaircon.retrofit.CustomerApi;
import com.singlesoft.repaircon.retrofit.RetrofitService;
import com.singlesoft.repaircon.retrofit.ServiceApi;
import com.singlesoft.repaircon.retrofit.UserApi;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class userPage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private User user;
    private long userid;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_page, menu);

        MenuItem cameraItem = menu.findItem(R.id.cameraId);
        cameraItem.setVisible(false);
        MenuItem binItem = menu.findItem(R.id.binId);

        if(!user.getUserType().equals("ADMIN") || userid == user.getId()) {
            binItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(userPage.this);
                    builder.setTitle(getString(R.string.from_delete_user_confirm_title));
                    builder.setMessage(getString(R.string.from_delete_user_confirm_note));

                    builder.setPositiveButton(getString(R.string.from_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do something if user clicked Yes
                            AlertDialog dialog = builder.create();
                            dialog.show();

                            SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                            String tokenString = prefs.getString("token", "");

                            RetrofitService Rservice = new RetrofitService(tokenString);
                            UserApi userApi = Rservice.getRetrofit().create(UserApi.class);

                            userApi.deleteUser(user.getId()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Toast.makeText(userPage.this, getString(R.string.from_processing_request), Toast.LENGTH_SHORT).show();
                                    if(userid == user.getId()){
                                        Intent intent = new Intent(userPage.this, loginPage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // To Get Token from Prefs
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");
        // To Create a Payload object from Token string and get user id
        String[] jwtParts = tokenString.split("\\.");
        String jwtPayload = new String(Base64.decode(jwtParts[1], Base64.DEFAULT));
        Gson gson = new Gson();
        JwtPayload payload = gson.fromJson(jwtPayload, JwtPayload.class);
        userid = payload.getUserId();

        // To set back to parent activity button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycleServicesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(userPage.this));

        user = (User) getIntent().getSerializableExtra("user");

        EditText nameField = findViewById(R.id.nameEditText);
        EditText passField = findViewById(R.id.passEditText);
        EditText passCheckField = findViewById(R.id.confirmPassEditText);

        Button addServiceAs = findViewById(R.id.addServiceButton);

        nameField.setText(user.getName());

        LinearLayout passlay = findViewById(R.id.passLayout);
        LinearLayout confirmlay = findViewById(R.id.passCheckLayout);
        LinearLayout showPass = findViewById(R.id.showPass);

        passlay.setVisibility(View.GONE);
        confirmlay.setVisibility(View.GONE);

        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passlay.setVisibility(View.VISIBLE);
                confirmlay.setVisibility(View.VISIBLE);
                showPass.setVisibility(View.GONE);
            }
        });

        // User Permission Spinner Setup
        Spinner permissionSpinner = findViewById(R.id.userTypeSpinner);
        String[] options = {
                getString(R.string.from_admin),
                getString(R.string.from_user),
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        permissionSpinner.setAdapter(adapter);
        if(user.getUserType().equals("ADMIN")){
            permissionSpinner.setSelection(0);
        } else if(user.getUserType().equals("USER")){
            permissionSpinner.setSelection(1);
        }
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

        if (!user.getId().equals(payload.getUserId()) && user.getUserType().equals("ADMIN")){
            //LinearLayout passLayout = findViewById(R.id.passLayout);
            //LinearLayout passCheckLayout = findViewById(R.id.passCheckLayout);
            MaterialButton save = findViewById(R.id.saveUserButton);
            save.setVisibility(View.GONE);
            nameField.setFocusable(false);
            nameField.setFocusableInTouchMode(false);
            permissionSpinner.setEnabled(false);
            showPass.setVisibility(View.GONE);
            addServiceAs.setVisibility(View.GONE);
        } else {
            addServiceAs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(userPage.this, addService.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });
        }

        loadData();
        sendData(nameField, passField,passCheckField);
    }

    private void sendData(EditText nameField, EditText passField, EditText passCheckField) {
        MaterialButton save = findViewById(R.id.saveUserButton);
        save.setOnClickListener(view -> {
            String name = String.valueOf(nameField.getText());
            String pass = String.valueOf(passField.getText());
            String confirmPass = String.valueOf(passCheckField.getText());
            LinearLayout warning = findViewById(R.id.pass_do_not_match);

            if(pass.equals(confirmPass)) {
                warning.setVisibility(View.GONE);
                user.setName(name);
                user.setPassword(pass);

                SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                String tokenString = prefs.getString("token", "");

                RetrofitService Rservice = new RetrofitService(tokenString);
                UserApi customerApi = Rservice.getRetrofit().create(UserApi.class);

                customerApi.updateUser(user.getId(), user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Toast.makeText(userPage.this, getString(R.string.from_successful_save), Toast.LENGTH_SHORT).show();
                        if(userid == user.getId()){
                            Intent intent = new Intent(userPage.this, loginPage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(userPage.this, "Connection Error: Failed to receive Data.", Toast.LENGTH_SHORT).show();
                        Logger.getLogger(addCustomer.class.getName()).log(Level.SEVERE, "Error to Send", t);
                    }
                });
            }
            else{
                warning.setVisibility(View.VISIBLE);
            }
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
        serviceApi.getAllUserServices(user.getId()).enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                InListView(response.body());
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                Toast.makeText(userPage.this, "Connection Error: Failed to receive Data.", Toast.LENGTH_SHORT).show();
                Logger.getLogger(addCustomer.class.getName()).log(Level.SEVERE,"Error to Send",t);
            }
        });
    }
    private void InListView(List<Service> serviceList) {
        ServiceAdapter serviceAdapter = new ServiceAdapter(userPage.this,serviceList);
        recyclerView.setAdapter(serviceAdapter);
    }
}