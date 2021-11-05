package hanu.a2_1801040055;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import hanu.a2_1801040055.adapters.CartAdapter;
import hanu.a2_1801040055.db.ProductsManager;
import hanu.a2_1801040055.models.Product;

public class CartActivity extends AppCompatActivity {
    RecyclerView rvCart;
    public TextView totalPrice;
    List<Product> cartLines;
    CartAdapter adapter;
    ProductsManager productsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        productsManager = ProductsManager.getInstance(this);

        //Recycler view
        rvCart = findViewById(R.id.product_order);

        totalPrice = findViewById(R.id.money);
        totalPrice.setText(productsManager.getTotalPrice()+" VND");

        //Get data from SQLite Database
        productsManager = ProductsManager.getInstance(this);
        cartLines = productsManager.getAllProducts();


        rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartLines, this);
        rvCart.setAdapter(adapter);

    }

}