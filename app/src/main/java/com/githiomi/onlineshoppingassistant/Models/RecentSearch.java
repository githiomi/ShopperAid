package com.githiomi.onlineshoppingassistant.Models;

public class RecentSearch {

    // Variables
    public String searchName;

    public RecentSearch(String searchName) {
        this.searchName = searchName;
    }

    public RecentSearch() {
    }

    // Getters and setters
    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }
}
