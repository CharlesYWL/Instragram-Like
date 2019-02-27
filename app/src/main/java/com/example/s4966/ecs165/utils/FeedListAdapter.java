package com.example.s4966.ecs165.utils;

import com.example.s4966.ecs165.R;
import com.example.s4966.ecs165.SquareImageView;
import com.example.s4966.ecs165.models.Post;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedListAdapter extends ArrayAdapter<Post> {
    private static final String TAG = "FeedListAdapter";

    private int layoutResourcesNum;
    private Context myContext;
    private DatabaseReference firebaseRef;
    private LayoutInflater myInflater;
    private String curretUserName = "";

    public FeedListAdapter(@NotNull Context context, int resource, @NotNull List<Post> posts){
        super(context, resource, posts);
        myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResourcesNum = resource;
        myContext = context;
        firebaseRef = FirebaseDatabase.getInstance().getReference();
    }

    static class PostViewCollection{
        CircleImageView profileImageView;
        TextView usernameTextView;
        SquareImageView postImageView;
        ImageView likeImageView;

        Post post;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PostViewCollection viewCollection;

        if(convertView == null){
            convertView = myInflater.inflate(layoutResourcesNum, parent, false);
            viewCollection = new PostViewCollection();

            viewCollection.usernameTextView = convertView.findViewById(R.id.post_username);
            viewCollection.postImageView = convertView.findViewById(R.id.post_image);
            viewCollection.profileImageView = convertView.findViewById(R.id.post_profile_photo);

            convertView.setTag(viewCollection);
        }else{
            viewCollection = (PostViewCollection) convertView.getTag();
        }

        viewCollection.post = getItem(position);

        // set all things ready


        return convertView;
    }
}
