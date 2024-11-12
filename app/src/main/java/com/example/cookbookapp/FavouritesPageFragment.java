package com.example.cookbookapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FavouritesPageFragment extends Fragment implements FavouriteHandler{

    private RecyclerView recyclerView;
    private RecipeListAdapter recipeListAdapter;
    private ArrayList<RecipeListItem> favouriteItems;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_page, container, false);

        recyclerView = view.findViewById(R.id.recipesrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize
        sharedPreferences = getActivity().getSharedPreferences("recipe_prefs", Context.MODE_PRIVATE);

        //add items here
        favouriteItems = new ArrayList<>();
        recipeListAdapter = new RecipeListAdapter(getContext(), favouriteItems, this);
        recyclerView.setAdapter(recipeListAdapter);

        refreshData();

        return view;
    }

    @Override
    public void saveFavourite(String id, boolean isFavourite) {
        Log.d("TAG2", "saveFavourite: " + id);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(id, isFavourite);
        editor.apply();

        for (RecipeListItem item : favouriteItems) {
            if (item.getId().equals(id)) {
                if (!isFavourite) {
                    favouriteItems.remove(item);
                }
                break;
            }
        }
        favouriteItems.sort((item1, item2) -> Boolean.compare(item2.isFavourite(), item1.isFavourite()));

        recipeListAdapter.notifyDataSetChanged();
    }

    public void refreshData(){
        new FetchRecipeList().execute("https://www.themealdb.com/api/json/v1/1/filter.php?a=Malaysian");
    }

    private class FetchRecipeList extends AsyncTask<String, Void, ArrayList<RecipeListItem>> {

        @Override
        protected ArrayList<RecipeListItem> doInBackground(String... urls) {
            ArrayList<RecipeListItem> recipeListItems = new ArrayList<>();
            try {
                Log.d("TAG", "Trying");
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                conn.disconnect();

                JSONObject jsonObject = new JSONObject(content.toString());

                JSONArray resultsArray = jsonObject.getJSONArray("meals");

                if(resultsArray.length() != 0){
                    for(int i = 0; i < resultsArray.length(); i++){
                        JSONObject result = resultsArray.getJSONObject(i);
                        RecipeListItem item = new RecipeListItem();

                        item.setName(result.getString("strMeal"));
                        item.setId(result.getString("idMeal"));
                        item.setUrl(result.getString("strMealThumb"));

                        item.setFavourite(sharedPreferences.getBoolean(item.getId(), false));
                        Log.d("TAG2", "Set favourite for " + item.getName() + " " + item.getId() + " " + item.isFavourite());

                        if(item.isFavourite()){
                            recipeListItems.add(item);
                        }
                    }
                    Log.d("TAG", "Adding complete");
                } else {
                    Log.d("TAG", "Results empty");
                }

            } catch (Exception e) {
                Log.d("TAG", "Error " + e);
            }

            return recipeListItems;
        }


        @Override
        protected void onPostExecute(ArrayList<RecipeListItem> items) {

            if (items != null && !items.isEmpty()) {
                Log.d("TAG", "List output");
                favouriteItems.clear();
                favouriteItems.addAll(items);

                favouriteItems.sort((item1, item2) -> Boolean.compare(item2.isFavourite(), item1.isFavourite()));

                recipeListAdapter.notifyDataSetChanged();
            }
            else {
                Log.d("TAG", "List empty");
            }
        }
    }

    public void onFragmentScrolledToView() {
        refreshData();
        Log.d("FavoritesFragment", "Favorites Fragment is now visible!");
    }
}
