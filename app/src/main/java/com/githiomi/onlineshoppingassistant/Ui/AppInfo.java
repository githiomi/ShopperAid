package com.githiomi.onlineshoppingassistant.Ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.githiomi.onlineshoppingassistant.R;

import butterknife.BindView;

public class AppInfo extends AppCompatActivity {

    // TAG
    private static final String TAG = AppInfo.class.getSimpleName();

    // Widgets
//    @BindView(R.id.tvAppInfo) TextView wAppInfoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        // Set title label
//        String appInfoTitle = "Application Info";
//        wAppInfoTitle.setText(appInfoTitle);
    }

    // Method when back icon is clicked
//    public void backFromApp(View view) {
//        onBackPressed();
//    }

    // On back pressed function
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}