package hanu.a2_1801040055.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.List;

import hanu.a2_1801040055.models.Product;

public class ProductsManager {

    private static ProductsManager instance;
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private static final String INSERT_STM = "INSERT INTO " +
            DbSchema.OrderTable.NAME +"(" + DbSchema.OrderTable.Cols.PRODUCT_ID +"," +
                                            DbSchema.OrderTable.Cols.PRODUCT_NAME +","+
                                            DbSchema.OrderTable.Cols.THUMBNAIL +","+
                                            DbSchema.OrderTable.Cols.UNIT_PRICE +","+
                                            DbSchema.OrderTable.Cols.QUANTITY +
        ") VALUES (?, ?, ?,  ?, ?)";

    public static ProductsManager getInstance(Context context){
        if(instance == null){
             instance = new ProductsManager(context);
        }
        return instance;
    }

    public ProductsManager(Context context){
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public List<Product> getAllProducts(){
        String sql = "SELECT * FROM " + DbSchema.OrderTable.NAME;
        Cursor cursor = db.rawQuery(sql, null);
        ProductsCursorWrapper cursorWrapper = new ProductsCursorWrapper(cursor);
        
        return cursorWrapper.getProducts();
    }

    public boolean addProduct(Product product){
        SQLiteStatement stm = db.compileStatement(INSERT_STM);
        stm.bindLong(1, product.getId());
        stm.bindString(2, product.getName());
        stm.bindString(3, product.getThumbnail());
        stm.bindDouble(4, product.getUnitPrice());
        stm.bindString(5, product.getQuantity() +"");

        long id = stm.executeInsert();
         if(id > 0){
             return true;
         }else{
             return false;
         }

    }

    public boolean deleteProduct(long productId){
        int result = db.delete(DbSchema.OrderTable.NAME,
                DbSchema.OrderTable.Cols.PRODUCT_ID + "= ?", new String[]{productId+""} );

        return result> 0;
    }


    public boolean updateProduct(Product product){

        ContentValues cv = new ContentValues();
        cv.put(DbSchema.OrderTable.Cols.PRODUCT_ID, product.getId());
        cv.put(DbSchema.OrderTable.Cols.PRODUCT_NAME, product.getName());
        cv.put(DbSchema.OrderTable.Cols.THUMBNAIL, product.getThumbnail());
        cv.put(DbSchema.OrderTable.Cols.UNIT_PRICE, product.getUnitPrice());
        cv.put(DbSchema.OrderTable.Cols.QUANTITY, product.getQuantity());

        int result = db.update(DbSchema.OrderTable.NAME, cv,DbSchema.OrderTable.Cols.PRODUCT_ID + "= ?", new String[]{product.getId()+""});
        return result > 0;
    }

    public Product findProductById(long productId){
        String sql = "SELECT * FROM " + DbSchema.OrderTable.NAME + " WHERE "+ DbSchema.OrderTable.Cols.PRODUCT_ID+ " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{productId+""});

        ProductsCursorWrapper cursorWrapper = new ProductsCursorWrapper(cursor);

        return cursorWrapper.getProductByID();
    }

    public double getTotalPrice(){
        String sql = "SELECT SUM("+ DbSchema.OrderTable.Cols.QUANTITY +" * "  +DbSchema.OrderTable.Cols.UNIT_PRICE+  ") AS total FROM " + DbSchema.OrderTable.NAME;
        Cursor cursor =db.rawQuery(sql, null);

        double total = 0;
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            total = cursor.getDouble(0);
        }

        total = cursor.getDouble(0);
        return total;
    }
}
