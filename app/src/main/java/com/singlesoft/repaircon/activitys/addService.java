package com.singlesoft.repaircon.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.models.Customer;
import com.singlesoft.repaircon.models.JwtPayload;
import com.singlesoft.repaircon.models.Service;
import com.singlesoft.repaircon.models.User;
import com.singlesoft.repaircon.retrofit.RetrofitService;
import com.singlesoft.repaircon.retrofit.ServiceApi;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class addService extends AppCompatActivity {
    private Customer customer;
    private User user;
    private final Service service = new Service();

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Handle the result here
                    Intent data = result.getData();
                    customer = (Customer) data.getSerializableExtra("customer");

                    setLayout();
                }
            });
    private void setLayout(){
        LinearLayout customerFrame = findViewById(R.id.customerFrame);
        Button choiceCustomer = findViewById(R.id.butonChoiceCustomer);

        if(customer != null) {
            choiceCustomer.setVisibility(View.GONE);
            customerFrame.setVisibility(View.VISIBLE);
            TextView customerName = findViewById(R.id.customerNameTextView);
            TextView customerContact = findViewById(R.id.customerContactTextView);
            TextView customerNumberServices = findViewById(R.id.customerServicesNumberTextView);

            customerName.setText(customer.getName());
            customerContact.setText(customer.getContact());
            customerNumberServices.setText(String.valueOf(customer.getNumServices()));

            customerFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(addService.this, com.singlesoft.repaircon.activitys.choiceCustomer.class);
                    launcher.launch(intent);
                }
            });
        }
        else {
            customerFrame.setVisibility(View.GONE);
            choiceCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(addService.this, choiceCustomer.class);
                    launcher.launch(intent);
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        // Get The Customer
        customer = (Customer) getIntent().getSerializableExtra("customer");

        user = (User) getIntent().getSerializableExtra("user");

        //To set Layout
        setLayout();

        Executor executor = Executors.newFixedThreadPool(10);
        executor.execute(new Runnable() {
            public void run() {
                // code to be executed in parallel
                //To send service Object
                send();
                //setTime
                setTime();
            }
        });

    }
    private void setTime(){
       LinearLayout showTimeLay = findViewById(R.id.showSetTime);
       LinearLayout timeLay = findViewById(R.id.timeLayout);
       timeLay.setVisibility(View.GONE);
       showTimeLay.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               timeLay.setVisibility(View.VISIBLE);
               showTimeLay.setVisibility(View.GONE);
           }
       });

       Button dataButton = findViewById(R.id.dateButton);
       Button hourButton = findViewById(R.id.hourButton);
       TextView dateField = findViewById(R.id.dateText);
       TextView timeField = findViewById(R.id.hourText);
       dataButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Calendar calendar = Calendar.getInstance(); // get current date
               int year = calendar.get(Calendar.YEAR);
               int month = calendar.get(Calendar.MONTH);
               int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

               DatePickerDialog dialog = new DatePickerDialog(addService.this, new DatePickerDialog.OnDateSetListener() {
                   @Override
                   public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                       dateField.setText(String.format("%02d/%02d/%04d", d, m+1, y));
                       // create a Calendar object and set its time to the selected date
                       Calendar calendar = Calendar.getInstance();
                       calendar.set(Calendar.YEAR, y);
                       calendar.set(Calendar.MONTH, m);
                       calendar.set(Calendar.DAY_OF_MONTH, d);
                       // get the Timestamp object for the selected date and time
                       Timestamp previsionTime = new Timestamp(calendar.getTimeInMillis());
                       // set the previsionTime for the service object
                       service.setPrevisionTime(previsionTime);
                       System.out.println(previsionTime);
                   }
               }, year, month, dayOfMonth);
               dialog.show();
           }
       });
       hourButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Calendar calendar = Calendar.getInstance(); // get current time
               int hour = calendar.get(Calendar.HOUR_OF_DAY);
               int minute = calendar.get(Calendar.MINUTE);

               TimePickerDialog dialog = new TimePickerDialog(addService.this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker timePicker, int h, int m) {
                       timeField.setText(String.format("%02d:%02d", h, m));
                       try {
                           // create a Calendar object and set its time to the selected date
                           Calendar calendar = Calendar.getInstance();
                           calendar.setTimeInMillis(service.getPrevisionTime().getTime());
                           calendar.set(Calendar.HOUR_OF_DAY,h);
                           calendar.set(Calendar.MINUTE,m);
                           // get the Timestamp object for the selected date and time
                           Timestamp previsionTime = new Timestamp(calendar.getTimeInMillis());
                           // set the previsionTime for the service object
                           service.setPrevisionTime(previsionTime);
                           System.out.println(previsionTime);
                       }catch (Exception e){
                           // create a Calendar object and set its time to the selected date
                           Calendar calendar = Calendar.getInstance();
                           calendar.set(Calendar.HOUR_OF_DAY,h);
                           calendar.set(Calendar.MINUTE,m);
                           // get the Timestamp object for the selected date and time
                           Timestamp previsionTime = new Timestamp(calendar.getTimeInMillis());
                           // set the previsionTime for the service object
                           service.setPrevisionTime(previsionTime);
                           System.out.println(previsionTime);
                       }
                   }
               }, hour, minute, true);
               dialog.show();
           }
       });
    }
    /*
    private void getDate(TextView dateField) {
        Calendar calendar = Calendar.getInstance(); // get current date
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(addService.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                dateField.setText(String.format("%02d/%02d/%04d", d, m+1, y));
                // create a Calendar object and set its time to the selected date
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, y);
                calendar.set(Calendar.MONTH, m);
                calendar.set(Calendar.DAY_OF_MONTH, d);
                // get the Timestamp object for the selected date and time
                Timestamp previsionTime = new Timestamp(calendar.getTimeInMillis());
                // set the previsionTime for the service object
                service.setPrevisionTime(previsionTime);
                System.out.println(previsionTime);
            }
        }, year, month, dayOfMonth);
        dialog.show();
    }
    private void getTime(TextView timeField) {
        Calendar calendar = Calendar.getInstance(); // get current time
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(addService.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                timeField.setText(String.format("%02d:%02d", h, m));
                try {
                    // create a Calendar object and set its time to the selected date
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(service.getPrevisionTime().getTime());
                    calendar.set(Calendar.HOUR_OF_DAY,h);
                    calendar.set(Calendar.MINUTE,m);
                    // get the Timestamp object for the selected date and time
                    Timestamp previsionTime = new Timestamp(calendar.getTimeInMillis());
                    // set the previsionTime for the service object
                    service.setPrevisionTime(previsionTime);
                    System.out.println(previsionTime);
                }catch (Exception e){
                    // create a Calendar object and set its time to the selected date
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,h);
                    calendar.set(Calendar.MINUTE,m);
                    // get the Timestamp object for the selected date and time
                    Timestamp previsionTime = new Timestamp(calendar.getTimeInMillis());
                    // set the previsionTime for the service object
                    service.setPrevisionTime(previsionTime);
                    System.out.println(previsionTime);
                }
            }
        }, hour, minute, true);
        dialog.show();
    }
    */
    @SuppressLint("DefaultLocale")
    private void send(){
        // Service Attributes fields declarations
        EditText modelField = findViewById(R.id.modelText);
        EditText tagField = findViewById(R.id.tagText);
        EditText descriptionField = findViewById(R.id.currentDescription);
        EditText payedField = findViewById(R.id.currentPay);
        EditText partCostField = findViewById(R.id.currentPartCost);
        EditText laborTaxField = findViewById(R.id.currentLaborTax);
        EditText discountField = findViewById(R.id.currentDiscount);
        TextView finalPriceField = findViewById(R.id.finalPriceText);

        // Set Default values as 0
        service.setPayed(BigDecimal.valueOf(0));
        service.setPartCost(BigDecimal.valueOf(0));
        service.setLaborTax(BigDecimal.valueOf(0));
        service.setDiscount(BigDecimal.valueOf(0));
        // Set the format to 0.00
        payedField.setText(String.format("%.2f", service.getPayed()));
        partCostField.setText(String.format("%.2f", service.getPartCost()));
        laborTaxField.setText(String.format("%.2f", service.getLaborTax()));
        discountField.setText(String.format("%.1f", service.getDiscount()));

        Spinner statusSpinner = findViewById(R.id.spinnerStatus);
        String[] options = {
                getString(R.string.from_budget),
                getString(R.string.from_authorized),
                getString(R.string.from_noFix),
                getString(R.string.from_bench),
                getString(R.string.from_finished)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        statusSpinner.setSelection(service.getStatus());
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        service.setStatus(0);
                        break;
                    case 1:
                        service.setStatus(1);
                        break;
                    case 2:
                        service.setStatus(2);
                        break;
                    case 3:
                        service.setStatus(3);
                        break;
                    case 4:
                        service.setStatus(4);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        // Payway Spinner Setup
        Spinner paywaySpinner = findViewById(R.id.paywaySpinner);
        String[] paywayOptions = {
                getString(R.string.from_cash),
                getString(R.string.from_pix),
                getString(R.string.from_credit_card_credit),
                getString(R.string.from_credit_card_debit)
                //getString(R.string.from_cheque)
        };
        ArrayAdapter<String> paywayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, paywayOptions);
        paywayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paywaySpinner.setAdapter(paywayAdapter);
        paywaySpinner.setSelection(service.getPayway()); // set the default selected option index
        paywaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // handle item selection
                switch (position) {
                    case 0:
                        service.setPayway(0);
                        break;
                    case 1:
                        service.setPayway(1);
                        break;
                    case 2:
                        service.setPayway(2);
                        break;
                    case 3:
                        service.setPayway(3);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        finalPrice(partCostField, laborTaxField, discountField, finalPriceField);
        // Send attributes settings
        MaterialButton save = findViewById(R.id.saveButton);
        save.setOnClickListener(view -> {
            // get values from edit text to send
            String model = String.valueOf(modelField.getText());
            String tag = String.valueOf(tagField.getText());
            String description = String.valueOf(descriptionField.getText());
            BigDecimal payed = BigDecimal.valueOf(0);
            BigDecimal partCost = BigDecimal.valueOf(0);
            BigDecimal laborTax = BigDecimal.valueOf(0);
            BigDecimal discount = BigDecimal.valueOf(0);

            if (!String.valueOf(payedField.getText()).equals("")) {
                payed = new BigDecimal(payedField.getText().toString());
            }
            if(!String.valueOf(partCostField.getText()).equals("")){
                partCost = new BigDecimal(partCostField.getText().toString());
            }
            if(!String.valueOf(laborTaxField.getText()).equals("")){
                laborTax = new BigDecimal(laborTaxField.getText().toString());
            }
            if(!String.valueOf(laborTaxField.getText()).equals("")) {
                discount = new BigDecimal(discountField.getText().toString());
            }

            // To Get Token from Prefs
            SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
            String tokenString = prefs.getString("token", "");
            // To Create a Payload object from Token string and get user id
            String[] jwtParts = tokenString.split("\\.");
            String jwtPayload = new String(Base64.decode(jwtParts[1], Base64.DEFAULT));
            Gson gson = new Gson();
            JwtPayload payload = gson.fromJson(jwtPayload, JwtPayload.class);
            long userId = payload.getUserId();

            // Set values at object to send
            service.setCustomer(customer);
            service.setModel(model);
            service.setTag(tag);
            service.setDescription(description);
            service.setPayed(payed);
            service.setPartCost(partCost);
            service.setLaborTax(laborTax);
            service.setDiscount(discount);

            if(user != null) {
                System.out.println("user is not null");
                service.setUser(new User(user.getId()));
            }else {
                System.out.println("user is null");
                service.setUser(new User(userId));
            }
            RetrofitService Rservice = new RetrofitService(tokenString);
            ServiceApi serviceApi = Rservice.getRetrofit().create(ServiceApi.class);

            if (!model.equals("")){
            serviceApi.saveService(service).enqueue(new Callback<Service>() {
                @Override
                public void onResponse(Call<Service> call, Response<Service> response) {
                    Toast.makeText(addService.this, "Saved Successful!", Toast.LENGTH_SHORT).show();

                    String requestUrl = call.request().url().toString();

                    // Create the request body
                    String jsonBodyString = new Gson().toJson(service);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBodyString);

                    // Combine the request URL and body into a string and print it
                    String requestString = "Request URL: " + requestUrl + "\nRequest Body: " + jsonBodyString;
                    System.out.println(requestString);

                    finish();
                }

                @Override
                public void onFailure(Call<Service> call, Throwable t) {
                    Toast.makeText(addService.this, "Connection Error: Failed to receive Data.", Toast.LENGTH_SHORT).show();
                }
            });
            }
            else {
                Toast.makeText(addService.this, "Add a Model.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    // Method to Update on Screen the current preview price
    private void finalPrice(EditText editText1, EditText editText2, EditText editText3, TextView result) {
        editText1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSumAndDiscount(editText1, editText2, editText3, result);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
        editText2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSumAndDiscount(editText1, editText2, editText3, result);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
        editText3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSumAndDiscount(editText1, editText2, editText3, result);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }
    // Second Method to Update on Screen the current preview price
    @SuppressLint("SetTextI18n")
    private void updateSumAndDiscount(EditText editText1, EditText editText2, EditText editText3, TextView result) {
        double sum = 0;
        if (!TextUtils.isEmpty(editText1.getText())) {
            sum += Double.parseDouble(editText1.getText().toString());
        }
        if (!TextUtils.isEmpty(editText2.getText())) {
            sum += Double.parseDouble(editText2.getText().toString());
        }
        if (!TextUtils.isEmpty(editText3.getText())) {
            double discountPercent = Double.parseDouble(editText3.getText().toString());
            double discountAmount = (discountPercent / 100.0) * sum;
            sum -= discountAmount;
        }
        result.setText(getString(R.string.from_money)+String.format("%.2f", sum));
    }
    public String format(BigDecimal value) {
        Locale ptBr = new Locale("pt", "BR");
        NumberFormat currencyFormat = DecimalFormat.getCurrencyInstance(ptBr);
        return currencyFormat.format(value);
    }
}