package com.githiomi.onlineshoppingassistant.Utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemSwipeHelperCallback extends ItemTouchHelper.Callback {

    // the item touch adapter
    private ItemSwipeHelperAdapter itemSwipeHelperAdapter;

    // Constructor
    public ItemSwipeHelperCallback(ItemSwipeHelperAdapter itemSwipeHelperAdapter) {
        this.itemSwipeHelperAdapter = itemSwipeHelperAdapter;
    }

    // To allow the swiping
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        return makeMovementFlags(0, swipeFlags);

    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        int swiped = viewHolder.getAdapterPosition();
        itemSwipeHelperAdapter.onItemDelete( swiped );

    }

}
