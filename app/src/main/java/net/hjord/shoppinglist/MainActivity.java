package net.hjord.shoppinglist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.hjord.shoppinglist.Models.Product;
import net.hjord.shoppinglist.Adapter.ProductAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {


    private DatabaseReference dbRef;


    //Views
    private ListView listView;
    private EditText textBox;
    private ProductAdapter adapter;
    private Spinner spinner;
    private Button deleteButton;

    private Context context;
    private Toolbar toolbar;

    //spinner select
    private int spinnerSelection = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.hjord.shoppinglist.R.layout.activity_main);
        toolbar = (Toolbar) findViewById(net.hjord.shoppinglist.R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;


        setColors();


        //Views
        textBox = (EditText) findViewById(net.hjord.shoppinglist.R.id.txtNewItem);
        listView = (ListView) findViewById(net.hjord.shoppinglist.R.id.list);
        deleteButton = (Button) findViewById(net.hjord.shoppinglist.R.id.btnDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDeleteSelected();
                adapter.notifyDataSetChanged();
            }
        });
        //Spinner
        spinner = (Spinner) findViewById(net.hjord.shoppinglist.R.id.qty_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, net.hjord.shoppinglist.R.array.qty_spinner_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);


        //DB
        final DatabaseReference main = FirebaseDatabase.getInstance().getReference().child(SingInActivity.sToken);

        //If user does not yet have a list, create one.
        main.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("members")) {
                    HashMap<String, String> users = new HashMap<String, String>();
                    users.put(SingInActivity.sToken, SingInActivity.sToken);
                    main.child("members").setValue(users);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        dbRef = main.child("items");

        //Add button
        Button addButton = (Button) findViewById(net.hjord.shoppinglist.R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product pro = new Product(textBox.getText().toString(), spinnerSelection);

                dbRef.push().setValue(pro);
                adapter.notifyDataSetChanged();
                deleteButton.setVisibility(View.VISIBLE);
            }
        });


        //Adapter
        adapter = new ProductAdapter(this, dbRef);
        listView.setAdapter(adapter);


    }


    public void onClickDeleteSelected() {

        if (adapter.getChecked().isEmpty()) {
            return;
        }

        final ArrayList<Product> toDelete = new ArrayList<>();

        for (int i : adapter.getChecked()) {
            toDelete.add(adapter.getItem(i));
            adapter.getRef(i).removeValue();
        }


        adapter.getChecked().clear();

        Snackbar.make(listView, "Items deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (Product product : toDelete) {
                            dbRef.push().setValue(product);
                        }
                        adapter.notifyDataSetChanged();
                        deleteButton.setVisibility(View.VISIBLE);
                        Snackbar.make(listView, "Items restored", Snackbar.LENGTH_SHORT).show();
                    }
                }).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(net.hjord.shoppinglist.R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Integer> arr = new ArrayList<>();

        for (int i : adapter.getChecked()) {
            arr.add(i);
        }

        outState.putIntegerArrayList("checked", arr);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);

        ArrayList<Integer> ll = savedState.getIntegerArrayList("checked");
        for (int i : ll) {
            adapter.getChecked().add(i);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_clear:
                confirmClear();
                return true;
            case R.id.action_share:
                share();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        StringBuilder toShare = new StringBuilder();
        for (int i = 0; i < adapter.getCount(); i++) {
            Product product = adapter.getItem(i);
            toShare.append(product.getQuantity() + " " + product.getName() + "\n");
        }

        intent.putExtra(Intent.EXTRA_TEXT, toShare.toString());
        startActivity(intent);

    }

    private void confirmClear() {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Do you really want to clear your list?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        for (int i = 0; i < adapter.getCount(); i++) {
                            adapter.getRef(i).removeValue();
                        }

                        adapter.notifyDataSetChanged();
                      /*  deleteButton.setVisibility(View.INVISIBLE);*/
                        Toast.makeText(context, "List deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerSelection = Integer.parseInt(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }


    private void setColors() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String strColor = prefs.getString("colorPref", "");

        if (strColor.isEmpty()) {
            return;
        }

        int color = Color.parseColor("#" + strColor);


        Log.d("COLOR", prefs.getString("colorPref", "COM"));

        toolbar.setBackgroundColor(color);


        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; //
        color = Color.HSVToColor(hsv);


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(color);

        }


    }
}
