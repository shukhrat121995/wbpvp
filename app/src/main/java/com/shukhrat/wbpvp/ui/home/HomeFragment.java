package com.shukhrat.wbpvp.ui.home;

import android.content.Context;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shukhrat.wbpvp.Blog;
import com.shukhrat.wbpvp.R;
import com.shukhrat.wbpvp.user.UserExpandInfo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private RecyclerView mBlogList;

    private DatabaseReference mDatabase;

    private LinearLayoutManager mLayoutManager;

    private static int scrollY = -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Feedback");
        mDatabase.keepSynced(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBlogList = root.findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setItemViewCacheSize(20);
        mBlogList.setLayoutManager(mLayoutManager);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query db = mDatabase.orderByChild("status_anonymous").equalTo("true_false");

            FirebaseRecyclerOptions<Blog> options =
                    new FirebaseRecyclerOptions.Builder<Blog>()
                            .setQuery(db, Blog.class)
                            .build();

            FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(options) {
                @Override
                public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.blog_row, parent, false);

                    return new BlogViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(BlogViewHolder holder, final int position, final Blog model) {
                    // Bind the image_details object to the BlogViewHolder
                    // ...

                    holder.setFeedback_title(model.getFeedback_title());
                    holder.setFeedback_description(model.getFeedback_description());
                    holder.setImage(model.getImage());
                    holder.setLocation(model.getLocation());
                    holder.setDate(model.getDate());

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            scrollY = position;
                            //Toast.makeText(getContext(), "test", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), UserExpandInfo.class);
                            Bundle b = new Bundle();
                            b.putString("title", model.getFeedback_title());
                            b.putString("description", model.getFeedback_description());
                            b.putString("image", model.getImage());
                            b.putString("location", model.getLocation());
                            b.putString("date", model.getDate());
                            b.putString("admin_reply", model.getAdmin_reply());
                            b.putString("admin_reply_date",model.getAdmin_reply_date());
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
