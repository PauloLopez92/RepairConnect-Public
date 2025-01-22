package com.singlesoft.repaircon.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.adapter.pagerAdapter;
import com.singlesoft.repaircon.fragments.customersFragment;
import com.singlesoft.repaircon.models.AlertExpDialogFragment;
import com.singlesoft.repaircon.models.JwtPayload;
import com.singlesoft.repaircon.fragments.servicesFragment;
import com.singlesoft.repaircon.fragments.todayFragment;
import com.singlesoft.repaircon.fragments.usersFragment;

import androidx.viewpager.widget.ViewPager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    private pagerAdapter adapter = new pagerAdapter(getSupportFragmentManager());
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.from_exit_confirm_title));
        builder.setMessage(getString(R.string.from_exit_confirm_note));

        builder.setPositiveButton(getString(R.string.from_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // exit the app
                dialogInterface.dismiss();
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.from_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss the dialog and do nothing
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        // Get the adapter from ViewPager and call notifyDataSetChanged()
        ViewPager viewPager = findViewById(R.id.view_pager);
        pagerAdapter adapter = (pagerAdapter) viewPager.getAdapter();

        assert adapter != null;

         */
        servicesFragment servicesFragment = (com.singlesoft.repaircon.fragments.servicesFragment) adapter.getItem(0);
        customersFragment customersFragment = (com.singlesoft.repaircon.fragments.customersFragment) adapter.getItem(1);

        Executor executor = Executors.newFixedThreadPool(10);
        executor.execute(() -> {
            // code to be executed in parallel
            servicesFragment.update();
            customersFragment.update();
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);

        // To Get Token from Prefs
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");
        // To Create a Payload object from Token string and get user id
        String[] jwtParts = tokenString.split("\\.");
        String jwtPayload = new String(Base64.decode(jwtParts[1], Base64.DEFAULT));
        Gson gson = new Gson();
        JwtPayload payload = gson.fromJson(jwtPayload, JwtPayload.class);

        try {
            String userType = payload.getUserType();
            if(userType.equals("USER")){
                adapter.addFragment(new servicesFragment("USER"), getString(R.string.from_services));
                adapter.addFragment(new customersFragment(), getString(R.string.from_customers));
                adapter.addFragment(new todayFragment(), getString(R.string.from_today));
            } else if (userType.equals("ADMIN")) {
                adapter.addFragment(new servicesFragment("ADMIN"), getString(R.string.from_services));
                adapter.addFragment(new customersFragment(), getString(R.string.from_customers));
                adapter.addFragment(new usersFragment(), getString(R.string.from_users));
            }else{finish();}
        }catch (Exception ignored){}

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        /*
        // To start login when token exp
        // TODO improve it
        long futureTimestamp = (payload.getExp()* 1000) - 5000;
        System.out.println(futureTimestamp);
        System.out.println(System.currentTimeMillis());

        new CountDownTimer(futureTimestamp - System.currentTimeMillis(), 1000) {
            public void onTick(long millisUntilFinished) {
                // Do nothing, just wait until the timer finishes
            }
            public void onFinish() {
                Intent intent = new Intent(MainActivity.this, loginPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
           }
        }.start();
        */
    }
}