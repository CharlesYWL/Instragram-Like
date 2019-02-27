package com.example.s4966.ecs165;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.eschao.android.widget.elasticlistview.ElasticListView;
import com.example.s4966.ecs165.models.Post;
import com.example.s4966.ecs165.utils.FeedListAdapter;

import java.util.ArrayList;

public class HomePage extends Fragment {


    private ElasticListView mainFeedListView;
    private FeedListAdapter adapter;
    private ArrayList<com.example.s4966.ecs165.models.Post> posts;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_homepage, container, false);
        mainFeedListView = (ElasticListView) view.findViewById(R.id.main_feed_listView);


        posts = new ArrayList<>();
        posts.add(new com.example.s4966.ecs165.models.Post());
        posts.add(new com.example.s4966.ecs165.models.Post());
        posts.add(new com.example.s4966.ecs165.models.Post());
        posts.add(new com.example.s4966.ecs165.models.Post());
        mainFeedlistInit();
        displayPosts();

        return view;
    }

    private void mainFeedlistInit(){
        mainFeedListView.setHorizontalFadingEdgeEnabled(true);
        mainFeedListView.setAdapter(adapter);
        //mainFeedListView.enableLoadFooter(true).getLoadFooter().setLoadAction(LoadFooter.LoadAction.RELEASE_TO_LOAD);
        //mainFeedListView.setOnUpdateListener(this).setOnLoadListener(this);
    }

    private void displayPosts(){
        adapter = new FeedListAdapter(getActivity(), R.layout.layout_post_view, posts);
        mainFeedListView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
