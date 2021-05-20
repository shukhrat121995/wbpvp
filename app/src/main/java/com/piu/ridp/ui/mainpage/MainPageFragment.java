package com.piu.ridp.ui.mainpage;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.piu.ridp.R;
import com.piu.ridp.ui.feedback_online.FeedbackFragment;
import com.piu.ridp.ui.news.NewsFragment;
import com.piu.ridp.ui.opendata.OpenDataFragment;

import java.util.Objects;

public class MainPageFragment extends Fragment {

    public MainPageFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_main_page, container, false);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)root.findViewById(R.id.bottom_navigation);

        /*check if it's a initial launch*/
        if(savedInstanceState == null) {
            FragmentReplacement(new NewsFragment());
            SetFragmentTitle(getResources().getString(R.string.news));
            bottomNavigationView.setSelectedItemId(R.id.id_news);
        }

        /*Bottom Navigation*/
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.id_feedback){
                    SetFragmentTitle(getResources().getString(R.string.feedback));
                    FragmentReplacement(new FeedbackFragment());
                    return true;
                }else if(item.getItemId() == R.id.id_news){
                    SetFragmentTitle(getResources().getString(R.string.news));
                    FragmentReplacement(new NewsFragment());
                    return true;
                }else if(item.getItemId() == R.id.id_open_data){
                    SetFragmentTitle(getResources().getString(R.string.open_data));
                    FragmentReplacement(new OpenDataFragment());
                    return true;
                }
                return false;
            }
        });


        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void SetFragmentTitle(String title){
        Objects.requireNonNull(getActivity()).setTitle(title);
    }

    private void FragmentReplacement(Fragment fragment){
        FragmentManager manager = getFragmentManager();
        assert manager != null;
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.coordinator_container, fragment);
        transaction.commit();
    }
}
