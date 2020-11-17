package com.githiomi.onlineshoppingassistant.Adapters.Firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.githiomi.onlineshoppingassistant.R;
import com.google.firebase.database.DatabaseReference;

public class RecentSearchesAdapter extends FirebaseRecyclerAdapter<String, RecentViewHolder> {

//    Local variables
    private DatabaseReference databaseReference;
    private Context context;

    public RecentSearchesAdapter(@NonNull FirebaseRecyclerOptions<String> recentSearches,
                                 DatabaseReference databaseReference,
                                 Context context){

        super(recentSearches);

        this.databaseReference = databaseReference.getRef();
        this.context = context;

    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_search_item, parent, false);
        return new RecentViewHolder(view);

    }

    @Override
    protected void onBindViewHolder(@NonNull RecentViewHolder viewHolder, int i, @NonNull String recentSearch) {
        viewHolder.bindToView(recentSearch);
    }
}
