package com.githiomi.onlineshoppingassistant.Adapters.Firebase;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.Models.RecentSearch;
import com.githiomi.onlineshoppingassistant.R;
import com.githiomi.onlineshoppingassistant.Ui.ResultsActivity;
import com.githiomi.onlineshoppingassistant.Ui.SearchActivity;
import com.githiomi.onlineshoppingassistant.Utils.ItemSwipeHelperAdapter;
import com.google.firebase.database.DatabaseReference;

public class RecentAdapter extends FirebaseRecyclerAdapter<RecentSearch, RecentViewHolder> implements ItemSwipeHelperAdapter {

    // Local variables
    private DatabaseReference databaseReference;
    private Context context;

    public RecentAdapter(@NonNull FirebaseRecyclerOptions<RecentSearch> options,
                         DatabaseReference databaseReference,
                         Context context) {
        super(options);

        this.databaseReference = databaseReference;
        this.context = context;

    }


    @Override
    protected void onBindViewHolder(@NonNull RecentViewHolder recentsViewHolder, int i, @NonNull RecentSearch recentSearch) {

        recentsViewHolder.bindToView(recentSearch.getSearchName());

    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_search_item, parent, false);
        return new RecentViewHolder(view);

    }

    // Swipe to delete
    @Override
    public void onItemDelete(int position) {
        getRef(position).removeValue();
    }
}
