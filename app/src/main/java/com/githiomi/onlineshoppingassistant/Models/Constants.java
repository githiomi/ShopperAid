package com.githiomi.onlineshoppingassistant.Models;

public class Constants {

    //    Web scraping base URLs
    // Jumia
    public static final String JUMIA_BASE_URL = "https://www.jumia.co.ke/catalog/?q=";
    public static final String JUMIA_ITEM_URL = "https://www.jumia.co.ke";
    public static final String JUMIA_PAGE_NO = "&page=";

    // Jiji
    public static final String KILIMALL_BASE_URL = "https://www.kilimall.co.ke/new/commoditysearch?q=";
    public static final String KILIMALL_PAGE_NO = "&page=";

    // Ebay
    public static final String PRE_EBAY_BASE_URL = "https://www.ebay.com/sch/i.html?_from=R40&_trksid=m570.l1313&_nkw=";
    public static final String POST_EBAY_BASE_URL = "&_sacat=0";
    public static final String EBAY_PAGE_NO = "&_pgn=";

    // Amazon
    public static final String AMAZON_DETAIL = "https://www.amazon.com";
    public static final String PRE_AMAZON_BASE_URL = "https://www.amazon.com/s?k=";
    public static final String POST_AMAZON_BASE_URL = "&ref=nb_sb_noss_1";

    //    Repetitive constants
    // For the profile
    public static final String TO_EDIT = "Edit Page Determinant";
    public static final String TO_EDIT_EMAIL = "Edit Email";
    // Camera intent
    public static final int REQUEST_IMAGE_CAPTURE = 111;
    // Currency converter
    public static final float DOLLARS_TO_KSH = 109;
    // Slide navigation
    public static final float END_SCALE = 0.7f;
    // Search key
    public static final String SEARCH_INPUT_KEY = "User Search Input";
    // To wrap a product
    public static final String WRAP_PRODUCT = "Product";
    public static final String ITEM_POSITION = "Item Position";
    // For the products
    public static final String FRAGMENT_SOURCE = "Fragment Source";
    // For the app fragments
    public static final String APP_FRAGMENT_NAME = "App Fragment";
    // Google sign in constant
    public static final int RC_SIGN_IN = 120;
    // For the theme that is active
    public static final String APP_THEME = "App Theme";

    //  API access token
    public static final String ACCESS_TOKEN = "82032c34d7d42c35c071715d76b6c951";
    public static final String CURRENCY_TO_GET = "KES";

}