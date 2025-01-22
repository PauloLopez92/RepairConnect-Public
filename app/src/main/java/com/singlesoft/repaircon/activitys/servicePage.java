package com.singlesoft.repaircon.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.adapter.imgAdapter;
import com.singlesoft.repaircon.models.BitmapStringItem;
import com.singlesoft.repaircon.models.JwtPayload;
import com.singlesoft.repaircon.models.Service;
import com.singlesoft.repaircon.retrofit.RetrofitService;
import com.singlesoft.repaircon.retrofit.ServiceApi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.Manifest;

public class servicePage extends AppCompatActivity {
    private Service service;
    private Uri image_uri;
    private TextView imgSecText;
    private String userType;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_page);

        service = (Service) getIntent().getSerializableExtra("service");

        // To set back to parent activity button
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(true);

        // To Get Token from Prefs
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");
        // To Create a Payload object from Token string and get user id
        String[] jwtParts = tokenString.split("\\.");
        String jwtPayload = new String(Base64.decode(jwtParts[1], Base64.DEFAULT));
        Gson gson = new Gson();
        JwtPayload payload = gson.fromJson(jwtPayload, JwtPayload.class);
        userType = payload.getUserType();

        // Get data from Server and set Send data Functions
        setData();
        // Try get Service images and display it
        getImages();
        // Generate QR Code at page and display it
        setQRCode();

        Executor executor = Executors.newFixedThreadPool(10);
        executor.execute(new Runnable() {
            public void run() {
                // code to be executed in parallel
                //setTime
                setTime();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkAndRequestPermissions();
    }


    // Method to set Camera Button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_page, menu);

        MenuItem cameraItem = menu.findItem(R.id.cameraId);
        MenuItem binItem = menu.findItem(R.id.binId);

        if(userType.equals("ADMIN")) {
            binItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem menuItem) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(servicePage.this);
                    builder.setTitle(getString(R.string.from_delete_service_confirm_title));
                    builder.setMessage(getString(R.string.from_delete_service_confirm_note));

                    builder.setPositiveButton(getString(R.string.from_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do something if user clicked Yes
                            AlertDialog dialog = builder.create();
                            dialog.show();

                            SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                            String tokenString = prefs.getString("token", "");

                            RetrofitService Rservice = new RetrofitService(tokenString);
                            ServiceApi serviceApi = Rservice.getRetrofit().create(ServiceApi.class);

                            serviceApi.deleteService(service.getId()).enqueue(new Callback<Void>() {
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
        cameraItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //dispatchTakePictureIntent();
                launchCamera();
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    //############################| UPLOAD |############################//

    // Method to Launch Camera
    private void launchCamera(){

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"test");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Test Image");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(camIntent, REQUEST_TAKE_PHOTO);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Image captured and saved to fileUri specified in the Intent
            sendImg(image_uri);

        }
    }

    // Helper method to get the real path of a URI
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    /*
    private void sendImg(Uri imageUri) {
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");

        RetrofitService Rservice = new RetrofitService(tokenString);
        ServiceApi serviceApi = Rservice.getRetrofit().create(ServiceApi.class);

        String filePath = getRealPathFromURI(imageUri);
        File file = new File(filePath);

        // Decode the image file into a Bitmap object
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        // Compress the bitmap
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // adjust the quality (second parameter) as per your needs
        byte[] bitmapBytes = baos.toByteArray();

        // Create a RequestBody with the compressed image bytes
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bitmapBytes);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        String filename = dateFormat.format(new Date());

        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", filename + ".jpg", requestBody);
        serviceApi.uploadFile(service.getId(), imagePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Handle response
                Toast.makeText(servicePage.this, "Saved Successful!", Toast.LENGTH_SHORT).show();
                getImages();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(servicePage.this, "Connection Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }*/
    private void sendImg(Uri imageUri) {
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");

        RetrofitService Rservice = new RetrofitService(tokenString);
        ServiceApi serviceApi = Rservice.getRetrofit().create(ServiceApi.class);

        String filePath = getRealPathFromURI(imageUri);
        File file = new File(filePath);

        // Decode the image file into a Bitmap object
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        // Define the maximum width and height
        int maxWidth = 1920;
        int maxHeight = 1920;

        // Calculate the aspect ratio of the original image
        float aspectRatio = (float) bitmap.getHeight() / bitmap.getWidth();

        // Determine whether the original image is wider or taller than the maximum dimensions
        boolean isWider = bitmap.getWidth() >= bitmap.getHeight();

        // Calculate the new width and height of the resized image based on the aspect ratio and maximum dimensions
        int newWidth = isWider ? maxWidth : (int) (maxHeight / aspectRatio);
        int newHeight = isWider ? (int) (maxWidth * aspectRatio) : maxHeight;

        // Use the new dimensions to resize the original image
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        // Compress the bitmap
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // adjust the quality (second parameter) as per your needs
        byte[] bitmapBytes = baos.toByteArray();

        // Create a RequestBody with the compressed image bytes
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bitmapBytes);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        String filename = dateFormat.format(new Date());

        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", filename + ".jpg", requestBody);
        serviceApi.uploadFile(service.getId(), imagePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Handle response
                Toast.makeText(servicePage.this, "Saved Successful!", Toast.LENGTH_SHORT).show();
                getImages();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(servicePage.this, "Connection Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Method to request camera permission
    @SuppressLint("QueryPermissionsNeeded")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, proceed with taking picture
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    launchCamera();
                }
            } else {
                System.out.println("No permission");
                // Permission denied, show a message or handle the denial gracefully
            }
        }
    }
    /*
    private void sendImg(Uri imageUri){
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");

        RetrofitService Rservice = new RetrofitService(tokenString);
        ServiceApi serviceApi = Rservice.getRetrofit().create(ServiceApi.class);

        String filePath = getRealPathFromURI(imageUri);
        File file = new File(filePath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        String randomString = dateFormat.format(currentDate);

        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", randomString+".jpg", requestBody);
        serviceApi.uploadFile(service.getId(), imagePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Handle response
                Toast.makeText(servicePage.this, "Saved Successful!", Toast.LENGTH_SHORT).show();
                getImages();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(servicePage.this, "Connection Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    */

    /*
    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            // Permission has already been granted
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                takePictureLauncher.launch(takePictureIntent);
            }
        }
    }
    // Camera Launcher
    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            // Handle the result
                            File imageFile;
                            try {
                                imageFile = getImageFileFromIntent(data);
                                sendImg(imageFile);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }


                        }
                    });


    // Method to request camera permission
    @SuppressLint("QueryPermissionsNeeded")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, proceed with taking picture
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    takePictureLauncher.launch(takePictureIntent);
                }
            } else {
                System.out.println("No permission");
                // Permission denied, show a message or handle the denial gracefully
            }
        }
    }

    // Methods to Launch camera to take a picture and send to server
    private File getImageFileFromIntent(Intent data) throws FileNotFoundException {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            try {
                File file = File.createTempFile("image", ".jpg", getCacheDir());
                FileOutputStream fos = new FileOutputStream(file);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // save as JPEG with maximum quality
                fos.flush();
                fos.close();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private void sendImgFile(File imageFile){

        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");

        RetrofitService Rservice = new RetrofitService(tokenString);
        ServiceApi serviceApi = Rservice.getRetrofit().create(ServiceApi.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");

        long currentTimeMillis = System.currentTimeMillis();

        Date currentDate = new Date(currentTimeMillis);

        //String randomString = RandomString.generate(10);
        String randomString = dateFormat.format(currentDate);

        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", randomString+".jpg", requestBody);
        serviceApi.uploadFile(service.getId(), imagePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Handle response
                Toast.makeText(servicePage.this, "Saved Successful!", Toast.LENGTH_SHORT).show();

                getImages();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(servicePage.this, "Connection Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    */

    //############################| DOWNLOAD |############################//

    // funtion to check permission
    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (!permissionsNeeded.isEmpty()) {
                requestPermissions(permissionsNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
            }
        }
    }


    /*
    // To Download Service Images as zip file
    private void getImages() {
        imgSecText = findViewById(R.id.imageSectionText);
        imgSecText.setVisibility(View.GONE);

        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");

        RetrofitService Rservice = new RetrofitService(tokenString);
        ServiceApi serviceApi = Rservice.getRetrofit().create(ServiceApi.class);

        serviceApi.downloadZip(service.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");

                    Executor executor = Executors.newFixedThreadPool(10);
                    Future<List<Bitmap>> future = ((ExecutorService) executor).submit(() -> extractImages(response.body()));
                    executor.execute(() -> {
                        try {
                            List<Bitmap> bitmaps = future.get();
                            runOnUiThread(() -> displayImages(bitmaps));
                        } catch (InterruptedException | ExecutionException e) {
                            Log.d(TAG, "failed to extract images", e);
                        }
                    });
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "download failed");
            }
        });
    }

    // Method to Extract Images from zip file at response body
    private List<Bitmap> extractImages(ResponseBody body) {
        List<Bitmap> bitmaps = new ArrayList<>();
        try {
            InputStream inputStream = body.byteStream();
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            // loop through each entry in the zip file
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                // extract the image file to a bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(zipInputStream);
                bitmaps.add(bitmap);
                // move on to the next entry
                zipEntry = zipInputStream.getNextEntry();
            }
            // close the input stream
            zipInputStream.closeEntry();
            zipInputStream.close();
            return bitmaps;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    */

    // To Get Image names
    public void getImages() {
        imgSecText = findViewById(R.id.imageSectionText);

        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");

        RetrofitService Rservice = new RetrofitService(tokenString);
        ServiceApi serviceApi = Rservice.getRetrofit().create(ServiceApi.class);

        serviceApi.getFileNames(service.getId()).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> imageNames = response.body();

                    Executor executor = Executors.newFixedThreadPool(10);
                    executor.execute(new Runnable() {
                        public void run() {
                            // code to be executed in parallel
                            assert imageNames != null;
                            getImage(serviceApi , imageNames);
                        }
                    });
                } else {
                    System.out.println("NO IMAGES");
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

                System.out.println("NO response");
            }
        });
    }

    private void getImage(ServiceApi serviceApi, List<String> filenames) {
        //List<Bitmap> bitmaps = new ArrayList<>();
        List<BitmapStringItem> bitmaps = new ArrayList<>();
        for(String img : filenames) {
            System.out.println("IMG:"+img);
            serviceApi.getImage(service.getId(),img).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        Executor executor = Executors.newFixedThreadPool(10);
                        executor.execute(() -> {
                            InputStream inputStream = response.body().byteStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            //bitmaps.add(bitmap);
                            bitmaps.add(new BitmapStringItem(bitmap,img,service.getId()));
                            runOnUiThread(() -> displayImages(bitmaps));
                        });
                    } else {
                        // Handle response error
                        System.out.println("NO BODY");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle failure
                    System.out.println("NO RESPONSE");
                }
            });
        }
    }

    // Method to Display Images
    private void displayImages(List<BitmapStringItem> bitmaps) {
        if(!bitmaps.isEmpty()){
            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            imgAdapter imgAdapter = new imgAdapter(servicePage.this,bitmaps);
            recyclerView.setLayoutManager(new GridLayoutManager(servicePage.this,3));
            imgSecText.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(imgAdapter);
        }
    }
    /*
    // Method to Display Images
    private void displayImages(List<Bitmap> bitmaps) {
        if(!bitmaps.isEmpty()){
            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            imgAdapter imgAdapter = new imgAdapter(bitmaps);
            recyclerView.setLayoutManager(new GridLayoutManager(servicePage.this,3));
            imgSecText.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(imgAdapter);
        }
    }
    */
    // Method to set date and time prevision timestamp
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

                DatePickerDialog dialog = new DatePickerDialog(servicePage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        dateField.setText(String.format("%02d/%02d/%04d", d, m+1, y));
                        try {
                            // create a Calendar object and set its time to the selected date
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(service.getPrevisionTime().getTime());
                            calendar.set(Calendar.YEAR, y);
                            calendar.set(Calendar.MONTH, m);
                            calendar.set(Calendar.DAY_OF_MONTH, d);
                            // get the Timestamp object for the selected date and time
                            Timestamp previsionTime = new Timestamp(calendar.getTimeInMillis());
                            // set the previsionTime for the service object
                            service.setPrevisionTime(previsionTime);
                            System.out.println(previsionTime);
                        }catch (Exception e){
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

                TimePickerDialog dialog = new TimePickerDialog(servicePage.this, new TimePickerDialog.OnTimeSetListener() {
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

    // Method to get Service data and send updated data
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setData(){
        // Service Attributes fields declarations
        EditText modelField = findViewById(R.id.modelText);
        EditText tagField = findViewById(R.id.tagText);
        EditText descriptionField = findViewById(R.id.currentDescription);
        EditText payedField = findViewById(R.id.currentPay);
        EditText partCostField = findViewById(R.id.currentPartCost);
        EditText laborTaxField = findViewById(R.id.currentLaborTax);
        EditText discountField = findViewById(R.id.currentDiscount);
        TextView finalPriceField = findViewById(R.id.finalPriceText);
        TextView dateField = findViewById(R.id.dateText);
        TextView timeField = findViewById(R.id.hourText);

        // Customer Attributes fields declarations
        TextView customerName = findViewById(R.id.customerNameTextView);
        TextView customerContact = findViewById(R.id.customerContactTextView);
        TextView customerNumberServices = findViewById(R.id.customerServicesNumberTextView);
        LinearLayout customerFrame = findViewById(R.id.customerFrame);

        if(userType.equals("ADMIN")){
            // User Attributes fields declarations
            TextView userName = findViewById(R.id.userNameTextView);
            TextView userType = findViewById(R.id.userTypeTextView);
            TextView userNumberServices = findViewById(R.id.userServicesNumberTextView);
            LinearLayout userFrame = findViewById(R.id.userFrame);

            // User Attributes fields SetText
            userName.setText(service.getUser().getName());
            userNumberServices.setText(String.valueOf(service.getUser().getNumServices()));
            if(service.getUser().getUserType().equals("ADMIN")) {
                userType.setText(getString(R.string.from_admin));
            }else {
                userType.setText(getString(R.string.from_user));
            }

            // Customer Frame setOnClick
            userFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), userPage.class);
                    intent.putExtra("user", service.getUser());
                    v.getContext().startActivity(intent);
                }
            });
        }else {
            // Remove user Section
            LinearLayout userFrame = findViewById(R.id.userFrame);
            userFrame.setVisibility(View.GONE);
            // Set Model and tag fields not editable
            modelField.setFocusable(false);
            modelField.setFocusableInTouchMode(false);
            tagField.setFocusable(false);
            tagField.setFocusableInTouchMode(false);
        }

        // Service Attributes fields SetText
        modelField.setText(service.getModel());
        tagField.setText(service.getTag());
        descriptionField.setText(service.getDescription());
        payedField.setText(String.format("%.2f", service.getPayed()));
        //partCostField.setText(String.valueOf(service.getPartCost()));
        partCostField.setText(String.format("%.2f", service.getPartCost()));
        laborTaxField.setText(String.format("%.2f", service.getLaborTax()));
        discountField.setText(String.format("%.2f", service.getDiscount()));
        finalPriceField.setText(format(service.getFinalPrice()));

        // To try get and format date and time
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormat.format(service.getPrevisionTime());
            dateField.setText(date);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String time = timeFormat.format(service.getPrevisionTime());
            timeField.setText(time);
        }catch (Exception e){}

        // Customer Attributes fields SetText
        customerName.setText(service.getCustomer().getName());
        customerContact.setText(service.getCustomer().getContact());
        customerNumberServices.setText(String.valueOf(service.getCustomer().getNumServices()));

        // Customer Frame setOnClick
        customerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), customerPage.class);
                intent.putExtra("customer", service.getCustomer());
                v.getContext().startActivity(intent);
            }
        });

        // Status Spinner Setup
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
        // Final Price Set Preview
        finalPrice(partCostField, laborTaxField, discountField, finalPriceField);

        // Send attributes settings
        MaterialButton save = findViewById(R.id.saveEditService);
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

            // Set values at object to send
            service.setModel(model);
            service.setTag(tag);
            service.setDescription(description);
            service.setPayed(payed);
            service.setPartCost(partCost);
            service.setLaborTax(laborTax);
            service.setDiscount(discount);

            SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
            String tokenString = prefs.getString("token", "");

            RetrofitService Rservice = new RetrofitService(tokenString);
            ServiceApi serviceApi = Rservice.getRetrofit().create(ServiceApi.class);
            serviceApi.updateService(service.getId(),service).enqueue(new Callback<Service>() {
                @Override
                public void onResponse(@NonNull Call<Service> call, @NonNull Response<Service> response) {
                    Toast.makeText(servicePage.this, getString(R.string.from_successful_save), Toast.LENGTH_SHORT).show();
                    //assert response.body() != null;
                    //System.out.println("!!!!!!"+response.body().toString());
                }
                @Override
                public void onFailure(@NonNull Call<Service> call, @NonNull Throwable t) {
                    Toast.makeText(servicePage.this, getString(R.string.from_connection_error_receive), Toast.LENGTH_SHORT).show();
                }
            });
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
        //result.setText(getString(R.string.from_money)+String.valueOf(sum));
        result.setText(getString(R.string.from_money)+String.format("%.2f", sum));
    }
    // Set QRCode methods
    private void setQRCode(){
        String service_id = service.getId();
        TextView linkPageField = findViewById(R.id.linkPage);
        String link = getString(R.string.from_web)+service_id;
        linkPageField.setText(link);
        genQRCode(link);
    }
    private void genQRCode(String link){
        // Generate QR Code
        Bitmap qrCodeBitmap = generateQRCode(link);
        // Display the QR code image in an ImageView using the Glide library
        ImageView qrCodeImageView = findViewById(R.id.qr_code_image_view);
        qrCodeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", link);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(servicePage.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        Glide.with(servicePage.this)
                .load(qrCodeBitmap)
                .into(qrCodeImageView);
    }
    private Bitmap generateQRCode(String data) {
        try {
            // Encode the data as a QR code image
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
            // Convert the bit matrix to a bitmap image
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String format(BigDecimal value) {
        Locale ptBr = new Locale("pt", "BR");
        NumberFormat currencyFormat = DecimalFormat.getCurrencyInstance(ptBr);
        return currencyFormat.format(value);
    }
    public String formatCurrency(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat format = new DecimalFormat("#,##0.00", symbols);
        return format.format(amount);
    }
    public static String normalizeCurrencyString(String currencyString) {
        // Remove all non-numeric characters and replace comma with dot
        return currencyString.replaceAll("[^\\d,]", "").replace(',', '.');
    }
}
