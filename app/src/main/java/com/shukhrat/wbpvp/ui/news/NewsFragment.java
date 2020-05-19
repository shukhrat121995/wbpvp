package com.shukhrat.wbpvp.ui.news;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shukhrat.wbpvp.Blog;
import com.shukhrat.wbpvp.PostActivity;
import com.shukhrat.wbpvp.R;
import com.shukhrat.wbpvp.authentification.EnterPhoneNumber;
import com.shukhrat.wbpvp.language.LocaleManager;
import com.shukhrat.wbpvp.ui.feedback_online.FeedbackFragment;
import com.shukhrat.wbpvp.user.UserExpandInfo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class NewsFragment extends Fragment {
    private RecyclerView mBlogList;

    private DatabaseReference mDatabase;

    private LinearLayoutManager mLayoutManager;

    private static int scrollY = -1;

    public NewsFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_news, container, false);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("News").child(new LocaleManager().getLanguagePref(getContext()));
        mDatabase.keepSynced(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBlogList = root.findViewById(R.id.blog_list_news);
        mBlogList.setHasFixedSize(true);
        mBlogList.setItemViewCacheSize(20);
        mBlogList.setLayoutManager(mLayoutManager);


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<NewsModel> options =
                new FirebaseRecyclerOptions.Builder<NewsModel>()
                        .setQuery(mDatabase, NewsModel.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<NewsModel, NewsFragment.BlogViewHolder>(options) {
            @Override
            public NewsFragment.BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blog_news, parent, false);

                return new NewsFragment.BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(NewsFragment.BlogViewHolder holder, final int position, final NewsModel model) {
                // Bind the image_details object to the BlogViewHolder
                // ...

                holder.setFeedback_title(model.getNews_title());
                holder.setImage(model.getNews_image());
                holder.setDate(model.getNews_date());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scrollY = position;
                        Intent intent = new Intent(getActivity(), NewsExpandInfo.class);
                        Bundle b = new Bundle();
                        b.putString("title", model.getNews_title());
                        b.putString("description", model.getNews_description());
                        b.putString("text", model.getNews_text());
                        b.putString("image", model.getNews_image());
                        b.putString("date", model.getNews_date());
                        intent.putExtras(b);
                        startActivity(intent);

                    }
                });
            }
        };

        if(scrollY != -1)
            mBlogList.smoothScrollToPosition(scrollY);
        adapter.startListening();
        mBlogList.setAdapter(adapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;


        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFeedback_title(String title){
            TextView news_title =  (TextView) mView.findViewById(R.id.news_title);
            news_title.setText(title);
        }

        public void setImage(final String image){
            final ImageView postImage = (ImageView) mView.findViewById(R.id.news_image);
            final String url = "http://obodqishloq.geekart.club/wb/images/news_images/" + image;

            final Picasso picasso = Picasso.get();
            picasso.setIndicatorsEnabled(false);
            picasso
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .fit()
                    .centerInside()
                    .into(postImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError(Exception e) {
                            picasso
                                    .load(url)
                                    .fit()
                                    .centerInside()
                                    .into(postImage);
                        }
                    });
        }

        public void setDate(String date){
            TextView news_date =  (TextView) mView.findViewById(R.id.news_date);
            news_date.setText(date);
        }
    }
}
