package com.githiomi.onlineshoppingassistant.Models;

public class Constants {

//    Web scraping base URLs
    // Jumia
    public static final String JUMIA_BASE_URL = "https://www.jumia.co.ke/catalog/?q=";
    public static final String JUMIA_ITEM_URL = "https://www.jumia.co.ke";

    // Jiji
    public static final String JIJI_BASE_URL = "https://jiji.co.ke/search?query=";

    // Ebay
    public static final String PRE_EBAY_BASE_URL = "https://www.ebay.com/sch/i.html?_from=R40&_trksid=m570.l1313&_nkw=";
    public static final String POST_EBAY_BASE_URL = "&_sacat=0";

    // Amazon
    public static final String AMAZON_DETAIL = "https://www.amazon.com";
    public static final String PRE_AMAZON_BASE_URL = "https://www.amazon.com/s?k=";
    public static final String POST_AMAZON_BASE_URL = "&ref=nb_sb_noss_1";

    // Kilimall
    public static final String KILIMALL_BASE_URL = "https://www.kilimall.co.ke/new/commoditysearch/?q=";


//    Repetitive constants
    // For the profile
    public static final String TO_EDIT = "Edit Page Determinant";
    public static final String TO_EDIT_USERNAME = "Edit Username";
    public static final String TO_EDIT_EMAIL = "Edit Email";
    //    Camera intent
    public static final int REQUEST_IMAGE_CAPTURE = 111;
    // Currency converter
    public static final float DOLLARS_TO_KSH = 100;
    // Search key
    public static final String SEARCH_INPUT_KEY = "User Search Input";
    // To wrap a product
    public static final String WRAP_PRODUCT = "Product";
    public static final String ITEM_POSITION = "Item Position";
    public static final String ITEM_URL = "Item Url";
    // For the products
    public static final String FRAGMENT_SOURCE = "Fragment Source";
}
