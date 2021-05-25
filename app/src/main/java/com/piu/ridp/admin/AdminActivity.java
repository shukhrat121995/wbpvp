package com.piu.ridp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.piu.ridp.Blog;
import com.piu.ridp.R;
import com.piu.ridp.language.BaseActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class AdminActivity extends BaseActivity {

    private RecyclerView mFeedbackList;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter adapter;

    private LinearLayoutManager mLayoutManager;

    private static int scrollY = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle(getString(R.string.administrator));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Feedback");
        mDatabase.keepSynced(true);

        mFeedbackList = (RecyclerView)findViewById(R.id.blog_list_admin);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mFeedbackList.setHasFixedSize(true);
        mFeedbackList.setItemViewCacheSize(20);
        mFeedbackList.setLayoutManager(mLayoutManager);

        LoadData();
    }


    public void LoadData(){
        Query db = mDatabase;

        FirebaseRecyclerOptions<Blog> options =
                new FirebaseRecyclerOptions.Builder<Blog>()
                        .setQuery(db, Blog.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolderAdmin>(options) {
            @NotNull
            @Override
            public BlogViewHolderAdmin onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blog_admin, parent, false);

                return new BlogViewHolderAdmin(view);
            }

            @Override
            protected void onBindViewHolder(@NotNull BlogViewHolderAdmin holder, final int position, @NotNull final Blog model) {
                // Bind the image_details object to the BlogViewHolderAdmin
                // ...

                holder.setFeedback_title(model.getFeedback_title());
                holder.setFeedback_description(model.getFeedback_description());
                holder.setImage(model.getImage());
                holder.setLocation(model.getLocation());
                holder.setDate(model.getDate());
                holder.setImageStatus(model.getAdmin());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Save Scroll Position
                        scrollY = position;
                        //StartNewActivity for admin
                        Intent intent = new Intent(AdminActivity.this, AdminEdit.class);
                        Bundle b = new Bundle();
                        b.putString("title", model.getFeedback_title());
                        b.putString("description", model.getFeedback_description());
                        b.putString("image", model.getImage());
                        b.putString("location", model.getLocation());
                        b.putString("date", model.getDate());
                        b.putString("admin_reply", model.getAdmin_reply());
                        b.putBoolean("anonymous", model.getAnonymous());
                        b.putString("feedback_id", String.valueOf(getRef(position).getKey()));
                        intent.putExtras(b);
                        startActivity(intent);

                    }
                });
            }
        };
        if(scrollY != -1)
            mFeedbackList.smoothScrollToPosition(scrollY);
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
            final Picasso picasso = Picasso.get();
            picasso.setIndicatorsEnabled(false);
            picasso
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
                            picasso
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

        public void setImageStatus(String status){
            ImageView status_image = (ImageView)mView.findViewById(R.id.image_status);
            if(status.equals("new")){
                status_image.setImageDrawable(mView.getResources().getDrawable(R.drawable.new_icon));
            } else if (status.equals("rejected")){
                status_image.setImageDrawable(mView.getResources().getDrawable(R.drawable.rejected_icon));
            } else if (status.equals("approved")){
                status_image.setImageDrawable(mView.getResources().getDrawable(R.drawable.approved_icon));
            }
        }
    }
}
