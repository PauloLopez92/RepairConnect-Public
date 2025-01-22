package com.singlesoft.repaircon.adapter;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.activitys.userPage;


public class imgHolder extends RecyclerView.ViewHolder implements View.OnClickListener/*, View.OnLongClickListener*/  {
    public ImageView imageView;

    public imgHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);
        itemView.setOnClickListener(this);
        //itemView.setOnLongClickListener(this);
    }

    public void bind(Bitmap bitmap) {

        // Get the screen width and calculate the new height of the bitmap
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int newHeight = (int) (screenWidth * ((float) bitmap.getHeight() / bitmap.getWidth()));
        // Resize the bitmap using Bitmap.createScaledBitmap()
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, newHeight, false);
        // Set the resized bitmap on the ImageView
        imageView.setImageBitmap(resizedBitmap);
    }

    @Override
    public void onClick(View view) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        String path = MediaStore.Images.Media.insertImage(view.getContext().getContentResolver(), bitmap, "Image Description", null);
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        view.getContext().startActivity(intent);
        Toast.makeText(imageView.getContext(), R.string.from_allert_saved_picture, Toast.LENGTH_SHORT).show();
    }
    /*
    // Method to show options menu, at to adapter class now
    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_option:
                        // Handle delete option
                        return true;
                    case R.id.share_option:
                        // Handle share option
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
    */
    /*
    // This and others commented codes at this class is to work with this method
    @Override
    public boolean onLongClick(View view) {
        // handle the long click event
        Toast.makeText(view.getContext(), "Long click detected", Toast.LENGTH_SHORT).show();
        return true; // indicate that the event has been handled
    }

     */
}