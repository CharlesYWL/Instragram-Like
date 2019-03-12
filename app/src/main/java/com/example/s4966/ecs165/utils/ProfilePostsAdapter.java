package com.example.s4966.ecs165.utils;

import com.example.s4966.ecs165.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.s4966.ecs165.SquareImageView;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProfilePostsAdapter extends ArrayAdapter<String> {
    private LayoutInflater layoutInflater;
    private int resource;
    private ArrayList<String> imageStoragePaths;

    public ProfilePostsAdapter(Context context, int resource, ArrayList<String> imageStoragePaths) {
        super(context, resource);
        Log.d("Profile Post Adapter", "come into constructor, size = " + Integer.toString(imageStoragePaths.size()));
        this.layoutInflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.imageStoragePaths = imageStoragePaths;
    }

    private class ViewCollection{
        SquareImageView squareImageView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewCollection viewCollection;
        if(convertView == null){
            convertView = layoutInflater.inflate(resource, parent, false);
            viewCollection = new ViewCollection();
            viewCollection.squareImageView = convertView.findViewById(R.id.grid_imageView);

            convertView.setTag(viewCollection);
        }else {
            viewCollection = (ViewCollection) convertView.getTag();
        }

        String imageStoragePath = getItem(position);
        final long TEN_MEGABYTE = 10 * 1024 * 1024;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child(imageStoragePath).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewCollection.squareImageView.setImageBitmap(bmp);
                    }
                });
            }
        });


        //TextView dummyTextView = new TextView(getContext());
        ///dummyTextView.setText(String.valueOf(position));
        //return dummyTextView;
        return convertView;
    }


}
