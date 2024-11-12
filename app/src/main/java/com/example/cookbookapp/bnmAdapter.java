package com.example.cookbookapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class bnmAdapter extends FragmentStateAdapter {

    public bnmAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new RecipePageFragment();
            case 1:
                return new ProfilePageFragment();
            default:
                return new RecipePageFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
