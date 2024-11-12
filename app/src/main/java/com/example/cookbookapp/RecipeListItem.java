package com.example.cookbookapp;

public class RecipeListItem {
    private String name;
    private String url;
    private String id;
    private boolean isFavourite = false;

    public RecipeListItem(String id, String url, String name) {
        this.isFavourite = false;
        this.id = id;
        this.url = url;
        this.name = name;
    }

    public RecipeListItem(){
        isFavourite = false;
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}
