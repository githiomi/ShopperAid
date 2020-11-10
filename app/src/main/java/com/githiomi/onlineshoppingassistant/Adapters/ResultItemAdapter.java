package com.githiomi.onlineshoppingassistant.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.githiomi.onlineshoppingassistant.Models.Product;
import com.githiomi.onlineshoppingassistant.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultItemAdapter extends RecyclerView.Adapter<ResultItemAdapter.ResultItemViewHolder> {

//    TAG
    private static final String TAG = ResultItemAdapter.class.getSimpleName();

//    Local variables
    private List<Product> productsRetrieved;
    private Context context;

//    Constructor
public ResultItemAdapter(List<Product> products, Context contextPassed){

        this.productsRetrieved = products;
        this.context = contextPassed;

    }

    @NonNull
    @Override
    public ResultItemAdapter.ResultItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View resultView = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item_view, parent, false);
        return new ResultItemViewHolder(resultView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultItemAdapter.ResultItemViewHolder holder, int position) {
        // Binding passed result
        Product product = productsRetrieved.get(position);
        holder.bindResultToView(product);
    }

    @Override
    public int getItemCount() {
        return productsRetrieved.size();
    }

    public class ResultItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Image constants
        private static final int MAX_WIDTH = 300;
        private static final int MAX_HEIGHT = 300;

        // Widgets
        @BindView(R.id.productImageView) ImageView wProductImage;
        @BindView(R.id.productName) TextView wProductName;
        @BindView(R.id.productPrice) TextView wProductPrice;
        @BindView(R.id.productRating) TextView wProductRating;

        public ResultItemViewHolder(@NonNull View itemView) {
            super(itemView);

            // Click listener
            itemView.setOnClickListener(this);

            // Binding widgets
            ButterKnife.bind(this, itemView);

        }

        public void bindResultToView(Product product) {

            Picasso.get().load(product.getImageUrl())
                            .resize(MAX_WIDTH, MAX_HEIGHT)
                            .centerCrop()
                            .error(R.drawable.no_image)
                            .into(wProductImage);

            wProductName.setText(product.getName());
            wProductPrice.setText(product.getPrice());
            wProductRating.setText(product.getRating());

        }

        // What happens when a single item is clicked
        @Override
        public void onClick(View v) {

            String productName = productsRetrieved.get(getAdapterPosition()).getName().toString();
            Toast.makeText(context, productName, Toast.LENGTH_SHORT).show();

        }
    }
}