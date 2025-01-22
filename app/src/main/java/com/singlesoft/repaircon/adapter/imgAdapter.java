package com.singlesoft.repaircon.adapter;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.activitys.servicePage;
import com.singlesoft.repaircon.models.BitmapStringItem;
import com.singlesoft.repaircon.retrofit.RetrofitService;
import com.singlesoft.repaircon.retrofit.ServiceApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class imgAdapter extends RecyclerView.Adapter<imgHolder> {

    private List<BitmapStringItem> bitmapString;

    private Context context;


    public imgAdapter(Context context,List<BitmapStringItem> bitmapString) {
        this.context = context;
        this.bitmapString = bitmapString;
    }

    @NonNull
    @Override
    public imgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create a new view holder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_item, parent, false);
        return new imgHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull imgHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get the bitmap at the current position and set it on the image view
        //Bitmap bitmap = bitmaps.get(position);
        Bitmap bitmap = bitmapString.get(position).getBitmap();
        //holder.imageView.setImageBitmap(bitmap);
        holder.bind(bitmap);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Call the listener's onItemLongClick() method
                //showPopupMenu(v); //Display Options menu
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.options_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_option:
                                // Handle delete option
                                //System.out.println(bitmapString.get(position).getName());
                                //System.out.println(bitmapString.get(position).getServiceId());
                                delete(bitmapString.get(position).getServiceId(),bitmapString.get(position).getName());
                                return true;
                            case R.id.share_option:
                                // Handle share option
                                try {
                                    share(bitmapString.get(position).getBitmap());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }
    private void delete(String serviceId,String name){
        SharedPreferences prefs = context.getSharedPreferences("Prefs", MODE_PRIVATE);
        String tokenString = prefs.getString("token", "");

        RetrofitService Rservice = new RetrofitService(tokenString);
        ServiceApi serviceApi = Rservice.getRetrofit().create(ServiceApi.class);
        serviceApi.deleteImage(serviceId, name).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(context, context.getString(R.string.from_successful_delete), Toast.LENGTH_SHORT).show();
                ((servicePage) context).getImages();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, context.getString(R.string.from_connection_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void share(Bitmap bitmap) throws IOException {
        // Save the bitmap to a file
        File cachePath = new File(context.getCacheDir(), "images");
        cachePath.mkdirs();
        File imageFile = new File(cachePath, "Share_image.png");
        FileOutputStream stream = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        stream.close();

        // Create the share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Image Description", null);
        Uri uri = Uri.parse(path);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        // Launch the share dialog
        context.startActivity(Intent.createChooser(shareIntent, "Share image"));
    }
    @Override
    public int getItemCount() {
        return bitmapString.size();
    }
}