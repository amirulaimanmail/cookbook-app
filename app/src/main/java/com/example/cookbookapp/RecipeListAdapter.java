package com.example.cookbookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.recipeListViewHolder> {

    private final Context context;
    private final List<RecipeListItem> recipeListItems;
    private final FavouriteHandler favouriteHandler;

    public RecipeListAdapter(Context context, List<RecipeListItem> recipeListItems, FavouriteHandler favouriteHandler) {
        this.context = context;
        this.recipeListItems = recipeListItems;
        this.favouriteHandler = favouriteHandler;
    }

    @NonNull
    @Override
    public RecipeListAdapter.recipeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new recipeListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapter.recipeListViewHolder holder, int position) {
        RecipeListItem recipeListItem = recipeListItems.get(position);

        holder.foodName.setText(recipeListItem.getName());

        holder.favToggle.setOnCheckedChangeListener(null);
        holder.favToggle.setChecked(recipeListItem.isFavourite());

        Glide.with(context)
                .load(recipeListItem.getUrl())
                .into(holder.foodImage);

        holder.favToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recipeListItem.setFavourite(isChecked);
            favouriteHandler.saveFavourite(recipeListItem.getId(), isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return recipeListItems.size();
    }

    public class recipeListViewHolder extends RecyclerView.ViewHolder {

        ImageView foodImage;
        TextView foodName;
        CheckBox favToggle;

        public recipeListViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            favToggle = itemView.findViewById(R.id.favoriteToggle);
        }
    }

    public interface FavoriteHandler {
        void saveFavourite(String recipeId, boolean isFavourite);
    }
}
