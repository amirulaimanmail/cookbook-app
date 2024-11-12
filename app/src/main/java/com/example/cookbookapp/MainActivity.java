package com.example.cookbookapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewpager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_main);

        viewpager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottomnavigationbar);

        bnmAdapter adapter = new bnmAdapter(this);
        viewpager.setAdapter(adapter);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if(id == R.id.recipePage){
                viewpager.setCurrentItem(0);
                return true;
            }
            else if (id == R.id.profilePage){
                viewpager.setCurrentItem(1);
                return true;
            }
            else {
                return false;
            }
        });

        viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position){
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.recipePage);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.profilePage);
                        break;
                }
            }
        });
    }
}
