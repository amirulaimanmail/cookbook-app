package com.example.cookbookapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewpager;
    private BottomNavigationView bottomNavigationView;
    private TextView header_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_main);

        viewpager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottomnavigationbar);
        header_tv = findViewById(R.id.header_tv);

        bnmAdapter adapter = new bnmAdapter(this);
        viewpager.setAdapter(adapter);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if(id == R.id.recipePage){
                viewpager.setCurrentItem(0);
                header_tv.setText("recipes");
                return true;
            }
            else if (id == R.id.favPage){
                viewpager.setCurrentItem(1);
                header_tv.setText("favourites");
                return true;
            }
            else if (id == R.id.profilePage){
                viewpager.setCurrentItem(2);
                header_tv.setText("my profile");
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
                callFunctionForFragment(position);
                switch (position){
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.recipePage);
                        header_tv.setText("recipes");
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.favPage);
                        header_tv.setText("favourites");
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.profilePage);
                        header_tv.setText("my profile");
                        break;
                }
            }
        });

    }

    private void callFunctionForFragment(int position) {
        // Retrieve the fragment by position and call a method on it
        if (position == 0) {
            // Fragment at position 0 (Recipe fragment)
            RecipePageFragment recipeFragment = (RecipePageFragment) getSupportFragmentManager().findFragmentByTag("f" + position);
            if (recipeFragment != null) {
                recipeFragment.onFragmentScrolledToView();
            }
        } else if (position == 1) {
            // Fragment at position 1 (Favorites fragment)
            FavouritesPageFragment favoritesFragment = (FavouritesPageFragment) getSupportFragmentManager().findFragmentByTag("f" + position);
            if (favoritesFragment != null) {
                favoritesFragment.onFragmentScrolledToView();
            }
        }
    }
}
