package com.singlesoft.repaircon.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.models.AuthResponse;
import com.singlesoft.repaircon.models.userLogin;
import com.singlesoft.repaircon.retrofit.LoginApi;
import com.singlesoft.repaircon.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class loginPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        ImageView appLogo = findViewById(R.id.imageViewLogo);
        ImageView singleLogo = findViewById(R.id.imageViewSingleSoft);
        int size = getScreenWidth();
        setImage(appLogo,R.drawable.logo,size,size);
        setImage(singleLogo,R.drawable.singlesoft_gray,192,192);

        themeState();
        Sender();
    }

    private void setUserName(String userName){
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastUserName", userName);
        editor.apply();
    }
    private String getUserName(){
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        return prefs.getString("lastUserName", "");
    }
    // TODO set a way to crypt password before store it
    private void setUserPass(String userName){
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastUserPass", userName);
        editor.apply();
    }
    // TODO set a way to decrypt password before return it
    private String getUserPass(){
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        return prefs.getString("lastUserPass", "");
    }
    private boolean loginState(){

        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        CheckBox checkBox = findViewById(R.id.stillLogged);

        // Retrieve the checkbox state from SharedPreferences
        boolean checkboxState = prefs.getBoolean("checkboxState", false);
        checkBox.setChecked(checkboxState);

        // Set a listener to detect when the checkbox is changed
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Do something when the checkbox is checked

                } else {
                    // Do something when the checkbox is unchecked
                }

                // Save the checkbox state to SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("checkboxState", isChecked);
                editor.apply();
            }
        });

        if (checkBox.isChecked()) {
            // Do something when the checkbox is checked
            return true;
        } else {
            // Do something when the checkbox is unchecked
            return false;
        }
    }
    private void Sender(){

        EditText inputEditName = findViewById(R.id.username_edittext);
        EditText inputEditPassword = findViewById(R.id.password_edittext);
        TextView wrong = findViewById(R.id.wrong_pass_text);
        Button loginButton = findViewById(R.id.login_button);

        RetrofitService Rservice = new RetrofitService("");
        LoginApi loginApi = Rservice.getRetrofitNoauth().create(LoginApi.class);

        inputEditName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    inputEditPassword.requestFocus();
                    return true;
                }
                return false;
            }
        });
        inputEditPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    loginButton.performClick();
                    return true;
                }
                return false;
            }
        });

        if (loginState()) {
            inputEditName.setText(getUserName());
            inputEditPassword.setText(getUserPass());
        }

        loginButton.setOnClickListener(view -> {
            String name = String.valueOf(inputEditName.getText());
            String pass = String.valueOf(inputEditPassword.getText());

            if (loginState()) {
                setUserName(name);
                setUserPass(pass);
            }else {
                setUserName("");
                setUserPass("");
            }

            userLogin login = new userLogin();
            login.setName(name);
            login.setPassword(pass);

            if (!name.equals("")){
                loginApi.getToken(login).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        /*
                        System.out.println("######|On Response|######");
                        String requestUrl = call.request().url().toString();
                        // Create the request body
                        String jsonBodyString = new Gson().toJson(login);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBodyString);
                        // Combine the request URL and body into a string and print it
                        String requestString = "Request URL: " + requestUrl + "\nRequest Body: " + jsonBodyString;
                        System.out.println(requestString);
                        */
                        try {
                            AuthResponse authResponse = response.body();
                            // Set token String at Prefs
                            SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("token", authResponse.getToken());
                            editor.apply();
                            // Create New MainActivity
                            Intent intent = new Intent(loginPage.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }catch (Exception e){
                            System.out.println(e);
                            wrong.setText(getString(R.string.from_not_Login));
                            wrong.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        wrong.setText(getString(R.string.from_server_not_responds));
                        wrong.setVisibility(View.VISIBLE);
                        /*
                        System.out.println("######|On Not Response|######");
                        String requestUrl = call.request().url().toString();
                        // Create the request body
                        String jsonBodyString = new Gson().toJson(login);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBodyString);
                        // Combine the request URL and body into a string and print it
                        String requestString = "Request URL: " + requestUrl + "\nRequest Body: " + jsonBodyString;
                        System.out.println(requestString);
                        */
                    }
                });
            }
            else {
                Toast.makeText(loginPage.this, "Add a Customer name.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void themeState(){
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switchTheme = findViewById(R.id.switch_theme);

        // Retrieve the switch state from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        boolean switchState = prefs.getBoolean("switchState", false);
        switchTheme.setChecked(switchState);

        // Set a listener to detect when the switch is changed
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                // Save the switch state to SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("switchState", isChecked);
                editor.apply();
            }
        });
        if (switchTheme.isChecked()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getSystemService(loginPage.this.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
    private void setImage(ImageView imageView, int imageResId, int reqWidth, int reqHeight) {
        // Decode the image resource with the specified dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), imageResId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResId, options);

        // Set the resized bitmap to the ImageView
        imageView.setImageBitmap(bitmap);
    }

    // Calculate the sample size to reduce the image dimensions while preserving quality
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}