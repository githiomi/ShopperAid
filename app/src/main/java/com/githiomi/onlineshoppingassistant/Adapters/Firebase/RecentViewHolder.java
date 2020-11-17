package com.githiomi.onlineshoppingassistant.Adapters.Firebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;
import com.githiomi.onlineshoppingassistant.Ui.ResultsActivity;

public class RecentViewHolder extends RecyclerView.ViewHolder {

//    Local variables
    private View view;
    private Context context;

    public RecentViewHolder(@NonNull View itemView) {
        super(itemView);

        this.view = itemView;
        this.context = itemView.getContext();

    }

    public void bindToView ( String recentSearch ){

        TextView tvRecentSearch = (TextView) view.findViewById(R.id.recentSearchItem);

        // Binding data to view
        tvRecentSearch.setText(recentSearch);

        tvRecentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toResultActivity = new Intent(context, ResultsActivity.class);
                toResultActivity.putExtra(Constants.SEARCH_INPUT_KEY, recentSearch);

                context.startActivity(toResultActivity);

            }
        });
    }
}
