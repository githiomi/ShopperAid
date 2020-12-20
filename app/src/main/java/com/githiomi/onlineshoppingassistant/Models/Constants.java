package com.githiomi.onlineshoppingassistant.Models;

import android.util.DisplayMetrics;
import android.view.Display;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class Constants {

//    Web scraping base URLs
    // Jumia
    public static final String JUMIA_BASE_URL = "https://www.jumia.co.ke/catalog/?q=";
    public static final String JUMIA_ITEM_URL = "https://www.jumia.co.ke";

    // Ebay
    public static final String PRE_EBAY_BASE_URL = "https://www.ebay.com/sch/i.html?_from=R40&_trksid=m570.l1313&_nkw=";
    public static final String POST_EBAY_BASE_URL = "&_sacat=0";

    // Amazon
    public static final String AMAZON_DETAIL = "https://www.amazon.com";
    public static final String PRE_AMAZON_BASE_URL = "https://www.amazon.com/s?k=";
    public static final String POST_AMAZON_BASE_URL = "&ref=nb_sb_noss_1";

//    Repetitive constants
    // For the profile
    public static final String TO_EDIT = "Edit Page Determinant";
    public static final String TO_EDIT_USERNAME = "Edit Username";
    public static final String TO_EDIT_EMAIL = "Edit Email";
    //    Camera intent
    public static final int REQUEST_IMAGE_CAPTURE = 111;
    // Currency converter
    public static final float DOLLARS_TO_KSH = 111;
    // Search key
    public static final String SEARCH_INPUT_KEY = "User Search Input";
    // To wrap a product
    public static final String WRAP_PRODUCT = "Product";
    public static final String ITEM_POSITION = "Item Position";
    // For the products
    public static final String FRAGMENT_SOURCE = "Fragment Source";
    // For the app fragments
    public static final String APP_FRAGMENT_NAME = "App Fragment";

}