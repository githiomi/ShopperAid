//package com.githiomi.onlineshoppingassistant;
//
//import android.app.Application;
//import android.util.Log;
//
//import com.githiomi.onlineshoppingassistant.Models.Constants;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//public class BaseApplication extends Application {
//
//    // TAG
//    private static final String TAG = BaseApplication.class.getSimpleName();
//
//    // This will be used to get the currency exchange rate when the app starts
//    // essential URL structure is built using constants
//    public static final String ACCESS_KEY = Constants.ACCESS_TOKEN;
//    public static final String BASE_URL = "https://api.currencylayer.com/live";
//    public static final String ENDPOINT = "live";
//
//    // this object is used for executing requests to the (REST) API
//    static CloseableHttpClient httpClient = HttpClients.createDefault();
//
//    // sendLiveRequest() function is created to request and retrieve the data
//    public static Float sendLiveRequest() throws JSONException, IOException {
//
//        // The following line initializes the HttpGet Object with the URL in order to send a request
//        HttpGet get = new HttpGet(BASE_URL + ENDPOINT + "?access_key=" + ACCESS_KEY + "&currencies=KES");
//        CloseableHttpResponse response = httpClient.execute(get);
//        HttpEntity entity = response.getEntity();
//
//        // the following line converts the JSON Response to an equivalent Java Object
//        JSONObject exchangeRates = new JSONObject(EntityUtils.toString(entity));
//
//        Log.d(TAG, "sendLiveRequest: -------- " + " Live Currency Exchange Rates");
//
//        // Parsed JSON Objects are accessed according to the JSON response's hierarchy, output strings are built
//        //Getting the actual currency
//        Double usdToKes = exchangeRates.getJSONObject("quotes").getDouble("USDKES");
//        float floatedUsdToKes = usdToKes.floatValue();
//
//        // When rate obtained, print
//        Log.d(TAG, "sendLiveRequest: Exchange rate: ----------- " + usdToKes);
//        response.close();
//
//        return floatedUsdToKes;
//    }
//
//}
