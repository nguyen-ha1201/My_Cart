package hanu.a2_1801040055.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import hanu.a2_1801040055.CartActivity;
import hanu.a2_1801040055.R;
import hanu.a2_1801040055.db.ProductsManager;
import hanu.a2_1801040055.models.Product;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    List<Product> carts;
    private CartActivity cartActivity;

    public CartAdapter(List<Product> carts, CartActivity cartActivity){
        this.carts = carts;
        this.cartActivity = cartActivity;
    }


    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Activity to display
        Context context = parent.getContext();

        //XML to java Object
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.item_product_in_cart, parent, false);


        return new CartHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        Product product = carts.get(position);

        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }


    public class CartHolder extends RecyclerView.ViewHolder {
        private TextView cartProductName, cartUnitPrice, cartQuantity, cartSumPrice;
        private ImageButton decrease, increase;
        private ImageView thumbnail;
        private Context context;

        public CartHolder(@NonNull View itemView, Context context) {
            super(itemView);
            cartProductName = itemView.findViewById(R.id.name1);
            cartUnitPrice = itemView.findViewById(R.id.price1);
            cartQuantity = itemView.findViewById(R.id.quality);
            cartSumPrice = itemView.findViewById(R.id.sum);
            decrease = itemView.findViewById(R.id.decrease);
            increase = itemView.findViewById(R.id.increase);
            thumbnail = itemView.findViewById(R.id.thumnail1);
            this.context = context;
        }

        public void bind(Product product){
            cartProductName.setText(product.getName());
            cartQuantity.setText(product.getQuantity()+"");
            cartUnitPrice.setText(product.getUnitPrice()+" VND");
            cartSumPrice.setText(product.getPrice()+" VND");


            increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductsManager productsManager = ProductsManager.getInstance(context);

                    boolean isUpdated = false;
                    product.increaseQuantity();
                    isUpdated = productsManager.updateProduct(product);


                    if( isUpdated){
                        Toast.makeText(context, "Added product", Toast.LENGTH_SHORT).show();
                        cartActivity.totalPrice.setText(productsManager.getTotalPrice()+" VND");
                    }else{
                        Toast.makeText(context, "Added fail", Toast.LENGTH_SHORT).show();
                    }
                    notifyDataSetChanged();
                }

            });

            decrease.setOnClickListener(new View.OnClickListener() {
                ProductsManager productsManager = ProductsManager.getInstance(context);

                @Override
                public void onClick(View v) {
                    if(product.getQuantity() ==  1){
                        new AlertDialog.Builder(context)
                                .setTitle("Delete?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        boolean isDeleted = productsManager.deleteProduct(product.getId());
                                        if(isDeleted){
                                            carts.remove(product);
                                            cartActivity.totalPrice.setText(productsManager.getTotalPrice()+" VND");
                                            Toast.makeText(context, "Deleted product", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(context, "Deleted fail", Toast.LENGTH_SHORT).show();
                                        }
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();


                    } else {
                        product.decreseQuantity();
                        productsManager.updateProduct(product);
                        cartActivity.totalPrice.setText(productsManager.getTotalPrice()+" VND");
                        notifyDataSetChanged();
                    }


                }
            });

            ThumbnailLoader task = new ThumbnailLoader();
            task.execute(product.getThumbnail());

        }

        public class ThumbnailLoader extends AsyncTask<String, Void, Bitmap> {
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
                thumbnail.setImageBitmap(bitmap);
            }
        }

    }


}
