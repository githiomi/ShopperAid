package com.githiomi.onlineshoppingassistant.Models;

import org.parceler.Parcel;

@Parcel
public class Product {
    String Link;
    String Name;
    String Price;
    String Rating;
    String ImageUrl;

    public Product() {}

    public  Product (String link, String name, String price, String rating, String imageUrl){
        this.Link = link;
        this.Name = name;
        this.Price = price;
        this.Rating = rating;
        this.ImageUrl = imageUrl;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getRating() {
        return Rating;
    }

    public String getImageUrl() {
        return ImageUrl;
    }
}