package com.example.clowix;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class Login_SignUp extends Fragment {

    private static final String TAG = "Login_SignUp";
     private TabLayout tabLayout;
     private static View outView;
    public Login_SignUp() {


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getActivity()).getWindow().setStatusBarColor(getResources().getColor(R.color.black));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login__sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        outView=view;
        tabLayout=view.findViewById(R.id.tabLayout);
        ViewPager viewPager=view.findViewById(R.id.viewPager);
        FloatingActionButton google=view.findViewById(R.id.google);
        FloatingActionButton facebook=view.findViewById(R.id.facebook);
        FloatingActionButton twitter=view.findViewById(R.id.twiiter);
        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign Up"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final PagerAdapter pagerAdapter=new Adaptor(getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
       viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

       animationApplier();

       google.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               Toast.makeText(getContext(),"App is under development",Toast.LENGTH_SHORT).show();
           }
       });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(),"App is under development",Toast.LENGTH_SHORT).show();
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(),"App is under development",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void animationApplier()
    {
        tabLayout.setTranslationY(300);
        tabLayout.setAlpha(0);
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();

    }

    private static class Adaptor extends FragmentStatePagerAdapter
    {

        private final int totalCount;

        public Adaptor(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);

            this.totalCount=behavior;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position)
            {
                case 0:
                    return new Login();
                case 1:
                    return new SignUp();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return totalCount;
        }
    }

    public static View getImageView()
    {
        return outView.<ImageView>findViewById(R.id.loading);
    }
}