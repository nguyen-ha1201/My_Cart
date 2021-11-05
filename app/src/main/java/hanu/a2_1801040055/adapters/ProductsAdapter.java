package hanu.a2_1801040055.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hanu.a2_1801040055.R;
import hanu.a2_1801040055.db.ProductsManager;
import hanu.a2_1801040055.models.Product;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.StoreHolder> implements Filterable {
    List<Product> products;
    List<Product> searchedProducts;
    ProductsManager productsManager;

    public ProductsAdapter(List<Product> products) {
        this.products = products;
        this.searchedProducts = products;
    }

    @NonNull
    @Override
    public ProductsAdapter.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Activity to display
        Context context = parent.getContext();

        //xml to java object
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.item_product, parent, false);

        return new ProductsAdapter.StoreHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.StoreHolder holder, int position) {
        Product product = searchedProducts.get(position);

        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return searchedProducts.size();
    }


    public class StoreHolder extends RecyclerView.ViewHolder {
        private TextView productName, productPrice;
        private ImageButton btnAddToCart;
        private ImageView productThumbnail;
        private Context context;

        public StoreHolder(@NonNull View itemView, Context context) {
            super(itemView);
            productName = itemView.findViewById(R.id.name);
            productPrice = itemView.findViewById(R.id.price);
            productThumbnail = itemView.findViewById(R.id.thumbnail);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);

            this.context = context;
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            productPrice.setText(product.getUnitPrice() + " VND");

            btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productsManager = ProductsManager.getInstance(context);
                    boolean isAdded = false;
                    boolean isUpdated = false;
                    Product productDb = productsManager.findProductById(product.getId());
                    if(productDb == null){
                        product.increaseQuantity();
                        isAdded = productsManager.addProduct(product);
                    }else{
                        productDb.increaseQuantity();
                        isUpdated = productsManager.updateProduct(productDb);
                    }

                    if(isAdded || isUpdated){
                        Toast.makeText(context, "Added product to your cart", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "Added fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ImageLoader task = new ImageLoader();
            task.execute(product.getThumbnail());
        }


        public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
            URL image_url;
            HttpURLConnection urlConnection;


            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    image_url = new URL(strings[0]);
                    urlConnection = (HttpURLConnection) image_url.openConnection();
                    urlConnection.connect();

                    InputStream is = urlConnection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    return bitmap;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                productThumbnail.setImageBitmap(bitmap);
            }
        }

    }

    //Search Handler
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String key = charSequence.toString();
                if (key.isEmpty()) {
                    searchedProducts = products;
                } else {
                    List<Product> listSearched = new ArrayList<>();
                    for (Product row : products) {
                        if (row.getName().toLowerCase().contains(key.toLowerCase())) {
                            listSearched.add(row);
                        }
                    }

                    searchedProducts = listSearched;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = searchedProducts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                searchedProducts = (List<Product>) results.values;

                for (Product item :
                        searchedProducts) {
                }

                notifyDataSetChanged();
            }
        };
    }
}
