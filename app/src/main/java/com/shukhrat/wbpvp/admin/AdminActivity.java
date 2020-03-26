package com.shukhrat.wbpvp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shukhrat.wbpvp.Blog;
import com.shukhrat.wbpvp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView mFeedbackList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle("Administrator");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Feedback");
        mDatabase.keepSynced(true);

        mFeedbackList = (RecyclerView)findViewById(R.id.blog_list_admin);
        mFeedbackList.setHasFixedSize(true);
        mFeedbackList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query db = mDatabase.orderByChild("status_anonymous").equalTo("true_false");

        FirebaseRecyclerOptions<Blog> options =
                new FirebaseRecyclerOptions.Builder<Blog>()
                        .setQuery(db, Blog.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolderAdmin>(options) {
            @Override
            public BlogViewHolderAdmin onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blog_admin, parent, false);

                return new BlogViewHolderAdmin(view);
            }

            @Override
            protected void onBindViewHolder(BlogViewHolderAdmin holder, final int position, final Blog model) {
                // Bind the image_details object to the BlogViewHolderAdmin
                // ...

                holder.setFeedback_title(model.getFeedback_title());
                holder.setFeedback_description(model.getFeedback_description());
                holder.setImage(model.getImage());
                holder.setLocation(model.getLocation());
                holder.setDate(model.getDate());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //StartNewActivity for admin
                        Intent intent = new Intent(getApplicationContext(), AdminEdit.class);
                        startActivity(intent);
                    }
                });
            }
        };

        adapter.startListening();
        mFeedbackList.setAdapter(adapter);
    }

    public static class BlogViewHolderAdmin extends RecyclerView.ViewHolder{

        View mView;


        public BlogViewHolderAdmin(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFeedback_title(String title){
            TextView post_title =  (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setFeedback_description(String desc){
            TextView post_description =  (TextView) mView.findViewById(R.id.post_description);
            post_description.setText(desc);
        }

        public void setImage(final String image){
            final ImageView postImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.get()
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .fit()
                    .centerCrop()
                    .into(postImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load(image)
                                    .fit()
                                    .centerCrop()
                                    .into(postImage);
                        }
                    });
        }

        public void setLocation(String location){
            TextView post_location =  (TextView) mView.findViewById(R.id.post_location);
            post_location.setText(location);
        }

        public void setDate(String date){
            TextView post_date =  (TextView) mView.findViewById(R.id.post_date);
            post_date.setText(date);
        }
    }
}
