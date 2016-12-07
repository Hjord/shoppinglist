package net.hjord.shoppinglist.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import net.hjord.shoppinglist.Models.Product;
import net.hjord.shoppinglist.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Hjord on 15/09/2016
 */
public class ProductAdapter extends FirebaseListAdapter<Product> {


    private Set<Integer> checked;

    public Set<Integer> getChecked() {
        return checked;
    }

    public ProductAdapter(Context context, DatabaseReference dbRef) {
        super((Activity) context, Product.class, R.layout.item_product, dbRef);
        this.checked = new HashSet<>();
    }


    @Override
    protected void populateView(View v, final Product model, final int position) {

        TextView txt = (TextView) v.findViewById(R.id.prdName);
        txt.setText(model.getQuantity() + " " + model.getName());

        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox);



        if (checked.contains(position)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checked.add(position);
                }
                else{
                    checked.remove(position);
                }
            }
        });


    }
}
