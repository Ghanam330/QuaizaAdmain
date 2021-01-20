package com.example.quaizappadmain;

public class CategoryModel {
    private String name;
    private String url;
   private String Key;
    private int sets;




    public CategoryModel() {
    }

    public CategoryModel(String name, int sets, String url,String Key) {
        this.name = name;
        this.Key=Key;
        this.url = url;
        this.sets = sets;
    }

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

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}



